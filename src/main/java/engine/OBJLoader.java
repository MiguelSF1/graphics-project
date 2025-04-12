package engine;

import org.joml.*;

import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    public static Mesh loadMeshFromOBJ(String fileName) {
        String file = Utils.readFile(fileName);
        String[] lines = file.split("\n");

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] verticesArray;
        float[] texturesArray = null;
        float[] normalsArray = null;
        int[] indicesArray;

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    Vector3f vec3f = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt":
                    Vector2f vec2f = new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn":
                    Vector3f vec3fn = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                    normals.add(vec3fn);
                    break;
                case "f":
                    if (texturesArray == null) {
                        texturesArray = new float[vertices.size() * 2];
                        normalsArray = new float[vertices.size() * 3];
                    }

                    for (int i = 1; i <= 3 ; i++) {
                        String[] vertex = tokens[i].split("/");

                        int curVertexPointer = Integer.parseInt(vertex[0]) - 1;
                        indices.add(curVertexPointer);

                        Vector2f curTex = textures.get(Integer.parseInt(vertex[1]) - 1);
                        texturesArray[curVertexPointer * 2] = curTex.x;
                        texturesArray[curVertexPointer * 2 + 1] = 1 - curTex.y;

                        Vector3f curNorm = normals.get(Integer.parseInt(vertex[2]) - 1);
                        normalsArray[curVertexPointer * 3] = curNorm.x;
                        normalsArray[curVertexPointer * 3 + 1] = curNorm.y;
                        normalsArray[curVertexPointer * 3 + 2] = curNorm.z;
                    }
                    break;
                default:
                    break;
            }
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for (Vector3f vec3f : vertices) {
            verticesArray[vertexPointer++] = vec3f.x;
            verticesArray[vertexPointer++] = vec3f.y;
            verticesArray[vertexPointer++] = vec3f.z;
        }

        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return new Mesh(verticesArray, normalsArray, texturesArray, indicesArray);
    }
}
