import processing.core.PVector;

import java.util.Date;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
class Emitter {
    //    private Timer timer;
//    ScheduledExecutorService service;
    private int period;
    private PVector location;
    private String type;
    private long timestamp;

    Emitter(String type, PVector location, int period) {
        this.type = type;
        this.period = period;
        this.location = location;

        this.timestamp = getTimestamp();

//        service = Executors.newSingleThreadScheduledExecutor();
//        service.scheduleAtFixedRate(this::update, 0, period, TimeUnit.MILLISECONDS);

//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                // Your database code here
//                update();
//            }
//        }, 0, period);
    }

    void run() {
        long currentTimestamp = getTimestamp();
        if (currentTimestamp - timestamp > period) {
            timestamp = currentTimestamp;
            update();
        }
    }

    void update() {

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

    private long getTimestamp() {
        Date d = new Date();
        return d.getTime();
    }
}
