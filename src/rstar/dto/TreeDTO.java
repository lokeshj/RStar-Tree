package rstar.dto;

public class TreeDTO extends AbstractDTO {
    public int dimension;
    public int pagesize;
    public long rootPointer;

    public TreeDTO(int dimension, int pagesize, long rootPointer) {
        this.dimension = dimension;
        this.pagesize = pagesize;
        this.rootPointer = rootPointer;
    }
}
