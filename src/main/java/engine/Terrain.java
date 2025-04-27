package engine;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Terrain {

    private Scene scene;
    private int NUM_CHUNKS = 4;
    private Entity[][] terrainEntities;

    public Terrain(Scene scene) {
        this.scene = scene;

        Texture texture = scene.getTextureCache().createTexture("resources/models/quad/quad.png");
        Material material = new Material();
        material.setTexturePath(texture.getTexturePath());
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        Mesh mesh = OBJLoader.loadMeshFromOBJ("resources/models/quad/quad.obj");
        material.getMeshList().add(mesh);
        Model quadModel = new Model("quad-model", materialList);
        scene.addModel(quadModel);


        int numRows = NUM_CHUNKS * 2 + 1;
        int numCols = numRows;
        terrainEntities = new Entity[numRows][numCols];
        for (int j = 0; j < numRows; j++) {
            for (int i = 0; i < numCols; i++) {
                Entity entity = new Entity("TERRAIN_" + j + "_" + i, "quad-model", mesh);
                terrainEntities[j][i] = entity;
                scene.addEntity(entity);
            }
        }
    }

    public void updateTerrain() {
        int cellSize = 10;
        Camera camera = scene.getCamera();
        Vector3f cameraPos = camera.getPosition();
        int cellCol = (int) (cameraPos.x / cellSize);
        int cellRow = (int) (cameraPos.z / cellSize);

        int numRows = NUM_CHUNKS * 2 + 1;
        int numCols = numRows;
        int zOffset = -NUM_CHUNKS;
        float scale = cellSize / 2.0f;
        for (int j = 0; j < numRows; j++) {
            int xOffset = -NUM_CHUNKS;
            for (int i = 0; i < numCols; i++) {
                Entity entity = terrainEntities[j][i];
                entity.setScale(scale);
                entity.setPosition((cellCol + xOffset) * 2.0f, 0, (cellRow + zOffset) * 2.0f);
                entity.getModelMatrix().identity().scale(scale).translate(entity.getPosition());
                xOffset++;
            }
            zOffset++;
        }
    }
}
