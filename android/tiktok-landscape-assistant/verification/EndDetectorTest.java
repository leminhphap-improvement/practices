import dev.lyonpro.tiktoklandscape.EndDetector;

public final class EndDetectorTest {
    private static void ok(boolean expected,boolean actual,String caseName) {
        if(expected!=actual)throw new AssertionError(caseName+": expected "+expected+", actual "+actual);
    }
    public static void main(String[] args) {
        EndDetector d=new EndDetector();
        ok(false,d.observe(0f,1000),"start");
        ok(false,d.observe(.5f,2000),"mid");
        ok(false,d.observe(.91f,3000),"near-end");
        ok(false,d.observe(.99f,4000),"near-end-late");
        ok(true,d.observe(.01f,5000),"end-to-start loop");
        ok(false,d.observe(.01f,5010),"no duplicate");
        ok(false,d.observe(.95f,5100),"second end armed");
        ok(false,d.observe(.02f,5300),"cooldown");
        ok(false,d.observe(.60f,6000),"seek middle not loop");
        ok(false,d.observe(Float.NaN,6100),"reject NaN");
        d.reset();ok(false,d.observe(0f,9000),"reset does not trigger");
        d.observe(.95f,10000);ok(true,d.observe(.0f,11000),"next loop after reset");
        System.out.println("EndDetector: 11 assertions passed");
    }
}
