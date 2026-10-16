import dev.lyonpro.tiktoklandscape.EpisodeCounter;
import dev.lyonpro.tiktoklandscape.EpisodePolicy;
import static dev.lyonpro.tiktoklandscape.EpisodePolicy.Action;

public final class EpisodePolicyTest {
    private static int count=0;
    private static void ok(boolean condition,String label) {
        if (!condition) throw new AssertionError(label);
        count++;
    }
    public static void main(String[] args) {
        EpisodeCounter first=EpisodeCounter.parse("Tập 1/15");
        EpisodeCounter last=EpisodeCounter.parse("Tập 15/15");
        EpisodeCounter en=EpisodeCounter.parse("Episode 3 of 8");
        ok(first!=null && first.current==1 && first.total==15, "VN counter");
        ok(last!=null && last.last(), "last episode");
        ok(en!=null && en.current==3 && en.total==8, "EN counter");
        ok(EpisodeCounter.parse("video 22/22")==null, "do not mistake unrelated ratio");
        ok(EpisodeCounter.parse("Tập 16/15")==null, "invalid current");
        ok(EpisodeCounter.parse("Tập 1/0")==null, "invalid total");
        ok(EpisodeCounter.parse("Tập 1/999")==null, "unreasonable total");
        ok(EpisodeCounter.parse("Tập 10")==null, "unknown total");
        ok(EpisodeCounter.parse(null)==null, "null text");
        ok(EpisodePolicy.afterLoop(first,true,false)==Action.CLICK_NEXT,"select explicit next");
        ok(EpisodePolicy.afterLoop(last,true,true)==Action.STOP_LAST,"never swipe past last");
        ok(EpisodePolicy.afterLoop(last,false,false)==Action.STOP_LAST,"last does not require next button");
        ok(EpisodePolicy.afterLoop(null,false,false)==Action.NO_SAFE_NEXT,"fail closed without next");
        ok(EpisodePolicy.afterLoop(null,false,true)==Action.SWIPE_OPT_IN,"explicit opt-in required");
        ok(EpisodePolicy.afterLoop(en,true,true)==Action.CLICK_NEXT,"prefer button over swipe");
        ok(first.same(EpisodeCounter.parse("Episode 1/15")),"same index across labels");
        System.out.println("EpisodeCounter + EpisodePolicy: "+count+" assertions passed");
    }
}
