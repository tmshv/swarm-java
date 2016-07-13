import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Track {
    ArrayList<PVector> history = new ArrayList<PVector>();

    int paint;

    Track(int c) {
        paint = c;
    }

    public PVector last() {
        if (history.size() > 1) {
            int i = history.size() - 2;
            return history.get(i);
        } else {
            return null;
        }
    }

    public void write(PVector coord) {
        this.history.add(coord);
    }
}
