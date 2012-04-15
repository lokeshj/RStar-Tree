package rstar.dto;

public class MbrDTO extends AbstractDTO{
    public float[][] points;

    public MbrDTO(float[][] points) {
        this.points = points;
    }
}
