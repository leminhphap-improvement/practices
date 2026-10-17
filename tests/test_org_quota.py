import importlib.util
import pathlib
import unittest
source=pathlib.Path(__file__).resolve().parents[1]/".cicd/org_quota.py"
spec=importlib.util.spec_from_file_location("quota",source)
q=importlib.util.module_from_spec(spec);spec.loader.exec_module(q)
known={"organizations":{"LyonPro":{"status":"EXHAUSTED","evidence":"USER_CONFIRMED"},
                       "leminhphap-improvement":{"status":"AVAILABLE","evidence":"USER_CONFIRMED"}}}
def api(overrides=None):
    data={"/repos/leminhphap-improvement/practices":({"full_name":"leminhphap-improvement/practices"},None),
    "/orgs/leminhphap-improvement/actions/permissions":({"enabled_repositories":"all"},None),
    "/organizations/leminhphap-improvement/settings/billing/budgets?per_page=100":({"budgets":[]},None),
    "/organizations/leminhphap-improvement/settings/billing/usage/summary?product=Actions":({"usageItems":[]},None)}
    data.update(overrides or {})
    return lambda path:data.get(path,(None,"UNAVAILABLE"))
class QuotaTests(unittest.TestCase):
    def test_old_org_block_no_api_probe(self):
        def no_api(x):raise AssertionError(x)
        self.assertFalse(q.classify("LyonPro",known,"practices",no_api)["allowed_to_dispatch"])
    def test_new_org_available(self):
        self.assertTrue(q.classify("leminhphap-improvement",known,"practices",api())["allowed_to_dispatch"])
    def test_billing_403_not_exhausted(self):
        path="/organizations/leminhphap-improvement/settings/billing/budgets?per_page=100"
        x=q.classify("leminhphap-improvement",known,"practices",api({path:(None,"API_UNAVAILABLE_OR_UNAUTHORIZED")}))
        self.assertEqual(x["status"],"AVAILABLE")
    def test_budget_cap_overrides_report(self):
        path="/organizations/leminhphap-improvement/settings/billing/budgets?per_page=100"
        x=q.classify("leminhphap-improvement",known,"practices",api({path:({"budgets":[{"prevent_further_usage":True,
        "budget_product_sku":"actions","budget_amount":2,"spent_amount":2}]},None)}))
        self.assertEqual(x["status"],"EXHAUSTED")
    def test_unknown_usage_not_available(self):
        self.assertEqual(q.classify("leminhphap-improvement",{}, "practices",api())["status"],"UNKNOWN")
    def test_missing_repo_cannot_move_work(self):
        self.assertFalse(q.classify("leminhphap-improvement",known,"private-repo",api())["allowed_to_dispatch"])
    def test_actions_disabled(self):
        x=q.classify("leminhphap-improvement",known,"practices",api({
           "/orgs/leminhphap-improvement/actions/permissions":({"enabled_repositories":"none"},None)}))
        self.assertFalse(x["allowed_to_dispatch"])
    def test_unverified_role_claim(self):
        pol={"organizations":{"leminhphap-improvement":{"status":"AVAILABLE","evidence":"INFERRED"}}}
        self.assertEqual(q.classify("leminhphap-improvement",pol,"practices",api())["status"],"UNKNOWN")
    def test_wrong_repo_identity(self):
        x=q.classify("leminhphap-improvement",known,"practices",api({
        "/repos/leminhphap-improvement/practices":({"full_name":"evil/other"},None)}))
        self.assertFalse(x["allowed_to_dispatch"])
    def test_user_report_scoped_not_global(self):
        self.assertEqual(q.classify("LyonPro",known)["status"],"EXHAUSTED")
        self.assertEqual(q.classify("leminhphap-improvement",known, "practices",api())["status"],"AVAILABLE")
if __name__=="__main__":unittest.main()
