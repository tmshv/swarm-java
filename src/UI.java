import controlP5.ControlP5;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created at 15/07/16
 *
 * @author tmshv
 */
public class UI {
    ControlP5 ui;

    float pointCloudScale;

    float rotation;
    float rotationZ;
    float positionZ;

    float cursorLat;
    float cursorLon;

    float step = 12;

    int width = 350;
    int height = 10;

    private PVector pos;

    public UI(PApplet app, float x, float y) {
        pos = new PVector(x, y);
        ui = new ControlP5(app);
        ui.setAutoDraw(false);

        addSlider("rotation", 0, 0, (float) Math.PI);
        addSlider("rotationZ", 0, 0, (float) (Math.PI * 2));
        addSlider("positionZ", 0, -1000, 1000);

        addSpace();
        addSlider("cursorLat", 0, -0.01f, 0.01f);
        addSlider("cursorLon", 0, -0.01f, 0.01f);
//        addSlider("pc-scale", 0, 0, 1);
    }

    void update() {
        rotation = getFloat("rotation");
        rotationZ = getFloat("rotationZ");
        positionZ = getFloat("positionZ");
        cursorLat = getFloat("cursorLat");
        cursorLon = getFloat("cursorLon");
//        pointCloudScale = getFloat("pc-scale");
    }

    void draw() {
        ui.draw();
    }

    private void addSlider(String name, float value, float from, float to) {
        ui
                .addSlider(name)
                .setPosition(pos.x, pos.y)
                .setSize(width, height)
                .setRange(from, to)
                .setValue(value);
        pos.y += step;
    }

    private void addSpace() {
        pos.y += step * .5;
    }

    private float getFloat(String name) {
        return ui.getController(name).getValue();
    }
}
