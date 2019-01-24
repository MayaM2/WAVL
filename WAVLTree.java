/**
*
* WAVLTree
*
* An implementation of a WAVL Tree with
* distinct integer keys and info.
*
*/

public class WAVLTree { 
	public WAVLNode root;
	public WAVLNode min;
	public WAVLNode max;
	public int size;
	
	public WAVLTree() { //Complexity O(1)
		this.root = null;
		this.min = null;
		this.max = null;
		this.size = 0;
		
	}

 /**
  * public boolean empty()
  *
  * returns true if and only if the tree is empty
  *
  */
	
	public WAVLNode wavl_getroot() { //Complexity O(1)
		return this.root;
}
 public boolean empty() { //Complexity O(1)
	if(this.root == null) {
		return true;
	 }
   return false; 
 }

/**
  * public String search(int k)
  *
  * returns the info of an item with key k if it exists in the tree
  * otherwise, returns null
  */
 public String search(int k) //Complexity O(log(n)) when n is number of nodes in tree
 {
	  WAVLNode node = this.root;
	  while((node!=null)&& (node.external == false)) { // we will go through real nodes only
		  if(k == node.key) { //correct node was found
			  return node.info;
		  } else if(k < node.key) {
			  node = node.left;
		  } else {
			  node = node.right;
		  }
	  }
	  return null;
 }

 /**
  * public int insert(int k, String i)
  *
  * inserts an item with key k and info i to the WAVL tree.
  * the tree must remain valid (keep its invariants).
  * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
  * returns -1 if an item with key k already exists in the tree.
  */
  public int insert(int k, String i) { //Complexity O(log(n)) when n is number of nodes in tree
	  if(this.search(k)!=null) {
		  return -1;
	  }
	  if(this.empty()== true) { // in case we want to insert the first node to an empty tree
		  this.root = new WAVLNode();	
		  this.root.rank = 0;
		  this.root.key = k;
		  this.root.info = i;
		  this.root.external = false;
		  this.root.right = new WAVLNode();
		  this.root.left = new WAVLNode();
		  this.root.right.parent = this.root;
		  this.root.left.parent = this.root;
		  this.min = this.root; // initiate min amd max to the only node in tree (the root)
		  this.max = this.root;
		  this.size = 1;
		  return 0;
		  }
	  WAVLNode node = this.root; // find the right place in tree to insert the new node, and call it "node
	 this.size+=1;
	  while(node.external == false) {
		  if(node.key > k) {
			  node = node.left;
			  }else{
				  node = node.right;
				   }
			   }
	  
	  node.external = false; // initiate the node which used to be an external node
	  node.key = k;
	  node.info = i;
	  node.rank = 0;
	  node.right = new WAVLNode();
	  node.left = new WAVLNode();
	  node.right.parent = node;
	  node.left.parent = node;
	  
	  if(k<this.min.key) { // updaet min and max if neseccery
		  this.min= node;
	  }
	  if(k>this.max.key) {
		  this.max= node;
	  }
	  
	  int counter = 0; // sums: promotion + demotion + rotation + 2*(each double-rotation)
	  node = node.parent;
	  int s = status(node); // status function returns an int representing the type of rebalance needed for the current node.
	  while(s!=0) { //status = 0 means no problem after insertion
		  if(s==1) { //status = 1 means (0,1) illegal - sent to promotion
			  rebalance1(node);
			  counter+=1;
			  if(node.parent==null) {
				  return counter; // can exit because balance is fixed
			  }
			  node = node.parent; // case after promotion there is still a problem
			  
		  }else if(s==2) { // status = 2 means single rotation needed
			  rebalance2(node);
			  counter+=1;
			  break;
		  } else { // status = 3 means double rotation needed
			  rebalance3(node);
			  counter+=2;
			  break;
		  }
		  s = status(node);
	  }
	  return counter;
	}
  
