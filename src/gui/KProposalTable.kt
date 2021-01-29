package tedit.gui

import java.util.Vector
import java.awt.Font
import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.swing.DefaultListSelectionModel
import javax.swing.table.DefaultTableModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.tree.TreeNode
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.event.*
import pen.toLong
import pen.eco.KProduct
import pen.eco.KProductInfo
import pen.eco.KQuantableProductInfo
import tedit.utils.vectorize
import tedit.*
import tedit.session.KTenderDocument

/** A table for showing products and edit quantities. */
class KProposalTable (internal val tenderDocument : KTenderDocument) : JTable()
{
   /** The proposal table is added to this JScrollPane. */
   internal val tab = JScrollPane()
   internal var modified                               = false
   private var model : CustomTableModel?               = null
   private var isChanging                              = false                  // To mitigate effects of unwanted selection events.
   private var selectedProductNum                      = -1

   init
   {
      setFillsViewportHeight( true )
      setRowSelectionAllowed( true )
      setColumnSelectionAllowed( false )
      setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION )
      setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN )
      setFont(Font( getFont().getName(), Font.PLAIN, 13 ))

      val listSelectionModel = getSelectionModel()
      listSelectionModel.addListSelectionListener( ListSelectionHandler() )
      setSelectionModel( listSelectionModel )

      tab.setViewportView( this )
   }

   /** Resets the table using info from proposal. */
   internal fun setup ()
   {
      isChanging = true

      val columnNames = Vector<Object>().apply {
         add( Lang.word( 45 ) as Object )
         add( Lang.word( 223 ) as Object )
         add( Lang.word( 46 ) as Object )
         add( Lang.word( 48 ) as Object )
      }

      model = CustomTableModel(vectorize( tenderDocument.proposal.products, GUI.productTree ), columnNames)
      setModel( model )
      model?.addTableModelListener( TableModelHandler() )

      getColumnModel().apply {
         val centerRenderer = DefaultTableCellRenderer()
         centerRenderer.setHorizontalAlignment( SwingConstants.CENTER )

         getColumn( 0 ).setPreferredWidth( 90 )
         getColumn( 0 ).setCellRenderer( centerRenderer )
         getColumn( 1 ).setPreferredWidth( 170 )
         getColumn( 2 ).setPreferredWidth( 70 )
         getColumn( 2 ).setCellRenderer( centerRenderer )
         getColumn( 3 ).setPreferredWidth( 70 )
         getColumn( 3 ).setCellRenderer( centerRenderer )
      }

      isChanging = false
   }

   /** Dumps information returning tab to a prestine state. */
   internal fun vanilla ()
   {
      selectedProductNum = -1
      model = null
      modified = false
   }

   /** Adds selected product to the proposal if not already present. */
   internal fun add () : Boolean
   {
      var success = false
      val node = GUI.productTree.getLastSelectedPathComponent()

      if (node != null && GUI.productTree.isAChoosableProduct( node as TreeNode ))
      {
         val newProductInfo = ((node as DefaultMutableTreeNode).getUserObject() as KProductInfo)
         val newProduct = KProduct( newProductInfo.id )

         var alreadyInTableNr = -1                                              // Used to find out if the new product already exists in the proposal.

         for (n in tenderDocument.proposal.products.indices)
            if (newProduct.id == tenderDocument.proposal.products.get( n ).id)
               alreadyInTableNr = n

         if (alreadyInTableNr == -1)                                            // The product was not found in the proposal.
         {
            val vector = vectorize( newProductInfo )

            if (selectedProductNum == -1)                                       // Add product.
            {
               selectedProductNum = getRowCount()
               model?.addRow( vector )
               setRowSelectionInterval( selectedProductNum, selectedProductNum )
               tenderDocument.proposal.products.add( newProduct )
            }
            else                                                                // Insert product.
            {
               selectedProductNum += 1
               model?.insertRow( selectedProductNum, vector )
               setRowSelectionInterval( selectedProductNum, selectedProductNum )
               tenderDocument.proposal.products.add( selectedProductNum, newProduct )
            }
            success = true
         }
         else
            setRowSelectionInterval( alreadyInTableNr, alreadyInTableNr )       // Otherwise, select it.
      }

      return success
   }

   /** Removes selected product from the proposal. */
   internal fun remove () : Boolean
   {
      var success = false

      if (selectedProductNum != -1)
      {
         isChanging = true
         tenderDocument.proposal.products.removeAt( selectedProductNum )
         model?.removeRow( selectedProductNum )

         if (selectedProductNum >= tenderDocument.proposal.products.size)
         {
            selectedProductNum -= 1
            if (selectedProductNum >= 0)
               setRowSelectionInterval( selectedProductNum, selectedProductNum )
         }
         else
            setRowSelectionInterval( selectedProductNum, selectedProductNum )

         isChanging = false
         success = true
      }

      return success
   }

   /** The product description is shown as a tooltip. */
   override fun getToolTipText (e : MouseEvent) : String
   {
      var ret : String = ""
      val rowIndex = rowAtPoint( e.getPoint() )
      val realRowIndex = convertRowIndexToModel( rowIndex )

      if (realRowIndex >= 0 && realRowIndex < tenderDocument.proposal.products.size)
      {
         val product = tenderDocument.proposal.products.getOrNull( realRowIndex )
         product?.let {
            val productInfo = GUI.productTree.productInfo( it )

            if (productInfo is KQuantableProductInfo)
               ret = productInfo.desc
         }
      }
      return ret
   }

   /** Makes the table uneditable except for product quantity. */
   internal inner class CustomTableModel (data : Vector<Vector<Object>>, columnNames : Vector<Object>) : DefaultTableModel (data, columnNames)
   {
      override fun isCellEditable (i1 : Int, i2 : Int) : Boolean
      {
         var ret = false
         if (i2 == 3)
            ret = true

         return ret
      }
   }

   /** Handles changes to quantities.
     * @note Could´nt be done in EventHandler due to threading issues. */
   internal inner class TableModelHandler () : TableModelListener
   {
      override fun tableChanged (e : TableModelEvent)
      {
         if (!(e.getColumn() == TableModelEvent.ALL_COLUMNS))
         {
            val value = getValueAt( e.getFirstRow(), 3 )
            if (value != null && value is String)
            {
               if (selectedProductNum != -1)
               {
                  val qty = value.trim().toLong( 0 )

                  val productList = tenderDocument.proposal.products
                  val product = productList.getOrNull( selectedProductNum )

                  if (product is KProduct)
                  {
                     modified = true
                     updateTitle()
                     productList.set(productList.indexOf( product ), KProduct( product.id, qty ))
                  }
               }
            }
         }
      }
   }

   /** Handles selection events in the table.
     * @note Could´nt be done in EventHandler due to threading issues. */
   internal inner class ListSelectionHandler () : ListSelectionListener
   {
      override fun valueChanged (e : ListSelectionEvent)
      {
         if (!isChanging && !tenderDocument.proposal.products.isEmpty())
         {
            selectedProductNum = (e.getSource() as DefaultListSelectionModel).getLeadSelectionIndex()
            showProductInfo(getValueAt( selectedProductNum, 0 ).toString())
         }
      }
   }
}
