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
    def test_original_acceptance_cannot_be_waived(self):
        x=a.initial(task())
        with self.assertRaises(a.ContractError):a.record(x,"AC-1","NOT_APPLICABLE")
    def test_team_and_integrations_selectable(self):
        self.assertIn("qa_lead",a.choose(task("team","high"))["roles"])
        self.assertIn("security_reviewer",a.choose(task("integrations","high"))["roles"])
    def test_graph_updates_missing_edges(self):
        x=a.initial(task())
        with tempfile.TemporaryDirectory() as d:
            p=pathlib.Path(d)/"proof.txt";p.write_text("local evidence")
            a.record(x,"AC-1","PASS",p)
            self.assertEqual(x["graph"]["current"],["AC-2"])
            self.assertEqual(a.summary(x)["missing_gates"],["AC-2"])

    def test_learning_requires_failure_and_proof(self):
        x=a.initial(task())
        with tempfile.TemporaryDirectory() as d:
            p=pathlib.Path(d)/"regression.txt";p.write_text("repro and corrected test")
            with self.assertRaises(a.ContractError):a.propose_lesson(x,"AC-1","real bug",p)
            a.record(x,"AC-1","FAIL")
            a.propose_lesson(x,"AC-1","real bug",p)
            self.assertEqual(x["lessons"][0]["status"],"CANDIDATE_NOT_ACCEPTED")
            self.assertEqual(a.summary(x)["lesson_candidates"],1)
    def test_all_six_product_packs_present(self):
        for name in ("assistant-lite","learning-mentor","career","agent-team","engineering","integrations"):
            self.assertTrue((ROOT/"packs"/(name+".md")).is_file(),name)
if __name__=="__main__":unittest.main()

