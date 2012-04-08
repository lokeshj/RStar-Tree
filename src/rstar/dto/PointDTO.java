package rstar.dto;

/**
 * User: Lokesh
 * Date: 4/4/12
 * Time: 9:05 PM
 */
public class PointDTO extends AbstractDTO{
    public float oid;
    public float[] coords;

    public PointDTO(float oid, float[] coords) {
        this.oid = oid;
        this.coords = coords;
    }
}
