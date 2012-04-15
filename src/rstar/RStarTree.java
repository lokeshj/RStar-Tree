package rstar;

import rstar.dto.PointDTO;
import rstar.dto.TreeDTO;
import rstar.interfaces.IDtoConvertible;
import rstar.interfaces.ISpatialQuery;
import rstar.nodes.RStarInternal;
import rstar.nodes.RStarLeaf;
import rstar.nodes.RStarNode;
import rstar.nodes.RStarSplit;
import rstar.spatial.HyperRectangle;
import rstar.spatial.SpatialPoint;
import util.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RStarTree implements ISpatialQuery, IDtoConvertible {

    private int dimension;
    private File saveFile;
    private StorageManager storage;
    private RStarNode root;
    private long rootPointer = -1;
    private RStarSplit splitManager;

    private float _pointSearchResult = -1;
    private ArrayList<SpatialPoint> _rangeSearchResult;
    private List<SpatialPoint> _knnSearchResult;
    private int bestSortOrder = -1;

    public RStarTree(int dimension) {
        this.dimension = dimension;
        this.saveFile = new File(Constants.TREE_FILE);
        this.storage = new StorageManager();
        this.splitManager = new RStarSplit(dimension, storage);

        storage.createDataDir(saveFile);
        setCapacities();
    }

    private void setCapacities(){
        Constants.DIMENSION = dimension;
//        Constants.MAX_CHILDREN = Constants.PAGESIZE/8;          // M = (pagesize - mbr_size)/ (size of Long = 8)
//        Constants.MIN_CHILDREN = Constants.MAX_CHILDREN/3;      // m = M/3
        Constants.MAX_CHILDREN = 10;
        Constants.MIN_CHILDREN = 4;
    }

    /* QUERY FUNCTIONS */

    /**
     * inserts a point in the tree and saves it on disk
     * @param point the point to be inserted
     * @return 1 if successful, else -1
     */
    @Override
    public int insert(SpatialPoint point) {
        System.out.println("inserting point with oid=" + point.getOid());
        RStarLeaf target = chooseLeaf(point);

        if (target.isNotFull()) {
            target.insert(point);
            storage.saveNode(target);
            //adjust root reference
            if (target.getNodeId() == rootPointer) {
                root = target;
            }
            adjustParentOf(target);
            return 1;
        } else {
            return treatLeafOverflow(target, point);
        }
    }

    /**
     * inserts a RStar node in the node pointed by nodePointer
     * @param nodePointer pointer to node in which the given node
     *                    is to be inserted
     * @param nodeToInsert the node to be inserted
     * @return 1 of successful, else -1
     */
    private int insertAt(Long nodePointer, RStarNode nodeToInsert) {
        storage.saveNode(nodeToInsert);
        RStarInternal target = (RStarInternal) loadNode(nodePointer);

        if (target.isNotFull()) {
            target.insert(nodeToInsert);

            if (target.getNodeId() == rootPointer) {
                root = target;
            }

            storage.saveNode(target);
            adjustParentOf(target);
            return 1;
        } else {
            return treatInternalOverflow(target, nodeToInsert);
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
        _pointSearchResult = -1;
        loadRoot();
        _pointSearch(root, point);
        return _pointSearchResult;
    }

    private void _pointSearch(RStarNode start, SpatialPoint point) {
        HyperRectangle searchRegion = new HyperRectangle(point.getCords());
        HyperRectangle intersection = start.getMBR().getIntersection(searchRegion);

        if(intersection != null) {
            if (start.isLeaf()) {
                float[] searchPoints = point.getCords();

                //lazy loading of child points
                for (Long pointer : start.childPointers) {
                    PointDTO dto = storage.loadPoint(pointer);

                    float[] candidates = dto.coords;
                    boolean found = true;
                    for (int i = 0; i < candidates.length; i++) {
                        if (candidates[i] != searchPoints[i]){
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        _pointSearchResult = dto.oid;
                        break;
                    }
                }
            } else {
                for (Long pointer : start.childPointers) {
                    if(_pointSearchResult != -1)         // point found
                        break;

                    try {
                        RStarNode childNode = storage.loadNode(pointer);    //recurse down
                        _pointSearch(childNode, point);

                    } catch (FileNotFoundException e) {
                        System.err.println("Exception while loading node from disk. message = "+e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * searches for points in the given range of the center point
     * @param center center point of the search region.
     * @param range radius of the search region.
     * @return List of all the points found in the range
     */
    @Override
    public List<SpatialPoint> rangeSearch(SpatialPoint center, double range) {

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
                for (Long pointer : start.childPointers) {
                    PointDTO dto = storage.loadPoint(pointer);
                    SpatialPoint spoint = new SpatialPoint(dto);
                    HyperRectangle pointMbr = new HyperRectangle(dto.coords);

                    if(pointMbr.getIntersection(searchRegion) != null)
                        _rangeSearchResult.add(spoint);
                }
            }
            else {
                for (Long pointer : start.childPointers) {
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
     * @return List of the k nearest neighbours of center.
     */
    @Override
    public List<SpatialPoint> knnSearch(SpatialPoint center, int k) {
        loadRoot();
        _knnSearch(root, center, k, 1);
        _rangeSearchResult = new ArrayList<SpatialPoint>();
        return _knnSearchResult;
    }

    private void _knnSearch(RStarNode start, SpatialPoint center, int k, float range) {
        _rangeSearchResult = new ArrayList<SpatialPoint>();

        float[] points = center.getCords();
        float[][] mbrPoints = new float[dimension][2];
        for (int i = 0; i < dimension; i++) {
            mbrPoints[i][0] = points[i] + range;
            mbrPoints[i][1] = points[i] - range;
        }
        HyperRectangle searchRegion = new HyperRectangle(dimension);
        searchRegion.setPoints(mbrPoints);

        _rangeSearch(start, searchRegion);

        if (_rangeSearchResult.size() < k) {
            _knnSearch(start, center, k, 2 * range);
        } else {
            final SpatialPoint fcenter = center;
            Comparator<? super SpatialPoint> paramComparator = new Comparator<SpatialPoint>() {
                @Override
                public int compare(SpatialPoint point1, SpatialPoint point2) {
                    float deltaDist = fcenter.distance(point1) - fcenter.distance(point2);
                    if(deltaDist == 0)
                        return 0;
                    else
                        return (int)(deltaDist /(Math.abs(deltaDist)));
                }
            };
            Collections.sort(_rangeSearchResult, paramComparator);
            _knnSearchResult = _rangeSearchResult.subList(0, k);
        }
    }

    private int treatLeafOverflow(RStarLeaf target, SpatialPoint point) {
        try {
            splitLeaf(target, point);
            return 1;
        } catch (AssertionError e) {
            return -1;
        }
    }

    private int treatInternalOverflow(RStarInternal fullNode, RStarNode newChild) {
        try {
            splitInternalNode(fullNode, newChild);
            return 1;
        } catch (AssertionError e) {
            return -1;
        }
    }

    /**
     * inserts point into and splits the target leafnode
     * @param splittingLeaf the leaf to split
     * @param newPoint the point to be inserted
     * @throws AssertionError when the target node does
     * not have any children
     */
    private void splitLeaf(RStarLeaf splittingLeaf, SpatialPoint newPoint) throws AssertionError {
        RStarLeaf newChild = splitManager.splitLeaf(splittingLeaf, newPoint);
        if (splittingLeaf.getNodeId() == rootPointer) {
            //we just split root
            root = splittingLeaf;
            createRoot(newChild);
        } else {
            newChild.setParentId(splittingLeaf.getParentId());
            insertAt(splittingLeaf.getParentId(), newChild);
        }
    }

    /**
     * splits an internal node and inserts a new node
     * @param splittingNode the node to be split
     * @param node the node to be inserted
     */
    private void splitInternalNode(RStarInternal splittingNode, RStarNode node) {
        RStarNode createdNode;
        try {
            createdNode = splitManager.splitInternalNode(splittingNode, node);
            if (splittingNode.getNodeId() == rootPointer) {
                //we just split root
                root = splittingNode;
                createRoot(createdNode);
            } else {
                createdNode.setParentId(splittingNode.getParentId());
                insertAt(splittingNode.getParentId(), createdNode);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Exception while loading node from disk. message: "+e.getMessage());
        }
    }

    /**
     * creates a new root and sets the old root (referred by this.root)
     * and siblingOfRoot its children
     * @param siblingOfRoot node created by splitting current root
     */
    private void createRoot(RStarNode siblingOfRoot) {
        RStarInternal newRoot = new RStarInternal(dimension);
        newRoot.setParentId(newRoot.getNodeId());
        newRoot.insert(root);
        newRoot.insert(siblingOfRoot);
        storage.saveNode(root);
        storage.saveNode(siblingOfRoot);
        storage.saveNode(newRoot);
        root = newRoot;
        rootPointer = newRoot.getNodeId();
    }

    /**
     * finds the most appropriate leaf node to
     * insert the newPoint into
     * @param newPoint the point to be inserted
     * @return RStarLeaf the most appropriate leaf to insert
     * newPoint
     */
    private RStarLeaf chooseLeaf(SpatialPoint newPoint) {
        loadRoot();
        SpatialPoint[] temp = new SpatialPoint[1];
        temp[0] = newPoint;
        return splitManager.chooseLeaf(root, new HyperRectangle(dimension, temp));
    }

    /**
     * updates mbr of all ancestor of a node
     * @param target updation starts from the parent of target
     */
    private void adjustParentOf(RStarNode target) {
        if (target.getNodeId() != rootPointer) {
            RStarNode parent = loadNode(target.getParentId());
            HyperRectangle mbr = parent.getMBR();
            mbr.update(target.getMBR());
            parent.setMbr(mbr);
            storage.saveNode(parent);
            if (parent.getNodeId() == rootPointer) {
                root = parent;
            }
            adjustParentOf(parent);
        }
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
                root.setParentId(root.getNodeId());
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
                if (nodeId == rootPointer) {
                    loadRoot();
                    return root;
                } else {
                    return storage.loadNode(nodeId);
                }
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

    /*private void loadTree() {
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
    }*/
}
