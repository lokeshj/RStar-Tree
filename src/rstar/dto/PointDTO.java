package rstar.dto;

/**
 * User: Lokesh
 * Date: 4/4/12
 * Time: 9:05 PM
 */
public class PointDTO {
    public float oid;
    public double[] coords;

    public PointDTO(float oid, double[] coords) {
        this.oid = oid;
        this.coords = coords;
    }
}
