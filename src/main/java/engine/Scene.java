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
    private Entity terrainEntity;
    private Mesh terrainMesh;

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

        generateTerrain();

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

        player = new Player("player", playerModel.getId(), playerMesh, terrain);
        float playerHeight = terrain.getTerrainHeight(50, 100) + (playerMesh.getAabbMax().y - playerMesh.getAabbMin().y) * 0.5f;
        player.setPosition(50, playerHeight + 0.4f, 100);
        addEntity(player);
    }

    public void checkPlayerCollisions(Vector3f oldPos, char axis) {
        AABB playerBox = player.getAABB();
        boolean collided = false;

        if (playerBox.min.x < 0 || playerBox.max.x > terrain.getSize() || playerBox.min.z < 0 || playerBox.max.z > terrain.getSize()) {
            collided = true;
        }

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
            switch (axis) {
                case 'X': player.setPosition(oldPos.x, player.getPosition().y, player.getPosition().z); break;
                case 'Z': player.setPosition(player.getPosition().x, player.getPosition().y, oldPos.z); break;
                case 'Y':
                    player.setPosition(player.getPosition().x, oldPos.y, player.getPosition().z);
                    player.setCurUpSpeed(0);
                    player.setIsInAir(false);
                    break;
            }
        }
    }

    private void generateTerrain() {
        terrain = new Terrain();
        terrainMesh = terrain.generateMesh();

        Texture texture = getTextureCache().createTexture("resources/models/grass/grass.png");

        terrainEntity = new Entity("ground", "ground-model", terrainMesh);
        terrainEntity.setPosition(0, 0, 0);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Entity getTerrainEntity() {
        return terrainEntity;
    }

    public Mesh getTerrainMesh() {
        return terrainMesh;
    }
}
