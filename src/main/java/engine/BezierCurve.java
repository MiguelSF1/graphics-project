package engine;

import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class BezierCurve {

    private final List<Vector3f> controlPoints;
    private float duration = 5f;

    public BezierCurve() {
        this.controlPoints = new ArrayList<>();
    }

    public void addControlPoint(Vector3f point) {
        controlPoints.add(point);
    }

    public void clearControlPoints() {
        controlPoints.clear();
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }


    public Vector3f evaluateAtTime(float globalTime) {
        float totalT = (globalTime % duration) / duration; // normalized time on path
        int segmentCount = getSegmentCount();
        float segmentT = totalT * segmentCount; // find segment currently in with fractional amount
        int segmentIndex = Math.min((int) segmentT, segmentCount - 1); // dont go out of bounds and get int amount
        float localT = segmentT - segmentIndex; // how far in current segment

        // get points of segment
        Vector3f p0 = controlPoints.get(segmentIndex * 3);
        Vector3f p1 = controlPoints.get(segmentIndex * 3 + 1);
        Vector3f p2 = controlPoints.get(segmentIndex * 3 + 2);
        Vector3f p3 = controlPoints.get(segmentIndex * 3 + 3);

        return evaluateCubic(p0, p1, p2, p3, localT);
    }

    private Vector3f evaluateCubic(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float t) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        // bezier curve
        Vector3f result = new Vector3f();
        result.add(new Vector3f(p0).mul(uuu));
        result.add(new Vector3f(p1).mul(3 * uu * t));
        result.add(new Vector3f(p2).mul(3 * u * tt));
        result.add(new Vector3f(p3).mul(ttt));
        return result;
    }

    public Vector3f evaluateTangentAtTime(float globalTime) {
        float totalT = (globalTime % duration) / duration;
        int segmentCount = (controlPoints.size() - 1) / 3;
        float segmentT = totalT * segmentCount;
        int segmentIndex = Math.min((int) segmentT, segmentCount - 1);
        float localT = segmentT - segmentIndex;

        Vector3f p0 = controlPoints.get(segmentIndex * 3);
        Vector3f p1 = controlPoints.get(segmentIndex * 3 + 1);
        Vector3f p2 = controlPoints.get(segmentIndex * 3 + 2);
        Vector3f p3 = controlPoints.get(segmentIndex * 3 + 3);

        return evaluateCubicTangent(p0, p1, p2, p3, localT);
    }

    private Vector3f evaluateCubicTangent(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float t) {
        float u = 1 - t;
        Vector3f tangent = new Vector3f();

        // first derivative of a bezier curve
        tangent.add(new Vector3f(p1).sub(p0).mul(3 * u * u));
        tangent.add(new Vector3f(p2).sub(p1).mul(6 * u * t));
        tangent.add(new Vector3f(p3).sub(p2).mul(3 * t * t));

        return tangent.normalize(); // direction not speed
    }

    public int getSegmentCount() {
        return (controlPoints.size() - 1) / 3;
    }

    public List<Vector3f> getControlPoints() {
        return controlPoints;
    }
}
