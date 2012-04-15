package rstar.interfaces;

import rstar.spatial.SpatialPoint;

import java.util.List;

public interface ISpatialQuery {
    /**
     * inserts a point in the tree
     * @param point the point to be inserted
     * @return 1 if successfull, -1 otherwise
     */
    int insert(SpatialPoint point);

    /**
     * returns the oid of the supplied point
     * in the tree if present
     * @param point the point to be searched
     * @return float oid
     */
    float pointSearch(SpatialPoint point);

    /**
     * returns all points in distance <i>range</i> of
     * point <i>center</i>
     * @return List of points in the range
     */
    List<SpatialPoint> rangeSearch(SpatialPoint center, double range);

    /**
     * returns the k nearest neighbours of <i>center</i>
     * @return List of k nearest neighbours of center
     */
    List<SpatialPoint> knnSearch(SpatialPoint center, int k);
}
