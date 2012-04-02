package rstar;

/**
 * Created with IntelliJ IDEA.
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
}
