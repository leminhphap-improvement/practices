import dev.lyonpro.tiktoklandscape.PlaybackProgress;

public final class PlaybackProgressTest {
    private static int n=0;
    private static void eq(Float expected,Float actual,String message) {
        if((expected==null)!=(actual==null) ||
                expected!=null && Math.abs(expected-actual)>0.0001f) {
            throw new AssertionError(message+": expected="+expected+", actual="+actual);
        }
        n++;
    }
    public static void main(String[] args) {
        eq(.5f,PlaybackProgress.fromVisibleLabel("00:30 / 01:00"),"normal video");
        eq(0f,PlaybackProgress.fromVisibleLabel("0:00/03:00"),"beginning");
        eq(1f,PlaybackProgress.fromVisibleLabel("1:40 / 1:40"),"end");
        eq(null,PlaybackProgress.fromVisibleLabel("02:00"),"single timestamp");
        eq(null,PlaybackProgress.fromVisibleLabel("1:40 / 1:20"),"elapsed beyond total");
        eq(null,PlaybackProgress.fromVisibleLabel("1:60 / 2:00"),"invalid seconds");
        eq(null,PlaybackProgress.fromVisibleLabel("13:10 / 00:04"),"invalid elapsed");
        eq(null,PlaybackProgress.fromVisibleLabel("0:01 / 0:02"),"not playable duration");
        eq(null,PlaybackProgress.fromVisibleLabel(null),"null label");
        eq(.25f,PlaybackProgress.fromVisibleLabel("00:00:30 / 00:02:00"),"hours display");
        System.out.println("PlaybackProgress: "+n+" assertions passed");
    }
}
