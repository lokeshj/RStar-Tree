package rstar.dto;

/**
 * User: Lokesh
 * Date: 4/4/12
 * Time: 9:06 PM
 */
public class NodeDTO extends AbstractDTO {
    public long nodeId;
    public long[] children;
    public MbrDTO mbr;

    public NodeDTO(long nodeId, long[] children, MbrDTO mbr) {
        this.nodeId = nodeId;
        this.children = children;
        this.mbr = mbr;
    }
}
