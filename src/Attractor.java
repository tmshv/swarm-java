import processing.core.PVector;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
class Attractor implements IInterest{
    private float mass;
    private PVector location;

    private String type;

    private int color;
    Attractor(String type, float mass, PVector location) {
        this.type = type;
        this.mass = mass;
        this.location = location;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public float getInterestValueFor(IInterest other) {
        return 0;
    }

    @Override
    public float getValue() {
        return mass;
    }
}