  public int status(WAVLNode node) { //Complexity is O(1)
	   //status is used in insertion
	   //status gets node (z), and checks the status of the node children to determine which balance should be made
	   //case 0 - nothing to change Wavl is legal
	   //case 1 - (0,1) illegal - sent to promotion
	   //case 2 - single rotation
	   //case 3 - double rotation
	   int diffL = node.rank - node.left.rank;
	   int diffR = node.rank - node.right.rank;
	   if((diffL==1&&diffR==2)|| (diffL==2&&diffR==1)|| (diffL==1&&diffR==1)|| (diffL==2&&diffR==2)){ //valid
		   return 0;
	   } else if(diffL + diffR == 1) { // case 1
		   return 1;
	   } else { // now we need to deciede between cases 2 and 3
		   if (diffL == 0) { //separates symmetry cases
			   if ((node.left.rank - node.left.left.rank) == 1) {
				   return 2; //rebalance 2 required
			   }
			   else {
				   return 3; //rebalance 3 required
			   }
		   }
		   else { // mirror case
			   if ((node.right.rank - node.right.right.rank) == 1) {
				   return 2; //rebalance 2 required
			   }
			   else {
				   return 3; //rebalance 3 required
			   }
		   }
			   
	   }
  }
  
  public void rebalance1(WAVLNode node) {//Complexity is O(1)
	   node.rank += 1;
  }
 
 public void rebalance2(WAVLNode node) { //Complexity is O(1)
	 //rebalance case of single rotation after insertion
	   if ((node.rank - node.left.rank) == 0) { // left diff 0 or right diff 0
		//node labeling "z", "x" etc. are same as in the WAVL presentation from the lecture
		   WAVLNode z = node;// defining all nodes which will take part in rotation
		   WAVLNode x = node.left;
		   WAVLNode b = node.left.right;
		   if(node.parent !=null) { // case node is not the root of the tree
			   WAVLNode parent = node.parent;
				// figure if z is a left child or a right child of his parent
				   if (parent.left.key == node.key) { // case left son
					   parent.left = x; 
				   }
				   else { // case right son
					   parent.right = x;
				   }
				   x.parent = parent;			   
		   }else { // case node was the root of the tree
			   this.root = x;
			   x.parent = null;
		   } // rest of rotation:
		   x.right = z;
		   z.parent = x;
		   b.parent = z;
		   z.left = b;
		   z.rank -= 1;
	   }
	   else { //symmetrical case
		 //node labeling "z", "x" etc. are same as in the WAVL presentation from the lecture
		   WAVLNode z = node;
		   WAVLNode x = node.right;
		   WAVLNode b = node.right.left;
		   if(z.parent!=null) { // case node is not the root of the tree
			   WAVLNode parent = node.parent;	
			// figure if z is a left child or a right child of his parent
			   if (parent.left.key == node.key) { // case left son
				   parent.left = x;
			   }
			   else { // case right son
				   parent.right = x;
			   }
			   x.parent = parent;   
		   } else { // case node was the root of the tree
			   this.root = x;
			   x.parent = null;
		   } // rest of rotation:
		   x.left = z;
		   z.parent = x;
		   b.parent = z;
		   z.right = b;
		   z.rank -= 1;
	   }
  }
 
 public void rebalance3(WAVLNode node) { //Complexity is O(1)
	// rebalance case of double rotation after insertion
	   if ((node.rank - node.left.rank) == 0) { // figures in which of symmetrical cases we are at.
		// node labeling "z", "x" etc. are same as in the WAVL presentation from the lecture
		   WAVLNode z = node; 
		   WAVLNode x = node.left;
		   WAVLNode b = node.left.right;
		   WAVLNode c = node.left.right.left;
		   WAVLNode d = node.left.right.right;
		   if(node.parent!= null) { // case node is not the root of the tree
			   WAVLNode parent = node.parent;
			// figure if z is a left child or a right child of his parent
			   if (parent.left.key == node.key) { // case left son
				   parent.left = b;
			   }
			   else { // case right son
				   parent.right = b;
			   } 
			   b.parent = parent;
		   }
		   else { // case node is the root of the tree
			   this.root = b;
			   b.parent = null; 
		   }
		   // rest of rotation:
		   b.left = x;
		   z.parent = b;
		   b.right = z;
		   x.right = c;
		   c.parent = x;
		   z.left = d;
		   d.parent = z;
		   x.parent = b;
		   b.rank += 1; // update ranks
		   x.rank -= 1;
		   z.rank -= 1;
	   }
	   else { //symmetrical case
		// node labeling "z", "x" etc. are same as in the WAVL presentation from the lecture
		   WAVLNode z = node;
		   WAVLNode x = node.right;
		   WAVLNode b = node.right.left;
		   if(node.parent!=null) { // case node is not the root of the tree
			   WAVLNode parent = node.parent; 
			   if (parent.left.key == node.key) { // left son
				   parent.left = b;// replace stuff
			   }
			   else { // right son of parent
				   parent.right = b;
			   }   
			   b.parent = parent;// replace stuff 
		   }
		   else { // case node is the root of the tree
			   this.root = b;
			   b.parent = null; 
		   }
		   WAVLNode c = node.right.left.right;
		   WAVLNode d = node.right.left.left;
		   // rest of rotation:
		   b.left = z;
		   z.parent = b;
		   b.right = x;
		   x.parent = b;
		   z.right = d;
		   d.parent = z;
		   x.left = c;
		   c.parent = x;
		   b.rank += 1; //update ranks
		   x.rank -= 1;
		   z.rank -= 1;
	   }
 }
  

