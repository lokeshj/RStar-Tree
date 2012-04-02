package rstar;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Lokesh
 * Date: 3/4/12
 * Time: 2:05 AM
 */
public class SpatialPoint implements Serializable {
    private int _dimension;
    private double[] _cords;
    private int  _oid;

    public SpatialPoint() {
    }

    public SpatialPoint(int dimension) {
        this._dimension = dimension;
    }

    public SpatialPoint(double[] cords, int dimension) {
        this._cords = cords;
        this._dimension = dimension;
    }

    public int getDimension(){
        return _dimension;
    }

    public void setCords(double[] data){
        this._cords = data;
    }

    public double[] getCords() {
        return _cords;
    }

    public int getOid() {
        return _oid;
    }

    public void setOid(int oid) {
        this._oid = oid;
    }
}
