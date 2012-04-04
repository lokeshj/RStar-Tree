package rstar.dto;

import java.io.Serializable;

/**
 * User: Lokesh
 * Date: 4/4/12
 * Time: 4:29 AM
 */
public class TreeDTO implements Serializable {
    public int dimension;
    public int pagesize;
    public long rootPointer;

    public TreeDTO(int dimension, int pagesize, long rootPointer) {
        this.dimension = dimension;
        this.pagesize = pagesize;
        this.rootPointer = rootPointer;
    }
}
