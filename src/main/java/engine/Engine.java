package engine;

public class Engine {
    private final EngineLogic appLogic;
    private final Window window;
    private Render render;
    private boolean running;
    private Scene scene;

    public Engine(String windowTitle, EngineLogic appLogic) {
        window = new Window(windowTitle, () -> {
            resize();
            return null;
        });
        this.appLogic = appLogic;
        render = new Render();
        scene = new Scene(window.getWidth(), window.getHeight());
        appLogic.init(window, scene, render);
        running = true;
    }

    private void cleanup() {
        appLogic.cleanup();
        render.cleanup();
        scene.cleanup();
        window.cleanup();
    }

    private void resize() {
        scene.resize(window.getWidth(), window.getHeight());
    }

    private void run() {
        long initialTime = System.currentTimeMillis();
        float timeU = 1000.0f / 30.0f;
        float deltaUpdate = 0;

        long updateTime = initialTime;
        while (running && !window.windowShouldClose()) {
            window.pollEvents();

            long now = System.currentTimeMillis();
            deltaUpdate += (now - initialTime) / timeU;

            window.getMouseInput().input();
            appLogic.input(window, scene, now - initialTime);

            if (deltaUpdate >= 1) {
                long diffTimeMillis = now - updateTime;
                appLogic.update(window, scene, diffTimeMillis);
                updateTime = now;
                deltaUpdate--;
            }

            render.render(window, scene);

            window.update();

            initialTime = now;
        }

        cleanup();
    }

    public void start() {
        running = true;
        run();
    }

    public void stop() {
        running = false;
    }

}
