package rstar.interfaces;

import rstar.spatial.SpatialPoint;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 4:57 AM
 */
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
     * returns all points in distance range of
     * point center
     * @param center
     * @param range
     * @return array of points in the range
     */
    SpatialPoint[] rangeSearch(SpatialPoint center, double range);

    /**
     * returns the k nearest neighbours of center
     * @param center
     * @param k
     * @return array of k nearest neighbours of center
     */
    SpatialPoint[] knnSearch(SpatialPoint center, int k);
}
