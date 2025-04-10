package game;

import engine.*;
import engine.Scene;

public class Main implements EngineLogic {

    public static void main(String[] args) {
        Main main = new Main();
        Engine gameEng = new Engine("Graphics Project", main);
        gameEng.start();
    }

    @Override
    public void cleanup() {
        // Nothing to be done yet
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        float[] positions = new float[]{
                -0.5f,  0.5f, -1.0f,
                -0.5f, -0.5f, -1.0f,
                 0.5f, -0.5f, -1.0f,
                 0.5f,  0.5f, -1.0f,
        };
        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
                0, 1, 3,
                3, 1, 2,
        };
        Mesh mesh = new Mesh(positions, colors, indices);
        scene.addMesh("quad", mesh);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        // Nothing to be done yet
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        // Nothing to be done yet
    }
}
