package rstar.dto;

/**
 * User: Lokesh
 * Date: 4/4/12
 * Time: 9:06 PM
 */
public class NodeDTO extends AbstractDTO {
    public long[] children;
    public MbrDTO mbr;
    public boolean isLeaf;

    public NodeDTO(long[] children, MbrDTO mbr, boolean leaf) {
        this.children = children;
        this.mbr = mbr;
        isLeaf = leaf;
    }
}