 /**
  * public int delete(int k)
  *
  * deletes an item with key k from the binary tree, if it is there;
  * the tree must remain valid (keep its invariants).
  * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
  * returns -1 if an item with key k was not found in the tree.
  */
  public int delete(int k) // Complexity O(log(n)) when n is number of nodes in tree
  {
	   int counter = 0; // sums: promotion + demotion + rotation + 2*(each double-rotation)
	   WAVLNode node = this.searchNode(k);
	   if(node == null) {
		   return -1;
	   }
	   this.size -=1;
	   if(k == this.min.key) {
		   WAVLNode succNode = node.successor();
		   if(succNode!= null) {
			   this.min = succNode; 
		   }else {
			   this.max = node.predecessor();
		   }
		   
	   }
	   
	   if(k == this.max.key) {
		   WAVLNode preNode = node.predecessor();
		   if(preNode!=null) {
			   this.max = preNode;  
		   }else {
			   this.max = node.successor();
		   }  
	   }
	   
	   int situation = node.location(); // location function returns an int representing the part of the tree where the node is at.
	   if(situation ==3) { //location = 3 meaning node is in the middle of the tree
		   // replace key and info with successor
		   WAVLNode succs = node.successor();
		   int tempKey = succs.key;
		   String tempInf = succs.info;
		   succs.key = node.key;
		   succs.info = node.info;
		   node.key = tempKey;
		   node.info = tempInf;
		   node = succs;
		   situation = succs.location();
		   // now our node is either a leaf or a unary node
	   }
	   if(situation ==1) { //location = 1 meaning node is a leaf
		   int id = node.identifyLeaf(); // identifyLeaf returning an int indicating what is the rebalancing case needed
		   if (id == 0) { //our node is the only node in the tree
			   this.root = null;
			   return 0;
		   }
		   node.external=true;
		   node.rank = -1;
		   node = node.parent;
		   if(id==1) { // case deleting the node won't require additional rebalancing
			   return 0;
		   }if(id==2) { // case deleting the node requires demoting and might cause additional rebalancing
			  node.rank = 0;
			  counter+=1;
			  if(node.parent==null || node.parent.rank-node.rank==2) { // case there is no need for rebalancing
				  return counter;
			  } // in case id =3, additional rebalancing needed later on. 
		   } 
	   }else { // location = 2 meaning node is a unary node
		   int id = node.identifyUnary(); // identifyUnary returning an int indicating the case of unary deletion
		   if(node.left.external == false) { // case node has a left son 
			   node.left.external= true;	// delete left son, and insert its properties into node
			   node.left.rank= -1;
			   node.key = node.left.key;
			   node.info = node.left.info;
			   node.rank = 0;
				 }
		   else { // mirror case - node has right son
			   node.right.external= true; // delete right son, and insert its properties into node
			   node.right.rank= -1;
			   node.key = node.right.key;
			   node.info = node.right.info;
			   node.rank = 0;
			   }
		   if (id == -1) { // identifyUnary = -1 means node is root
			   return 0;
		   } 
		   if(id==1) { // identifyUnary = 1 means there is no balancing needed
			   return 0;
		   } else if (id == 0){  // identifyUnary = 0 means additional balancing needed
			   node = node.parent;   
		   }
	   } 
	   int rebalanceType;
	   while(true) {
		   rebalanceType = node.identifyBalance(); // identifyBalance returns an int indicating the kind of balancing needed after deletion
		   if (rebalanceType == 1) { // identifyBalance = 1 means demote needed
			   node = node.Balance1();// Balance1 implements demote
			   counter+=1;
			   if (node ==null) { // reached root and so balancing procedure ends
				   return counter;
			   }if(node.rank-node.left.rank == 1 || node.rank-node.left.rank==2) {
				   if(node.rank-node.right.rank == 1 || node.rank-node.right.rank==2 ) {
					   return counter;  // case no need to rebalance further
				   }   
			   }
		   }
		   else if(rebalanceType == 2) { // identifyBalance = 2 means double demote needed
			   node = node.Balance2(); // Balance2 implements double demote
			   counter+=2;
			   if (node ==null) {
				   return counter;
			   }if(node.rank-node.left.rank == 1 || node.rank-node.left.rank==2) { // checking case no need to rebalance further
				   if(node.rank-node.right.rank == 1 || node.rank-node.right.rank==2 ) {
					   return counter;  
				   }   
			   }
			}
		   else if(rebalanceType == 3) { // identifyBalance = 3 means rotation needed. is a final procedure.
			   WAVLNode mayberoot = node.Balance3(); // Balance3 implements rotation
			   if(mayberoot !=null) {
				   this.root = mayberoot; //Balance 3 doing in place balancing and returning the new root.
			   }
			   // we need to know if extra demotion is needed
			   if(node.rank-node.left.rank==1) { 
				   if(node.left.rank-node.left.left.rank==2) {
					   if(node.left.rank-node.left.right.rank==2) {
						   node.left.rank-=1;
						   return counter+2;
					   }
				   }
			   }
			   if(node.rank-node.right.rank==1) { // symmetrical case - checking if extra demotion is needed
				   if(node.right.rank-node.right.left.rank==2) {
					   if(node.left.rank-node.right.right.rank==2) {
						   node.right.rank-=1;
						   return counter+2;
					   }
				   }
			   }
			   return counter+1; // case no extra demotion is needed
		   }
		   else {  // identifyBalance = 4 means double rotation needed. is a final procedure.
			   node.Balance4(); // Balance4 implements double rotation
			   return counter+2;
		   }  
	   }
}

  
  public WAVLNode searchNode(int k) // Complexity is O(log(n)) when n is number of nodes in tree
  {
	  WAVLNode node = this.root;
	  while((node != null) && (node.external!=true)) {
		  if(k == node.key) {
			  return node;
		  } else if(k < node.key) {
			  node = node.left;
		  } else {
			  node = node.right;
		  }
	  }
	  return null;
  }
  
