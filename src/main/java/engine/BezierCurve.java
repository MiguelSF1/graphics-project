package engine;

import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class BezierCurve {

    private final List<Vector3f> controlPoints;
    private final int stepsPerSegment;
    private float duration = 5f;

    public BezierCurve(int stepsPerSegment) {
        this.controlPoints = new ArrayList<>();
        this.stepsPerSegment = stepsPerSegment;
    }

    public void addControlPoint(Vector3f point) {
        controlPoints.add(point);
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public List<Vector3f> computeCurve() {
        List<Vector3f> curvePoints = new ArrayList<>();
        int segmentCount = (controlPoints.size() - 1) / 3;

        for (int i = 0; i < segmentCount; i++) {
            Vector3f p0 = controlPoints.get(i * 3);
            Vector3f p1 = controlPoints.get(i * 3 + 1);
            Vector3f p2 = controlPoints.get(i * 3 + 2);
            Vector3f p3 = controlPoints.get(i * 3 + 3);

            for (int j = 0; j <= stepsPerSegment; j++) {
                float t = j / (float) stepsPerSegment;
                Vector3f point = evaluateCubic(p0, p1, p2, p3, t);
                if (i > 0 && j == 0) continue; // avoid duplicates
                curvePoints.add(point);
            }
        }

        return curvePoints;
    }

    public Vector3f evaluateAtTime(float globalTime) {
        float totalT = (globalTime % duration) / duration;
        int segmentCount = (controlPoints.size() - 1) / 3;
        float segmentT = totalT * segmentCount;
        int segmentIndex = Math.min((int) segmentT, segmentCount - 1);
        float localT = segmentT - segmentIndex;

        Vector3f p0 = controlPoints.get(segmentIndex * 3);
        Vector3f p1 = controlPoints.get(segmentIndex * 3 + 1);
        Vector3f p2 = controlPoints.get(segmentIndex * 3 + 2);
        Vector3f p3 = controlPoints.get(segmentIndex * 3 + 3);

        return evaluateCubic(p0, p1, p2, p3, localT);
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

    private Vector3f evaluateCubic(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float t) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        Vector3f result = new Vector3f();
        result.add(new Vector3f(p0).mul(uuu));
        result.add(new Vector3f(p1).mul(3 * uu * t));
        result.add(new Vector3f(p2).mul(3 * u * tt));
        result.add(new Vector3f(p3).mul(ttt));
        return result;
    }

    private Vector3f evaluateCubicTangent(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float t) {
        float u = 1 - t;
        Vector3f tangent = new Vector3f();

        tangent.add(new Vector3f(p1).sub(p0).mul(3 * u * u));
        tangent.add(new Vector3f(p2).sub(p1).mul(6 * u * t));
        tangent.add(new Vector3f(p3).sub(p2).mul(3 * t * t));

        return tangent.normalize();
    }

    public int getSegmentCount() {
        return (controlPoints.size() - 1) / 3;
    }

    public List<Vector3f> getControlPoints() {
        return controlPoints;
    }
}
