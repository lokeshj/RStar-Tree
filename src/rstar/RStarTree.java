package rstar;

import rstar.dto.PointDTO;
import rstar.dto.TreeDTO;
import rstar.interfaces.IDtoConvertible;
import rstar.interfaces.ISpatialQuery;
import rstar.spatial.HyperRectangle;
import rstar.spatial.SpatialPoint;
import util.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:29 AM
 */
public class RStarTree implements ISpatialQuery, IDtoConvertible {

    private int dimension;
    private int pagesize;
    private File saveFile;
    private StorageManager storage;
    private RStarNode root;
    private long rootPointer = -1;

    private float _pointSearchResult = -1;
    private ArrayList<SpatialPoint> _rangeSearchResult;
    private ArrayList<SpatialPoint> _knnSearchResult;

    public RStarTree() {
        init(Constants.TREE_FILE, Constants.DIMENSION, Constants.PAGESIZE);
    }

    public RStarTree(int dimension) {
        init(Constants.TREE_FILE, dimension, Constants.PAGESIZE);
    }

    public RStarTree(String saveFile, int dimension, int pagesize) {
        init(saveFile, dimension, pagesize);
    }

    private void init(String saveFile, int dimension, int pagesize){
        this.dimension = dimension;
        this.pagesize = pagesize;
        this.saveFile = new File(saveFile);
        this.storage = new StorageManager();
        initStorage();
        setCapacities();
    }

    public RStarTree(String saveFile) {
        dimension = Constants.DIMENSION;
        pagesize = Constants.PAGESIZE;
        this.saveFile = new File(saveFile);
        this.storage = new StorageManager();
        initStorage();
        setCapacities();
    }

    private void initStorage() {
        loadTree();
        createDataDir();
    }

    private void setCapacities(){
        Constants.DIMENSION = dimension;
        //TODO add cost for mbr
        Constants.MAX_CHILDREN = Constants.PAGESIZE/8;          // M = (pagesize - mbr_size)/ (size of Long = 8)
        Constants.MIN_CHILDREN = Constants.MAX_CHILDREN/3;      // m = M/3
    }

    /* QUERY FUNCTIONS */

    /**
     * inserts a point in the tree and saves it on disk
     * @param point the point to be inserted
     * @return 1 if successful, else -1
     */
    @Override
    public int insert(SpatialPoint point) {
        //TODO insert
        System.out.println("inserting point with oid=" + point.getOid());
        loadRoot();
        if (root.isLeaf()) {
            if (root.isNotFull()) {
                //insert in root
                int status = root.insert(point);
                if(status == 1) {
                    storage.saveNode(root);
                } else {
                    System.out.println("failed to insert");
                }

                return status;
            } else {
                System.out.println("node full");
                return -1;
            }
        } else {
            System.out.println("root is not leaf");
            return -1;
        }
    }

    /**
     * searches for a spatial point in the tree and
     * returns its oid if its found.
     * @param point the point to be searched
     * @return oid of the point if found, else -1.
     */
    @Override
    public float pointSearch(SpatialPoint point) {
        System.out.println("searching point :" + point);
        _pointSearchResult = -1;
        loadRoot();
        _pointSearch(root, point);
        return _pointSearchResult;
    }

    private void _pointSearch(RStarNode start, SpatialPoint point) {
        HyperRectangle searchRegion = new HyperRectangle(dimension);
        searchRegion.update(point);
        HyperRectangle intersection = start.getMBR().getIntersection(searchRegion);

        if(intersection != null) {
            if (start.isLeaf()) {
                float[] searchPoints = point.getCords();

                //lazy loading of child points
                for (Long pointer : root.childPointers) {
                    PointDTO dto = storage.loadPoint(pointer);

                    float[] candidates = dto.coords;
                    boolean found = true;
                    for (int i = 0; i < candidates.length; i++) {
                        if (candidates[i] != searchPoints[i])
                            found = false;
                        break;
                    }
                    if (found) {
                        _pointSearchResult = dto.oid;
                    }
                }
            } else {
                for (Long pointer : root.childPointers) {
                    if(_pointSearchResult != -1)         // point found
                        break;

                    try {
                        RStarNode childNode = storage.loadNode(pointer);    //recurse down
                        _pointSearch(childNode, point);

                    } catch (FileNotFoundException e) {
                        System.err.println("Exception while loading node from disk");
                    }
                }
            }
        }
    }

