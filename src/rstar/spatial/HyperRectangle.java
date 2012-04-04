package rstar.spatial;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:52 AM
 */
public class HyperRectangle {
    private int _dimension;
    private SpatialPoint[] points;

    public HyperRectangle(int dimension) {
        this._dimension = dimension;
        points = new SpatialPoint[(int)Math.pow(2, _dimension)];
    }

    public HyperRectangle(int dimension, SpatialPoint[] coords) {
        this._dimension = dimension;
        points = new SpatialPoint[(int)Math.pow(2, _dimension)];
        System.arraycopy(coords, 0, points, 0, coords.length);
    }

    public void update(SpatialPoint newPoint) {
        //TODO
    }

    public void update(HyperRectangle addedRegion) {
        //TODO
    }

    public HyperRectangle getIntersection(HyperRectangle otherMBR) {
        //TODO
        return this;
    }

    public long deltaV_onInclusion(HyperRectangle newmbr) {
        //TODO
        return 0;
    }
}
