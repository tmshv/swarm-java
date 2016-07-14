import processing.core.PVector;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Tweet extends Attractor {
    String text;
    String username;
    int followers;

    public Tweet(PVector location, String text, String username, int followers) {
        super("tweet", followers, location);
        this.text = text;
        this.username = username;
        this.followers = followers;
    }
}
