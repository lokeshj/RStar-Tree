package rstar;

import java.io.File;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:29 AM
 */
public class RStarTree implements IRStarQuery {

    public RStarTree() {
        //TODO
    }

    public RStarTree(String datafile, int dimension, int capacity) {
        //TODO
        this.dimension = dimension;
        this.capacity = capacity;
        this.dataFile = new File(datafile);
        initStorage();
    }

    private void initStorage() {
        if (dataFile.exists() && dataFile.length() != 0) {
            int loadStatus = loadTreeFromFile(dataFile);
            if (loadStatus == -1)
                storage = new StorageManager();
        }
        else storage = new StorageManager();
    }

    private int loadTreeFromFile(File file) {
        //TODO load dimension, capacity, storageManager from datafile and r
        return 1;
    }

    @Override
    public int insert(SpatialPoint point) {
        loadRoot();
        //TODO
        return 1;
    }

    @Override
    public float pointSearch(SpatialPoint point) {
        //TODO
        return 1;
    }

    @Override
    public SpatialPoint[] rangeSearch(SpatialPoint center, double range) {
        //TODO
        return null;
    }

    @Override
    public SpatialPoint[] knnSearch(SpatialPoint center, int k) {
        //TODO
        return null;
    }

    private void loadRoot() {
        if (root == null) {
            //empty tree
            root = storage.loadNode(root);
            if(root == null)            // still null -> empty tree
                root = new RStarLeaf(dimension);
        }
        storage.addNode(root);
    }

    private IRStarNode root;
    private int dimension;
    private int capacity;
    private File dataFile;
    private StorageManager storage;

}
