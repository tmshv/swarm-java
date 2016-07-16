import processing.core.PVector;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Tweet extends Attractor {
    float followersCoef = 0.3f;
    String text;
    String username;
    int followers;

    public Tweet(PVector location, String text, String username, int followers) {
        super("tweet", 0, location);
        this.text = text;
        this.username = username;
        this.followers = followers;
    }

    @Override
    public float getMass() {
        return followers * followersCoef;
    }
}