  /**
   * public String min()
   *
   * Returns the info of the item with the smallest key in the tree,
   * or null if the tree is empty
   */
  public String min() // Complexity O(1)
  {
	   if(this.empty()== true) {
		   return null;
	   }
	   return this.min.info;
  }

  /**
   * public String max()
   *
   * Returns the info of the item with the largest key in the tree,
   * or null if the tree is empty
   */
  public String max() // Complexity O(1)
  {
	   if(this.empty()== true) {
		   return null;
	   }
	   return this.max.info;
  }
 /**
  * public int[] keysToArray()
  *
  * Returns a sorted array which contains all keys in the tree,
  * or an empty array if the tree is empty.
  */
 public int[] keysToArray() //Complexity O(n) when n is number of nodes in tree
 {
	  if(this.empty()== true) {
		  int[] arr2 = new int[0];
		   return arr2 ;
	   }
	  int[] arr = new int[this.size];
	  WAVLNode node = this.min;
	  arr[0] = node.key;
	  for(int i=1; i<arr.length; i++) {
		  node = node.successor();
		  arr[i] = node.key;
	  }
	  return arr;
 }

 /**
  * public String[] infoToArray()
  *
  * Returns an array which contains all info in the tree,
  * sorted by their respective keys,
  * or an empty array if the tree is empty.
  */
 public String[] infoToArray() //Complexity O(n) when n is number of nodes in tree
 {
	  if(this.empty()== true) {
		  String[] arr2 = new String [0];
		   return arr2 ;
	   }
	  String[] arr = new String[this.size];
	  WAVLNode node = this.min;
	  arr[0] = node.info;
	  for(int i=1; i<arr.length; i++) {
		  node = node.successor();
		  arr[i] = node.info;
	  }
	  return arr;   
 }

  /**
   * public int size()
   *
   * Returns the number of nodes in the tree.
   *
   * precondition: none
   * postcondition: none
   */
  public int size() //Complexity O(1)
  {
	   if(this.empty()== true) {
		   return 0;
	   }
	   return this.size;
  }
  
