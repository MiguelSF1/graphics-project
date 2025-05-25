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

    private AmbientLight ambientLight;
    private DirLight dirLight;
    private List<PointLight> pointLights;

    private ParticleSystem particleSystem;

    private BezierCurve bezierCurve;
    private Entity movingEntity;
    private float curveTime = 0f;

    private Entity starEntity;
    private int starCount;


    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();

        skybox = new Skybox();

        generateTerrain();

        addPlayer();

        playerCamera = new Camera(player);

        createLights();

        particleSystem = new ParticleSystem(0.1f, 0.01f, 2000);

        generateBezierCurve();

        generateStar();
        starCount = 0;
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
        return playerCamera;
    }

    public Player getPlayer() {
        return player;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public DirLight getDirLight() {
        return dirLight;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public List<Particle> getParticles() {
        return particleSystem.getAliveParticles();
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public float getCurveTime() {
        return curveTime;
    }

    public Entity getMovingEntity() {
        return movingEntity;
    }

    public BezierCurve getBezierCurve() {
        return bezierCurve;
    }

    public void setCurveTime(float curveTime) {
        this.curveTime = curveTime;
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
                if (e.getId().equals(player.getId())) continue;

                AABB otherBox = e.getAABB();
                if (AABB.intersects(playerBox, otherBox)) {
                    collided = true;
                    if (e.getId().equals(starEntity.getId())) {
                        Random rand = new Random();
                        float randomX = 20 + rand.nextFloat() * (60 - 20);
                        float randomZ = 40 + rand.nextFloat() * (150 - 40);
                        float fixedY = 3;
                        starEntity.setPosition(randomX, fixedY, randomZ);
                        starCount++;
                        playerCamera.startCelebration();
                    }
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

        getTextureCache().createTexture("resources/models/grass/grass.png");

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

    private void createLights() {
        ambientLight = new AmbientLight();
        dirLight = new DirLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 1), 0.8f);
        pointLights = new ArrayList<>();

        PointLight pointLight = new PointLight(new Vector3f(0, 0, 1), new Vector3f(50, 0.2f, 100), 1.0f);
        pointLights.add(pointLight);

    }

    private void generateBezierCurve() {
        bezierCurve = new BezierCurve();
        bezierCurve.addControlPoint(new Vector3f(30, 5, 80));
        bezierCurve.addControlPoint(new Vector3f(35, 10, 60));
        bezierCurve.addControlPoint(new Vector3f(40, 5, 40));

        bezierCurve.addControlPoint(new Vector3f(45, 10, 20));

        bezierCurve.addControlPoint(new Vector3f(40, 5, 40));
        bezierCurve.addControlPoint(new Vector3f(35, 10, 60));
        bezierCurve.addControlPoint(new Vector3f(30, 5, 80));

        Mesh mesh = OBJLoader.loadMeshFromOBJ("resources/models/sheep/sheep.obj");
        Material material = new Material();
        material.getMeshList().add(mesh);
        Model model = new Model("moving-model", List.of(material));
        addModel(model);

        movingEntity = new Entity("mover", "moving-model", mesh);
        addEntity(movingEntity);
    }

    private void generateStar() {
        Material starMaterial = new Material();
        getTextureCache().createTexture("resources/models/star/star.png");
        starMaterial.setTexturePath("resources/models/star/star.png");
        List<Material> starMaterialList = new ArrayList<>();
        starMaterialList.add(starMaterial);
        Mesh starMesh = OBJLoader.loadMeshFromOBJ("resources/models/star/star.obj");
        starMaterial.getMeshList().add(starMesh);
        Model starModel = new Model("star-model", starMaterialList);
        addModel(starModel);
        starEntity = new Entity("star-entity", "star-model", starMesh);
        starEntity.setPosition(50, 2, 90);
        starEntity.setRotation(90, 0, 0);
        addEntity(starEntity);
    }
}