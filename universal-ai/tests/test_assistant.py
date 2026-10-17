import importlib.util
import json
import pathlib
import tempfile
import unittest
ROOT=pathlib.Path(__file__).resolve().parents[1]
spec=importlib.util.spec_from_file_location("assistant",ROOT/"assistant.py")
a=importlib.util.module_from_spec(spec);spec.loader.exec_module(a)
def task(kind="short_chat",impact="low"):
    return {"kind":kind,"goal":"Solve the concrete user task","impact":impact,"acceptance":["Result correct","Evidence scoped"],"capabilities":[]}
class PortableTests(unittest.TestCase):
    def test_short_chat_never_spawns_team(self):
        x=a.choose(task());self.assertEqual(x["mode"],"SHORT_CHAT")
        self.assertEqual(x["team_mode"],"SINGLE_ASSISTANT")
        self.assertEqual(x["agent_invocations"],"NOT_EXECUTED")
    def test_high_impact_engineering_adds_specialists(self):
        x=a.choose(task("engineering","high"))
        self.assertIn("qa_lead",x["roles"]);self.assertEqual(x["effort"],"evidence_intensive")
    def test_low_learning_one_mentor(self):
        x=a.choose(task("learning"));self.assertEqual(x["roles"],["mentor"])
    def test_career_specialist_only_for_high(self):
        x=a.choose(task("career","high"));self.assertIn("domain_specialist",x["roles"])
    def test_capabilities_not_invented(self):
        self.assertEqual(a.choose(task())["tools"],[])
    def test_requires_real_goal_and_acceptance(self):
        with self.assertRaises(a.ContractError):a.initial({"kind":"short_chat","goal":"yes"})
    def test_rejects_secret_fields(self):
        x=task();x["api_key"]="sensitive"
        with self.assertRaises(a.ContractError):a.initial(x)
    def test_never_self_certify_pass(self):
        state=a.initial(task())
        with self.assertRaises(a.ContractError):a.record(state,"AC-1","PASS")
    def test_evidence_is_not_acceptance(self):
        state=a.initial(task())
        with tempfile.TemporaryDirectory() as d:
            evidence=pathlib.Path(d)/"result.txt";evidence.write_text("local test output")
            a.record(state,"AC-1","PASS",evidence)
            a.record(state,"AC-2","PASS",evidence)
            self.assertEqual(a.summary(state)["state"],"READY_FOR_INDEPENDENT_ACCEPTANCE")
            self.assertEqual(a.summary(state)["product_acceptance"],"NOT_VERIFIED")
    def test_resume_after_interruption(self):
        with tempfile.TemporaryDirectory() as d:
            file=pathlib.Path(d)/"checkpoint.json";state=a.initial(task("engineering","high"));a.atomic_save(file,state)
            self.assertEqual(a.summary(a.load_json(file))["missing_gates"],["AC-1","AC-2"])
    def test_reject_false_pass_from_tampered_json(self):
        state=a.initial(task());state["acceptance"][0]["status"]="PASS"
        with self.assertRaises(a.ContractError):a.summary(state)
    def test_graph_has_past_present_future(self):
        x=a.initial(task())
        self.assertTrue(all(k in x["graph"] for k in ("past","current","future","edges")))
    def test_reject_role_fake_invocation(self):
        x=a.initial(task());x["route"]["agent_invocations"]="COMPLETE"
        with self.assertRaises(a.ContractError):a.summary(x)
    def test_nonapplicable_is_not_unverified_pass(self):
        x=a.initial(task());a.record(x,"AC-1","NOT_APPLICABLE")
        self.assertEqual(a.summary(x)["state"],"INCOMPLETE")
if __name__=="__main__":unittest.main()
