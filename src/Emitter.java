import processing.core.PVector;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
class Emitter {
    private Timer timer;

    private int period;
    private PVector location;
    private String type;

    Emitter(String type, PVector location, int period) {
        this.type = type;
        this.period = period;
        this.location = location;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Your database code here
            }
        }, 0, period);
    }

    void update(){

    }

    public PVector getLocation() {
        return location;
    }

    public void setLocation(PVector location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
