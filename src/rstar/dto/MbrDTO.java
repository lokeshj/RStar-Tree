package rstar.dto;

/**
 * User: Lokesh
 * Date: 4/4/12
 * Time: 9:04 PM
 */
public class MbrDTO extends AbstractDTO{
    public PointDTO[] points;

    public MbrDTO(PointDTO[] points) {
        this.points = points;
    }
}
