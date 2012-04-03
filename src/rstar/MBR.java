package rstar;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:52 AM
 */
public class MBR {
    private int _dimension;
    private SpatialPoint[] points;

    public MBR(int dimension) {
        this._dimension = dimension;
        points = new SpatialPoint[(int)Math.pow(2, _dimension)];
    }

    public MBR(int dimension, SpatialPoint[] coords) {
        this._dimension = dimension;
        points = new SpatialPoint[(int)Math.pow(2, _dimension)];
        System.arraycopy(coords, 0, points, 0, coords.length);
    }

    public void update(SpatialPoint newPoint) {
        //TODO
    }

    public void update(MBR addedRegion) {
        //TODO
    }

    public MBR getIntersection(MBR otherMBR) {
        //TODO
        return this;
    }

    public long deltaV_onInclusion(MBR newmbr) {
        //TODO
        return 0;
    }
}
