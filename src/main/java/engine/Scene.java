package engine;

import org.joml.Vector3f;

import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;

    private Projection projection;

    private TextureCache textureCache;

    private Player player;

    private Skybox skybox;

    private Terrain terrain;

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

        generateTerrain();
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
        player.setPosition(50, 1.5f, 100);
        addEntity(player);
    }

    public void checkPlayerMove(long dt, Window window) {
        Vector3f playerMove = player.getMove(dt, window);

        Vector3f oldPos = new Vector3f(player.getPosition());
        player.incrementPosition(playerMove.x, playerMove.y, playerMove.z);

        AABB playerBox = player.getAABB();

        boolean collided = false;
        for (Model model : modelMap.values()) {
            for (Entity e : model.getEntitiesList()) {
                if (e == player) continue;

                AABB otherBox = e.getAABB();
                if (AABB.intersects(playerBox, otherBox)) {
                    collided = true;
                    break;
                }
            }
            if (collided) break;
        }

        if (collided) {
            player.setPosition(oldPos.x, oldPos.y, oldPos.z);
        }
    }

    private void generateTerrain() {
        int[][] hm = new Heightmap("resources/models/terrain/heightmap.png").getGray();
        terrain = new Terrain();

        Texture texture = getTextureCache().createTexture("resources/models/grass/grass.png");
        Material material = new Material();
        material.setTexturePath(texture.getTexturePath());
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        material.getMeshList().add(terrain.generateMesh());
        Model groundModel = new Model("ground-model", materialList);
        addModel(groundModel);

        Entity ground = new Entity("ground", groundModel.getId(), terrain.generateMesh());
        ground.setPosition(0, 0, 0);
        addEntity(ground);
    }
}
