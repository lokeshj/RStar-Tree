package rstar;

import java.io.Serializable;
import java.util.HashMap;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:33 AM
 */
public class StorageManager implements Serializable {
    HashMap<IRStarNode, String> fileMap;

    public StorageManager() {
        //TODO
        fileMap = new HashMap<IRStarNode, String>();
    }

    public void addNode(IRStarNode node) {
        fileMap.put(node, ""+node.hashCode());
    }

    public String getFileName(IRStarNode node) {
        return fileMap.get(node);
    }

    public void saveNode(IRStarNode node) {
        //TODO serialize node and save to file
    }

    public IRStarNode loadNode(IRStarNode node) {
        //TODO load the file from disk and return unSerialized object
        return null;
    }
}