    /**
   * public int getRoot()
   *
   * Returns the root WAVL node, or null if the tree is empty
   *
   * precondition: none
   * postcondition: none
   */
  public IWAVLNode getRoot() //Complexity is O(1)
  {
	   return this.root;
  }
    /**
   * public int select(int i)
   *
   * Returns the value of the i'th smallest key (return -1 if tree is empty)
   * Example 1: select(1) returns the value of the node with minimal key 
	* Example 2: select(size()) returns the value of the node with maximal key 
	* Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor 	
   *
	* precondition: size() >= i > 0
   * postcondition: none
   */   
  public String select(int i)  //Complexity O((log(n) + i) when n is number of nodes in tree
  {
	   if(this.empty()== true) {
		   return "-1";
	   }
	   WAVLNode node = this.min;
	   for(int j=1; j<i ; j++) {
		 node = node.successor();  
	   }
	   return node.getValue();
	   
 }

	/**
	   * public interface IWAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IWAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public IWAVLNode getLeft(); //returns left child (if there is no left child return null)
		public IWAVLNode getRight(); //returns right child (if there is no right child return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
		public int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))
	}

  /**
  * public class WAVLNode
  *
  * If you wish to implement classes other than WAVLTree
  * (for example WAVLNode), do it in this file, not in 
  * another file.
  * This class can and must be modified.
  * (It must implement IWAVLNode)
  */
	

	
 public class WAVLNode implements IWAVLNode{
	  public String info = null;
	  public int key = 0;
	  public int rank = -1;
	  public boolean external = true;
	  public WAVLNode parent = null;
	  public WAVLNode left = null;
	  public WAVLNode right = null;  
	  
		
		public boolean isLegalNode() {
			if(this.external== true) {
				return true;
			}
			if((this.rank-this.right.rank == 1) || (this.rank-this.right.rank == 2) ) {
				if((this.rank-this.left.rank == 1) || (this.rank-this.left.rank == 2) ) {
					return true;
				}
			}
			return false;
		}
	
	  public int location() { //Complexity is O(1)
		  //will return 1 for leaf 2 for unary 3 for middle of tree
		  if(this.left.external==true && this.right.external==true) {
			  return 1;
		  } else if(this.left.external==true || this.right.external==true) {
			  return 2;
		  } else {
			  return 3;
		  }
		  
	  }
	  
	  public int identifyLeaf() { //Complexity is O(1)
		  //gets y
		  if (this.parent == null) { //there is only one node in tree
			  return 0;
		  }
		  WAVLNode parent = this.parent;
		  if((parent.rank - parent.left.rank == 1) && (parent.rank - parent.right.rank == 1)) {
			  return 1; // case deleting the node won't require additional rebalancing
		  } else if(parent.rank==1) {
			  return 2; // case deleting the node requires demotion, and might require additional balancing after that.
		  }else {
			  return 3; // requires additional balancing without demotions at that stage.
		  }		  
	  }
	  
	  public int identifyUnary() { //Complexity is O(1)
		  // (gets unary node labeled "y" from delete function) 
		  WAVLNode parent = this.parent;
		  if (this.parent == null) { // case node is root
			  return -1;
		  } 
		  if(parent.rank==2) {
			  return 1; // case there is no balancing needed
		  } else {
			  return 0; // returns 0 if additional balancing needed
		  }  
	  }
	  
	  public int identifyBalance() { //Complexity is O(1)
			// gets node labeled "z" from delete function and returning an int representing what balancing procedure is needed.
			  if ((this.rank - this.left.rank == 2) || (this.rank - this.right.rank == 2)) {
				  return 1; // identifyBalance = 1 means demote needed.
			  }
			  WAVLNode y; // node labeled "y" representing the child of "z" with rank difference 1. 
			  if ((this.rank - this.left.rank) == 1) {// finding y
				  y = this.left;
			  } else {
				  y = this.right;
			  }
			  if ((y.external != true) && (y.rank - y.left.rank == 2) && (y.rank - y.right.rank == 2)) { //28 fix
				  return 2;  // identifyBalance = 2 means double demote needed
			  } else if ((y.rank - y.left.rank == 1) && (y.rank - y.right.rank == 1)) {
				  return 3; // identifyBalance = 3 means rotation needed. is a final procedure.
			  }
			// now we are left with node "y" that has (1,2) differance with his children
			  if (this.right.key == y.key) { // case y is right child to z
				  if (y.rank - y.left.rank == 2) {
					  return 3; 
				  }else
					  return 4; // identifyBalance = 4 means double rotation needed. is a final procedure.
			  }else { // case y is left child to z
				  if (y.rank - y.left.rank == 2) {
					  return 4; 
				  }else
					  return 3; 
			  }
		  }
	  
	  
	  public WAVLNode Balance1() { //Complexity is O(1)
		  // means demote needed, returns next node to take care of
		  this.rank -= 1;
		  return this.parent;  

	  }
	  
