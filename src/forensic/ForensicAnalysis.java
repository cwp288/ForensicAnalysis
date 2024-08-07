package forensic;

import java.util.LinkedList;

/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 * @author Kal Pandit
 */
public class ForensicAnalysis {

    private TreeNode treeRoot;            // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     *      Use the BST insertion algorithm from class to insert.
     * 
     * DO NOT EDIT this method, IMPLEMENT buildSingleProfile and insertPerson.
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {
        // DO NOT EDIT THIS CODE
        StdIn.setFile(filename); // DO NOT remove this line

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /** 
     * Reads ONE profile from input file and returns a new Profile.
     * Do not add a StdIn.setFile statement, that is done for you in buildTree.
    */
    public Profile createSingleProfile() {
        int i = StdIn.readInt();
        STR[] dude = new STR[i];
        int count = 0;
        while(count < i){
            String strString = StdIn.readString();
            int occurrence = StdIn.readInt();
            STR bro = new STR(strString, occurrence);
            dude[count] = bro;
            count++;
        }
        Profile cullen = new Profile(dude);
        return cullen; // update this line
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * USE the compareTo method on keys.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {
        TreeNode cullen = new TreeNode(name, newProfile, null, null);
        if(treeRoot == null){
            treeRoot = cullen;
        } else{
            TreeNode ptr = treeRoot;
            TreeNode temp = null;
            while(ptr != null){
                if(ptr.getName().compareTo(name) < 0){
                    temp = ptr;
                    ptr = ptr.getRight();
                    if(ptr == null){
                        temp.setRight(cullen);
                        break;
                    }
                } else if (ptr.getName().compareTo(name) > 0) {
                    temp = ptr;
                    ptr = ptr.getLeft();
                    if(ptr == null){
                        temp.setLeft(cullen);
                        break;
                        }
                    } 
                }
            }
        }

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */
    public int getMatchingProfileCount(boolean isOfInterest) {
        return matching(treeRoot, isOfInterest); 

    }
    
    private int matching(TreeNode n, boolean m){
        if (n == null){
            return 0;
        }
        int c = 0;
        if(n != null){
            c += matching(n.getLeft(), m);
            if(n.getProfile().getMarkedStatus() == m){
                c += 1;
            }
            c += matching(n.getRight(), m);
        }
        return c;
    } 

    

    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * Provided method - DO NOT UPDATE.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {
        
        // DO NOT EDIT THIS CODE
        
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    public void flagProfilesOfInterest() { // WHy god why cant we make this recursive
     Queue<TreeNode> queue = new Queue<>();
    queue.enqueue(treeRoot);
    while (!queue.isEmpty()) {
        TreeNode node = queue.dequeue();
        int matching = 0;
        STR[] strs = node.getProfile().getStrs();
        for (int i = 0; i < strs.length; i++) {
            String strString = strs[i].getStrString();
            int m = numberOfOccurrences(firstUnknownSequence, strString) + numberOfOccurrences(secondUnknownSequence, strString);

            if (m == strs[i].getOccurrences()) {
                matching++;
            }
        }
        if (matching >= Math.ceil(strs.length / 2.0)) {
            node.getProfile().setInterestStatus(true);
        }
        if (node.getLeft() != null) {
            queue.enqueue(node.getLeft());
        }
        if (node.getRight() != null) {
            queue.enqueue(node.getRight());
        }
    }
}

    /*
      while(ptr != null){ // Change parameters, I dont think it works, traversal
            temp = ptr.getProfile(); //Checking the one I'm on. 
            tempSTRS = temp.getStrs(); // Array of STRS
            int i = 0;
            while(i < tempSTRS.length){
            if(tempSTRS[i].getOccurrences() % 2 == 1){ //Checks if its odd 
                numofoccroundup = tempSTRS[i].getOccurrences() /2 + 1; // Rounds up
            } else{
                numofoccroundup = tempSTRS[i].getOccurrences() /2; // If its even dont round up.
            }
                numtemp = numberOfOccurrences(firstUnknownSequence, tempSTRS[i].getStrString());
                numtemp += numberOfOccurrences(secondUnknownSequence, tempSTRS[i].getStrString());
                i++;
            }
            if(numtemp == numofoccroundup){ // Change the interest status, if they are equal
                temp.setInterestStatus(true);
            }
            if(ptr.getName().compareTo(ptr.getLeft().getName()) < 0){ // Traverse probably
                ptr = ptr.getRight();

            } else if(ptr.getName().compareTo(ptr.getRight().getName()) > 0){
                ptr = ptr.getLeft();
            }
        }



        TreeNode ptr = treeRoot;
        STR[] tempSTRS = null;
        String strString = "";
        int numtemp = 0;
       while(ptr != null){
        if(ptr.getLeft() == null){ // If we have visted the first node. Then go right
            tempSTRS = ptr.getProfile().getStrs();
            int matching = 0;
            for(int i = 0; i < tempSTRS.length; i++){
                strString = tempSTRS[i].getStrString();
                numtemp = numberOfOccurrences(firstUnknownSequence, strString) + numberOfOccurrences(secondUnknownSequence, strString);
                if(numtemp == tempSTRS[i].getOccurrences()){
                    matching ++;
                }
            }
            if(matching >= Math.ceil(tempSTRS.length/2.0)){
                ptr.getProfile().setInterestStatus(true);
            }
            ptr = ptr.getRight();
        } else{
            TreeNode predessor = ptr.getLeft();
            while(predessor.getRight() != null && predessor.getRight() != ptr){
                predessor = predessor.getRight();
                if(predessor.getRight() == null){
                    predessor.setRight(ptr);
                    ptr.setLeft(ptr);
                } else{
                    predessor.setRight(null);
                    tempSTRS = ptr.getProfile().getStrs();
                    for(int i = 0; i < tempSTRS.length; i++){
                    strString = tempSTRS[i].getStrString();
                    numtemp = numberOfOccurrences(firstUnknownSequence, strString) + numberOfOccurrences(secondUnknownSequence, strString);
                    if(numtemp == tempSTRS[i].getOccurrences()){
                    matching ++;
                }
            }
            if(matching >= Math.ceil(tempSTRS.length/2.0)){
                ptr.getProfile().setInterestStatus(true);
            }
                    ptr = ptr.getRight();
                }
            }
        }
       }
       


       
     */
    /**
     * Uses a level-order traversal to populate an array of unmarked Strings representing unmarked people's names.
     * - USE the getMatchingProfileCount method to get the resulting array length.
     * - USE the provided Queue class to investigate a node and enqueue its
     * neighbors.
     * 
     * @return the array of unmarked people
     */
    public String[] getUnmarkedPeople() {
        String[] list = new String[getMatchingProfileCount(false)];//Names
        Queue<TreeNode> queue = new Queue<>(); // Holder
        queue.enqueue(treeRoot);
        int i = 0;
        while(!queue.isEmpty()){
            TreeNode temp = queue.dequeue();
            if(temp.getProfile().getMarkedStatus() == false){
                list[i] = temp.getName();
                i++;
            }
            if(temp.getLeft() != null){
                queue.enqueue(temp.getLeft());
            }
            if(temp.getRight() != null){
                queue.enqueue(temp.getRight());
            }
        }
        return list; // update this line
    }

    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name (Last, First)
     * This is similar to the BST delete we have seen in class.
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * You may assume that all names are distinct.
     * 
     * @param fullName the full name of the person to delete
     */
    public void removePerson(String fullName) {
        treeRoot = remove(treeRoot, fullName);
    }
    private TreeNode remove(TreeNode node, String fullName) {
        if (node == null) {
            return null; // Base case: Tree is empty or person not found
        }
        if (node.getName().compareTo(fullName) == 0) {
            // Node to be removed found
            if (node.getLeft() == null && node.getRight() == null) {
                // Case 1: Node is a leaf node
                return null;
            } else if (node.getLeft() == null) {
                // Case 2: Node has only a right child
                return node.getRight();
            } else if (node.getRight() == null) {
                // Case 3: Node has only a left child
                return node.getLeft();
            } else {
                // Case 4: Node has both left and right children
                TreeNode successor = findMinNode(node.getRight());
                node.setName(successor.getName());
                node.setProfile(successor.getProfile());
                node.setRight(remove(node.getRight(), successor.getName()));
                return node;
            }
        } else if (node.getName().compareTo(fullName) < 0) {
            // Traverse to the right subtree
            node.setRight(remove(node.getRight(), fullName));
        } else {
            // Traverse to the left subtree
            node.setLeft(remove(node.getLeft(), fullName));
        }
    
        return node;
    }
    
    private TreeNode findMinNode(TreeNode node) {
        if (node.getLeft() == null) {
            return node;
        }
        return findMinNode(node.getLeft());
    }
   
    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        String[] list = getUnmarkedPeople();
        for(int i = 0; i < list.length; i++){
            removePerson(list[i]);
        }
    }

    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
