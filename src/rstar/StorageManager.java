package rstar;

import rstar.dto.TreeFile;
import util.Constants;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:33 AM
 */
public class StorageManager  {
    RandomAccessFile curFile;
    FileChannel channel;

    public StorageManager() {
        //TODO
    }

    public void save(IRStarNode node) {
        //TODO serialize node and save to file
    }

    public IRStarNode load(long nodeId) {
        return nodeFromDisk(fileFromNodeId(nodeId));
    }

    public String fileFromNodeId(long nodeId) {
        String file = Constants.FILE_PREFIX + nodeId + Constants.FILE_SUFFIX;
        return file;
    }

    private IRStarNode nodeFromDisk(String filename) {
        IRStarNode node = null;
        //TODO read file and unserialize object, tc of exceptions
        return null;
    }

    public int saveTree(TreeFile tree, File saveFile) {
        //TODO save tree object in saveFile
        int status = -1;
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(tree);
            oos.flush();
            oos.close();
            status = 1;             // successful save
        } catch (IOException e) {
            System.err.println("Error while saving Tree to " + saveFile.toURI());
        }

        return status;
    }

    public TreeFile loadTree(File saveFile){
        //TODO deserialize savefile and return retrieved TreeFile object
        return null;
    }
}