    /**
     * searches for points in the given range of the center point
     * @param center center point of the search region.
     * @param range radius of the search region.
     * @return ArrayList of all the points found in the range
     */
    @Override
    public List<SpatialPoint> rangeSearch(SpatialPoint center, double range) {
        System.out.println("searching in range " + range + " of point: " + center);

        float[] points = center.getCords();
        float[][] mbrPoints = new float[dimension][2];
        for (int i = 0; i < dimension; i++) {
            mbrPoints[i][0] = points[i] + (float) range;
            mbrPoints[i][1] = points[i] - (float) range;
        }
        HyperRectangle searchRegion = new HyperRectangle(dimension);
        searchRegion.setPoints(mbrPoints);

        _rangeSearchResult = new ArrayList<SpatialPoint>();
        loadRoot();
        _rangeSearch(root, searchRegion);
        return _rangeSearchResult;
    }

    private void _rangeSearch(RStarNode start, HyperRectangle searchRegion) {
        HyperRectangle intersection = start.getMBR().getIntersection(searchRegion);
        if (intersection != null) {
            if (start.isLeaf()) {
                for (Long pointer : root.childPointers) {
                    PointDTO dto = storage.loadPoint(pointer);
                    SpatialPoint spoint = new SpatialPoint(dto);
                    HyperRectangle pointMbr = new HyperRectangle(dto.coords);

                    if(pointMbr.getIntersection(searchRegion) != null)
                        _rangeSearchResult.add(spoint);
                }
            }
            else {
                for (Long pointer : root.childPointers) {
                    try {
                        RStarNode childNode = storage.loadNode(pointer);    //recurse down
                        _rangeSearch(childNode, searchRegion);

                    } catch (FileNotFoundException e) {
                        System.err.println("Exception while loading node from disk");
                    }
                }
            }
        }
    }

    /**
     * searches for the k nearest neighbours of a center point
     * @param center SpatialPoint
     * @param k number of nearest neighbours required
     * @return ArrayList of the k nearest neighbours of center.
     */
    @Override
    public List<SpatialPoint> knnSearch(SpatialPoint center, int k) {
        System.out.println("knn search with k = "+k+" and point: "+center);
        _knnSearchResult = new ArrayList<SpatialPoint>();
        //TODO knnsearch
        return _knnSearchResult;
    }

    private void _knnSearch(RStarNode start, SpatialPoint point, int k) {

    }
    /*
     ***** DISK RELATED FUNCTIONS ****
     */

    /**
     * loads root from disk if exists
     * otherwise creates a new LeafNode and
     * assigns it root.
     */
    private void loadRoot() {
        if (root == null) {
            //empty tree
            root = loadNode(rootPointer);
            if (root == null)            // still null -> empty tree
            {
                root = new RStarLeaf(dimension);
            }
            rootPointer = root.getNodeId();
        }
    }

    /**
     * loads Nodes from disk using their nodeId
     * @param nodeId the nodeId attribute of the Node
     *               to be loaded
     * @return the Node required, null uf it doesn't exist
     */
    private RStarNode loadNode(long nodeId) {
        //check for valid nodeId
        if (nodeId != -1) {
            try {
                return storage.loadNode(nodeId);
            } catch (FileNotFoundException e) {
                System.err.println("Error while loading R* Tree node from file " + storage.constructFilename(nodeId));
            }
        }
        return null;
    }

    /**
     * saves the tree details to disk
     * @return 1 if successful, -1 otherwise
     */
    public int save() {
        return storage.saveTree(this.toDTO(), saveFile);
    }

    /**
     * converts this tree to its DTO representation
     * which in turn can be saved to disk.
     * @return TreeDTO object which is the DTO form of
     * this tree
     */
    @Override
    public TreeDTO toDTO() {
        return new TreeDTO(dimension, Constants.PAGESIZE, rootPointer);
    }

    private void createDataDir() {
        // check for the node-data directory. create one if doesn't exist
        File dataDir = new File(saveFile.getParentFile(), Constants.TREE_DATA_DIRECTORY);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            if (!dataDir.mkdir()) {
                System.err.println("Failed to create data directory of the tree. Exiting..");
                System.exit(1);
            }
            System.out.println("Data directory created");
        }
    }

    private void loadTree() {
        if (saveFile.exists() && saveFile.length() != 0) {
            try {
                TreeDTO treeData = storage.loadTree(saveFile);
                if (treeData != null) {             //update tree fields from saveFile
                    this.dimension = treeData.dimension;
                    this.pagesize = treeData.pagesize;
                    this.rootPointer = treeData.rootPointer;
                    System.out.printf("Tree loaded successfully from %s. dimension = %d and pagesize = %d bytes%n",
                            saveFile.getName(), dimension, pagesize);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Failed to load R* Tree from "+saveFile.getName());
            }

        }
    }
}
