package apps

import java.awt.Component
import java.awt.event.MouseListener
import javax.swing.JTree
import javax.swing.ImageIcon
import javax.swing.tree.*
import javax.swing.event.TreeSelectionListener
import pen.Log
import pen.eco.*

/** An implementation of JTree, used for selecting products. */
class KProductTree (treeSelectionHandler : TreeSelectionListener, mouseHandler : MouseListener) : JTree()
{
   var treeTop : DefaultMutableTreeNode         = DefaultMutableTreeNode( KProductInfo() )
   var model : DefaultTreeModel                 = DefaultTreeModel( treeTop )
   var searchResult                             = ArrayList<DefaultMutableTreeNode>(0)
   var searchResultIndex                        = 0

   init
   {
      model = DefaultTreeModel( treeTop )
      setModel( model )
      getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION )
      setRootVisible( true )
      setShowsRootHandles( true )

      val leafIcon : ImageIcon? = ImageIcon( "${Constants.ICONS_DIR}${Constants.SLASH}package-x-generic.png" )
      val openIcon : ImageIcon? = ImageIcon( "${Constants.ICONS_DIR}${Constants.SLASH}folder-open.png" )
      val closedIcon : ImageIcon? = ImageIcon( "${Constants.ICONS_DIR}${Constants.SLASH}folder.png" )
      val analogueIcon : ImageIcon? = ImageIcon( "${Constants.ICONS_DIR}${Constants.SLASH}user-bookmarks.png" )

      object : DefaultTreeCellRenderer()
      {
         @Override
         fun getTreeCellRendererComponent( t : JTree, dmt : Object, s : Boolean, e : Boolean, isLeaf : Boolean, r : Int, f : Boolean) : Component
         {
            super.getTreeCellRendererComponent( t, dmt, s, e, isLeaf, r, f )
            if (!isLeaf && ((dmt as DefaultMutableTreeNode).getUserObject() as KProductInfo).analogue && analogueIcon != null)
               setIcon( analogueIcon )
            return this
         }
      }.apply {
         if (leafIcon != null)
            setLeafIcon( leafIcon )
         if (openIcon != null)
            setOpenIcon( openIcon )
         if (closedIcon != null)
            setClosedIcon( closedIcon )
      }.also {
         setCellRenderer( it )
      }

//      setLargeModel( true )                                                   // Set to true when handling large trees.
      addMouseListener( mouseHandler )
      addTreeSelectionListener( treeSelectionHandler )
   }

   /** Loads an product tree XML file.
     * @return null if load failed. */
   fun load (filename : String) : Int?
   {
      Log.debug( "Loading product tree $filename" )
      return ProductTreeParser.parse( filename )?.let {
         model.setRoot( it )
         expand( it )
         treeTop = it
         0
      }
   }

   fun productInfo (product : Product, dmt : DefaultMutableTreeNode = treeTop) : ProductInfo
   {
      var ret : ProductInfo = NoProductInfo()
      val children = dmt.children()

      while (ret is NoProductInfo && children.hasMoreElements())
         ret = productInfo( product, children.nextElement() as DefaultMutableTreeNode )

      val productInfo = dmt.getUserObject() as KProductInfo
      if (productInfo.id == product.id)
         ret = KQuantableProductInfo( productInfo, product.qty )

      return ret
   }

   /** Searches product tree for a search term, expands graphically down to the nodes found and selects the first one.
     * @param dmt The tree/subtree to search.
     * @param includeDescription Include product descriptions in the search. */
   fun search (searchTerm : String, dmt : DefaultMutableTreeNode, includeDescription : Boolean = false) : ArrayList<DefaultMutableTreeNode>
   {
      val ret = ArrayList<DefaultMutableTreeNode>()
      searchNode( searchTerm, dmt, ret, includeDescription )
      return ret
   }
   /** Recursive function to do the actual searching. */
   private fun searchNode (searchTerm : String, dmt : DefaultMutableTreeNode, result : MutableList<DefaultMutableTreeNode>, includeDescription : Boolean) : Unit
   {
      val children = dmt.children()

      while (children.hasMoreElements())
         searchNode( searchTerm, children.nextElement() as DefaultMutableTreeNode, result, includeDescription )

      if ((dmt.getUserObject() as KProductInfo).name.toLowerCase().indexOf( searchTerm.toLowerCase() ) >= 0)
         result += dmt
      else if (includeDescription)
         if ((dmt.getUserObject() as KProductInfo).desc.toLowerCase().indexOf( searchTerm.toLowerCase() ) >= 0)
            result += dmt
   }

   fun resetSearch ()
   {
      searchResult = ArrayList<DefaultMutableTreeNode>(0)
      searchResultIndex = 0
   }

   /** Looks to see if the the product is in a leaf or it has parent that is analogue.
     * @param treeNode the TreeNode containing the product.
     * @return True if leaf or analogue. */
   fun isAChoosableProduct (treeNode : TreeNode) : Boolean
   {
      var ret = false

      if (treeNode.isLeaf)
         ret = true
      else
      {
         var n : TreeNode? = treeNode
         while (ret == false && n != null)
         {
            if (((n as DefaultMutableTreeNode).getUserObject() as KProductInfo).analogue)
               ret = true

            if (treeNode.isLeaf)
               ret = true
            else
               n = n.getParent()
         }
      }

      return ret
   }

   /** Collapses all graphical nodes bottom up.
     * @param dmt The tree/subtree to collapse. */
   fun collapseAll (dmt : DefaultMutableTreeNode)
   {
      val children = dmt.children()

      if (!dmt.isLeaf())
         while (children.hasMoreElements())
            collapseAll( children.nextElement() as DefaultMutableTreeNode )

      collapsePath(TreePath( dmt.getPath() as Array<Object> ))
   }
   //fun collapseAllFaster(){val ui=getUI();setUI(null);collapseAll(treeTop);setUI(ui)}

   /** Does a one level expansion of the graphical tree.
     * @param dmt The tree/subtree to expand. */
   fun expand (dmt : DefaultMutableTreeNode)
   {
      val children = dmt.children()

      while (children.hasMoreElements())
         scrollPathToVisible(TreePath( ((children.nextElement() as DefaultMutableTreeNode).getPath()) as Array<Object> ))
   }
   fun showNode (dmt : DefaultMutableTreeNode) = setSelectionPath(TreePath( dmt.getPath() as Array<Object> ))
}
