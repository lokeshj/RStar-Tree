package rstar.dto;

public class PointDTO extends AbstractDTO{
    public float oid;
    public float[] coords;

    public PointDTO(float oid, float[] coords) {
        this.oid = oid;
        this.coords = coords;
    }
}
