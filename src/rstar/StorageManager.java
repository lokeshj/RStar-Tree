package rstar;

import rstar.dto.TreeDTO;
import rstar.interfaces.IDiskQuery;
import rstar.interfaces.IRStarNode;
import util.Constants;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:33 AM
 */
public class StorageManager implements IDiskQuery {
    RandomAccessFile curFile;
    FileChannel channel;


    public StorageManager() {
        //TODO
    }

    @Override
    public void save(IRStarNode node) {
        //TODO serialize node and save to file
        if (node.isLeaf()) {

        } else {

        }
    }

    @Override
    public IRStarNode load(long nodeId) throws FileNotFoundException {
        return nodeFromDisk(constructFilename(nodeId));
    }

    private IRStarNode nodeFromDisk(String filename) throws FileNotFoundException {
        IRStarNode node = null;
        getChannel(new File(filename));
        //TODO read file and unserialize object, tc of exceptions
        return null;
    }

    public String constructFilename(long nodeId) {
        String file = Constants.FILE_PREFIX + nodeId + Constants.FILE_SUFFIX;
        return file;
    }

    @Override
    public int saveTree(TreeDTO tree, File saveFile) {
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

    @Override
    public TreeDTO loadTree(File saveFile) throws FileNotFoundException {
        //TODO deserialize savefile and return retrieved TreeFile object
        getChannel(saveFile);
        return null;
    }

    private void getChannel(File node) throws FileNotFoundException {
        curFile = new RandomAccessFile(node, "rw");
        channel = curFile.getChannel();
    }
}
