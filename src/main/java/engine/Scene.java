package engine;

import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;

    private Projection projection;

    private TextureCache textureCache;

    private Player player;

    private Camera camera;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        addPlayer();
        camera = new Camera(player);
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
        return camera;
    }

    public Player getPlayer() {
        return player;
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
        Texture texture = getTextureCache().createTexture("resources/models/cube/cube.png");
        Material material = new Material();
        material.setTexturePath(texture.getTexturePath());
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        Mesh mesh = OBJLoader.loadMeshFromOBJ("resources/models/cube/cube.obj");
        material.getMeshList().add(mesh);
        Model stallModel = new Model("cube-model", materialList);
        addModel(stallModel);

        player = new Player("player", stallModel.getId());
        player.setPosition(0, 0, -5);
        addEntity(player);
    }
}
