package engine;

import org.joml.Vector3f;

public class AABB {
    public final Vector3f min;
    public final Vector3f max;

    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    public static boolean intersects(AABB a, AABB b) {
        return (a.min.x <= b.max.x && a.max.x >= b.min.x) &&
                (a.min.y <= b.max.y && a.max.y >= b.min.y) &&
                (a.min.z <= b.max.z && a.max.z >= b.min.z);
    }
}
