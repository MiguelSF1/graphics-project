package engine;

import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;

    private Projection projection;

    private TextureCache textureCache;

    private Player player;

    private Skybox skybox;

    private Camera playerCamera;
    private Camera leftCamera;
    private Camera rightCamera;
    private Camera camera1stPerson;

    private int curCameraIdx;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();

        skybox = new Skybox();

        addPlayer();
        playerCamera = new Camera(player);

        leftCamera = new Camera(player);
        leftCamera.setPosition(-10, 0, 0);

        rightCamera = new Camera(player);
        rightCamera.setPosition(10, 0, 0);

        camera1stPerson = new Camera(player);

        curCameraIdx = 0;
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntitiesList().add(entity);
    }

    public Projection getProjection() {
        return projection;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public Camera getCamera() {
        if (curCameraIdx == 0) {
            return playerCamera;
        } else if (curCameraIdx == 1) {
            return leftCamera;
        } else if (curCameraIdx == 2) {
            return rightCamera;
        }

        return camera1stPerson;
    }

    public Player getPlayer() {
        return player;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public int getCurCameraIdx() {
        return curCameraIdx;
    }

    public void selectCamera(int idx) {
        curCameraIdx = idx;
    }

    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public void cleanup() {
        modelMap.values().forEach(Model::cleanup);
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }

    private void addPlayer() {
        Material material = new Material();
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        Mesh playerMesh = OBJLoader.loadMeshFromOBJ("resources/models/sheep/sheep.obj");
        material.getMeshList().add(playerMesh);
        Model playerModel = new Model("player-model", materialList);
        addModel(playerModel);

        player = new Player("player", playerModel.getId(), playerMesh);
        player.setPosition(0, 0, -5);
        addEntity(player);
    }
}