	  public WAVLNode Balance2() {//Complexity is O(1)
		// means double demote needed, returns next node to take care of
		  if (this.rank - this.right.rank == 1) {
			  this.rank -= 1;
			  this.right.rank -= 1;
			  
		  }else { // mirror case
			  this.rank -= 1;
			  this.left.rank -= 1;
			  
		  }
			 return this.parent;  
	  }
	  
	  public WAVLNode Balance3() { //Complexity is O(1)
		  // means rotation needed. is a final procedure.
		  // node labeling "z", "y" etc. are same as in the WAVL presentation from the lecture
		  WAVLNode z = this;
		
		  if (z.rank - z.left.rank == 1) {
			  WAVLNode y = z.left;
			  WAVLNode a = y.right;
			  z.rank -= 1; 
			  y.rank += 1;
			  
			 
			  if(z.parent!=null) { // case "z" is not root
				  if (z.parent.left.key == z.key) {  // check if "z" is right/left child of its parents and updade y accordingly
					  z.parent.left = y;
				  }else {
					  z.parent.right = y;
				  }
				  y.parent = z.parent; // rest of rotation
				  z.parent = y;  
				  y.right = z;
				  a.parent = z;
				  z.left = a;
				   return null;
			  }
			  y.parent = z.parent; // rotation case "z" is root
			  z.parent = y;  
			  y.right = z;
			  a.parent = z;
			  z.left = a;
			   return y; // returns new root to "delete" function
			   
		  } else { // symmetrical case
			  WAVLNode y = z.right;
			  WAVLNode a = y.left;
			  z.rank -= 1;
			  y.rank += 1;
			  
			  if(z.parent!= null) { // case "z" is not root
				  if (z.parent.left.key == z.key) { // check if "z" is right/left child of its parents and updade y accordingly
					  z.parent.left = y;
				  }else {
					  z.parent.right = y;
				  }
				  y.parent = z.parent;  // rest of rotation
				  z.parent = y;
				  y.left = z;
				  a.parent = z;
				  z.right = a;
				  return null;
			  }
			  y.parent = z.parent; // rotation case "z" is root
			  z.parent = y;
			  y.left = z;
			  a.parent = z;
			  z.right = a;
			  
			  return y; // returns new root to "delete" function
		  }
	  }
	  
	  public void Balance4() { //Complexity is O(1)
		// means double rotation needed. is a final procedure.
		// node labeling "z", "y" etc. are same as in the WAVL presentation from the lecture
		  WAVLNode z = this;
		  
		  if (z.rank - z.right.rank == 1) { // check if "y" is right son to "z" 
			  WAVLNode y = z.right;
			  WAVLNode a = y.left;
			  WAVLNode d = a.right;
			  WAVLNode c = a.left;
			  
			  if(z.parent!=null) { // case "z" is not root of tree
				  if (z.parent.left.key == z.key) { // check if "z" is a left son to its parent
					  z.parent.left = a;
				  } else { // case "z" is right son to its parent
					  z.parent.right = a;
				  }
				  //change connections according to double rotation
				  a.parent = z.parent;
				  a.left = z;
				  z.parent = a;
				  a.right = y;
				  y.parent = a;
				  y.left = d;
				  d.parent = y;
				  z.right = c;
				  c.parent = z;
				  z.rank -= 2; //update ranks
				  y.rank -= 1;
				  a.rank += 2;
			  }
			  else { // case "z" is root of tree
				  // updating rotation
				  int ztempkey = z.key;
				  WAVLNode x = z.left;
				  String ztempinfo = z.info;
				  z.info=a.info;
				  z.key=a.key;
				  a.info=ztempinfo;
				  a.key=ztempkey;
				  z.left=a;
				  a.parent=z;
				  a.left=x;
				  x.parent=a;
				  a.right=c;
				  c.parent=a;
				  y.left=d;
				  y.rank-=1;
				  
			  }
		  }
		   else { // symmetrical case - "y" is left son to "z"
			 
			  WAVLNode y = z.left;
			  WAVLNode a = y.right;
			  WAVLNode d = a.left;
			  WAVLNode c = a.right;
			  

			  if(z.parent!= null) { // case "z" is not root of tree 
				  if (z.parent.left.key == z.key) { // check if "z" is a left son to its parent
					  z.parent.left = a;
				  } else { // case "z" is right son to its parent
					  z.parent.right = a;
				  }
				  //change connections according to double rotation
				  a.parent = z.parent;
				  a.right = z;
				  z.parent = a;
				  a.left = y;
				  y.parent = a;
				  y.right = d;
				  d.parent = y;
				  z.left = c;
				  c.parent = z;
				  z.rank -= 2; //update ranks
				  y.rank -= 1; 
				  a.rank += 2;
			  }
			  else { // case "z" is root of tree
				  // updating rotation
				  int ztempkey = z.key;
				  WAVLNode x = z.right;
				  String ztempinfo = z.info;
				  z.info=a.info;
				  z.key=a.key;
				  a.info=ztempinfo;
				  a.key=ztempkey;
				  z.right=a;
				  a.parent=z;
				  a.right=x;
				  x.parent=a;
				  a.left=c;
				  c.parent=a;
				  y.right=d;
				  y.rank-=1; 
			  }
		  }		  
	  }
	  
