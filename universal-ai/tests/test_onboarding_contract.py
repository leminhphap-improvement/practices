import pathlib, unittest
ROOT=pathlib.Path(__file__).resolve().parents[1]
class OnboardingContractTests(unittest.TestCase):
    def test_project_intro_under_host_limit(self):
        text=(ROOT/'PROJECT-INTRO-TEMPLATE.md').read_text(encoding='utf-8')
        self.assertLess(len(text),8000)
    def test_intro_is_router_not_full_architecture(self):
        text=(ROOT/'PROJECT-INTRO-TEMPLATE.md').read_text(encoding='utf-8')
        self.assertIn('Sources of truth',text)
        self.assertIn('Execution and continuity',text)
        self.assertNotIn('lyonpro',text.lower())
    def test_onboarding_uses_adaptive_one_question_intake(self):
        text=(ROOT/'ONBOARDING.md').read_text(encoding='utf-8')
        self.assertIn('one high-value question per turn',text)
        self.assertIn('goal charter',text)
        self.assertIn('8,000 characters',text)
    def test_core_separates_domain_packs_and_adapters(self):
        text=(ROOT/'AI-ARCHITECTURE-WORKFLOW.md').read_text(encoding='utf-8')
        self.assertIn('Domain packs',text)
        self.assertIn('Execution adapters',text)
        self.assertIn('Only COMPLETED_VERIFIED',text)
if __name__=='__main__': unittest.main()
