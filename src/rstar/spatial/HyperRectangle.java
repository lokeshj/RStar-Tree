package rstar.spatial;

import rstar.dto.MbrDTO;
import rstar.interfaces.IDtoConvertible;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:52 AM
 */
public class HyperRectangle implements IDtoConvertible {
    private int _dimension;
    /**
     * points is a 2D double array containing
     * the max and min values for each dimension
     * in the rectangle.
     */
    private float[][] points;
    private static int MAX_CORD = 0;
    private static int MIN_CORD = 1;

    public float[][] getPoints() {
        return points;
    }

    public void setPoints(float[][] points) {
        this.points = points;
    }

    public HyperRectangle(int dimension) {
        this._dimension = dimension;
        points = new float[dimension][2];
    }

    public HyperRectangle(int dimension, SpatialPoint[] points) {
        this._dimension = dimension;
        this.points = new float[dimension][2];

        update(points);
    }

    private void update(SpatialPoint[] newPoints) {
        for (int j = 0; j < newPoints.length; j++) {
            float[] cord = newPoints[j].getCords();
            assert cord.length == _dimension;
            for (int i = 0; i < cord.length; i++) {
                if (points[i][MAX_CORD] < cord[i]) {
                    points[i][MAX_CORD] = cord[i];
                }
                if (points[i][MIN_CORD] > cord[i]) {
                    points[i][MIN_CORD] = cord[i];
                }
            }
        }
    }

    public void update(SpatialPoint newPoint) {
        SpatialPoint[] newPoints = new SpatialPoint[1];
        newPoints[0] = newPoint;
        update(newPoints);
    }

    public void update(HyperRectangle addedRegion) {
        float[][] newPoints = addedRegion.getPoints();
        assert newPoints.length == _dimension;
        for (int j = 0; j < _dimension; j++) {
            if (points[j][MAX_CORD] < newPoints[j][MAX_CORD]) {
                points[j][MAX_CORD] = newPoints[j][MAX_CORD];
            }
            if (points[j][MIN_CORD] > newPoints[j][MIN_CORD]) {
                points[j][MIN_CORD] = newPoints[j][MIN_CORD];
            }
        }
    }

    /**
     * finds the intersecting region of this MBR with otherMBR
     * @param otherMBR
     * @return the intersecting region, null if not intersecting
     */
    public HyperRectangle getIntersection(HyperRectangle otherMBR) {
        float[][] interPoints = new float[_dimension][2];
        float[][] newPoints = otherMBR.getPoints();
        assert newPoints.length == _dimension;

        boolean intersectExists = true;
        for (int i = 0; i < _dimension; i++) {
            if ((points[i][MAX_CORD] <= newPoints[i][MIN_CORD]) || (points[i][MIN_CORD] >= newPoints[i][MAX_CORD])) {
                intersectExists = false;
                break;
            }
            interPoints[i][MAX_CORD] = Math.min(newPoints[i][MAX_CORD], points[i][MAX_CORD]);
            interPoints[i][MIN_CORD] = Math.max(newPoints[i][MIN_CORD], points[i][MAX_CORD]);
        }

        if (!intersectExists) {
            return null;
        }
        HyperRectangle intersect = new HyperRectangle(_dimension);
        intersect.setPoints(interPoints);
        return intersect;
    }

    public long deltaV_onInclusion(HyperRectangle newmbr) {
        //TODO deltaVOnInclusion
        return 0;
    }

    @Override
    public MbrDTO toDTO() {
        return new MbrDTO(points);
    }
}