	//gets node, returns successor node
	  public WAVLNode successor() { //Complexity is O(log(n)) = height of tree,  when n is number of nodes in tree 
		  
		  if(this.right.external != true) { //check if successor is within your right subtree
			  return this.right.minNode();
		  } // case successor is above the node
		  WAVLNode node = this; 
		  WAVLNode node2 = this.parent;
		  while(node2!= null && node == node2.right) { //while not in root and parent is smaller than you go up
			  node = node2;
			  node2 = node.parent;
		  }
			return node2; //return first parent that you were his left child, he is bigger than you
	 }
	  
	  public WAVLNode predecessor() {
		  if(this.left.external != true) { //check if predecessor is within your left subtree
			  return this.left.maxNode();
		  } // case predecessor is above the node
		  WAVLNode node = this; 
		  WAVLNode node2 = this.parent;
		  while(node2!= null && node == node2.left) { //while not in root and parent is larger than you go up
			  node = node2;
			  node2 = node.parent;
		  }
			return node2; //return first parent that you were his right child, he is smaller than you
	 }
	  
	  public WAVLNode minNode(){ // Complexity is O(log(n)) = height of tree,  when n is number of nodes in tree 
		  WAVLNode node = this;
		  if(node.left.external == true) {
			  return node;
		  } else {
			  while(node.left.external == false) {
				  node = node.left;
			  }
		  }
		  return node; 
	  }
	  
	  public WAVLNode maxNode(){ // Complexity is O(log(n)) = height of tree,  when n is number of nodes in tree 
		  WAVLNode node = this;
		  if(node.right.external == true) {
			  return node;
		  } else {
			  while(node.right.external == false) {
				  node = node.right;
			  }
		  }
		  return node; 
	  }
	  
		public int getKey() // Complexity is O(1)
		{
			if(this.external == true) {
				return -1;
			} else {
				return this.key;
			}
			
		}
		public String getValue() // Complexity is O(1)
		{
			if(this.external == true) {
				return null;
			} else {
				return this.info;
			}
		}
		public IWAVLNode getLeft() // Complexity is O(1)
		{
			if(this.left.external == true) {
				return null;
			} else {
				return this.left;
			}
		}
		public IWAVLNode getRight() // Complexity is O(1)
		{
			if(this.right.external == true) {
				return null;
			} else {
				return this.right;
			}
		}
		// Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
		public boolean isRealNode() // Complexity is O(1)
		{
			if(this.external==true) {
				return false;
			}else {
				return true;
			}
		}

		public int getSubtreeSize() // Complexity is O(n) when n is number if nodes in tree
		{
			if (this.right.external == false && this.left.external == false) {	//case node has two real nodes as children
				return 1 + this.left.getSubtreeSize() + this.right.getSubtreeSize();
			}
			else if (this.right.external == true && this.left.external == false) { //case node has one real child node (left)
				return 1 + this.left.getSubtreeSize();
			}
			else if (this.left.external == true && this.right.external == false) { //case node has one real child node (right)
					return 1 + this.right.getSubtreeSize();
					}
			else if (this.right.external == true && this.left.external == true){ // case node is a leaf
				return 1;
			}
			else
				return 0; //stop condition
			}
 }
}