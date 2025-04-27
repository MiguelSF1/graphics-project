package engine;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class Heightmap {

    private int[][] gray;

    public Heightmap(String texturePath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer buf = stbi_load(texturePath, w, h, channels, 4);
            if (buf == null) {
                throw new RuntimeException("Image file [" + texturePath + "] not loaded: " + stbi_failure_reason());
            }

            int width = w.get(0);
            int height = h.get(0);

            gray = new int[height][width];
            for(int y = 0 ; y < height ; y++){
                for(int x = 0 ; x < width ; x++){
                    int i = (x + y * width) * 4;    // index of R byte in buf

                    int r = buf.get(i    ) & 0xFF;  // &0xFF to convert signed byte -> 0–255
                    int g = buf.get(i + 1) & 0xFF;
                    int b = buf.get(i + 2) & 0xFF;

                    // convert to gray
                    gray[y][x] = (r + g + b) / 3;
                }
            }
            stbi_image_free(buf);
        }
    }

    public int[][] getGray() {
        return gray;
    }
}
