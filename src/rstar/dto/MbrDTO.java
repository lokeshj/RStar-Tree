package rstar.dto;

import util.Constants;

/**
 * User: Lokesh
 * Date: 4/4/12
 * Time: 9:04 PM
 */
public class MbrDTO extends AbstractDTO{
    public float[][] points;

    public MbrDTO(float[][] points) {
        this.points = points;
    }
}
