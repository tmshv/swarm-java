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
    float pointCloudRotation;

    float rotation;
    float rotationZ;
    float positionZ;

    float cursorLat;
    float cursorLon;
    float cursorHeight;

    float step = 12;

    int width = 350;
    int height = 10;

    private PVector pos;

    public UI(PApplet app, float x, float y) {
        pos = new PVector(x, y);
        ui = new ControlP5(app);
        ui.setAutoDraw(false);

        addSlider("rotation", 0.93f, 0, (float) Math.PI / 3);
        addSlider("rotationZ", 0, 0, (float) (Math.PI * 2));
        addSlider("positionZ", -68.57f, -1000, 1000);

        addSpace();
        addSlider("cursorLat", 0, -0.001f, 0.001f);
        addSlider("cursorLon", 0, -0.001f, 0.001f);
        addSlider("cursorHeight", 0, -100, 100);

        addSpace();
        addSlider("pcScale", 0.0006142857f, 0, .005f);
        addSlider("pcRot", 0.57f, 0, (float) Math.PI);
    }

    void update() {
        rotation = getFloat("rotation");
        rotationZ = getFloat("rotationZ");
        positionZ = getFloat("positionZ");
        cursorLat = getFloat("cursorLat");
        cursorLon = getFloat("cursorLon");
        cursorHeight = getFloat("cursorHeight");
        pointCloudScale = getFloat("pcScale");
        pointCloudRotation = getFloat("pcRot");
    }

    void print() {
        System.out.println("rotation: " + rotation);
        System.out.println("rotationZ: " + rotationZ);
        System.out.println("positionZ: " + positionZ);
        System.out.println("cursorLat: " + cursorLat);
        System.out.println("cursorLon: " + cursorLon);
        System.out.println("cursorHeight: " + cursorHeight);
        System.out.println("pointCloudScale: " + pointCloudScale);
        System.out.println("pointCloudRotation: " + pointCloudRotation);
    }

    void draw() {
        ui.draw();
    }

    private void addSlider(String name, float value, float from, float to) {
        ui
                .addSlider(name)
                .setColorLabel(0)
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
