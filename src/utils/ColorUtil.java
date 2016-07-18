package utils;

/**
 * Created at 18/07/16
 *
 * @author tmshv
 */
public class ColorUtil {
    public static int setAlpha(int color, int alpha) {
        int rgb = color & 0x00ffffff;
        return alpha << 24 | rgb;
    }
}
