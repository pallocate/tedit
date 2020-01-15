package tedit

import java.awt.event.MouseEvent
import java.util.Vector
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
import pen.eco.KMutableProposal
import pen.eco.KMutableProduct
import pen.eco.KProductInfo
import pen.eco.KQuantableProductInfo
import pen.eco.KMutableHeader
import pen.par.Tender
import apps.Utils
import apps.Constants.USERS_DIR
import pen.Constants.SLASH

/** A table for showing products and edit quantities. */
class KProposalTable () : JTable()
{
   var modified                                 = false
   var proposal                                 = KMutableProposal()
      private set
   private var model : CustomTableModel?        = null
   private var isChanging                       = false                         // To mitigate effects of unwanted selection events.
   private var selectedProductNum               = -1

   init
   {
      setFillsViewportHeight( true )
      setRowSelectionAllowed( true )
      setColumnSelectionAllowed( false )
      setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION )
      setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN )
      setFont(java.awt.Font( getFont().getName(), java.awt.Font.PLAIN, 13 ))

      val listSelectionModel = getSelectionModel()
      listSelectionModel.addListSelectionListener( ListSelectionHandler() )
      setSelectionModel( listSelectionModel )
   }

   fun updateFromTender (tender : Tender)
   {
      if (tender.proposal is KMutableProposal)
         proposal = tender.proposal as KMutableProposal
   }

   /** Resets the table using info from proposal. */
   fun setup ()
   {
      isChanging = true

      val columnNames = Vector<Object>().apply {
         add( Lang.word( 45 ) as Object )
         add( Lang.word( 223 ) as Object )
         add( Lang.word( 46 ) as Object )
         add( Lang.word( 48 ) as Object )
      }

      model = CustomTableModel(Utils.vectorize( proposal.products, Ref.productTree ), columnNames)
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
   fun vanilla ()
   {
      selectedProductNum = -1
      proposal.apply {
         header.vanilla()
         products.clear()
      }

      model = null
      modified = false
   }

   /** Adds selected product to the proposal if not already present. */
   fun add () : Boolean
   {
      var success = false
      val node = Ref.productTree.getLastSelectedPathComponent()

      if (node != null && Ref.productTree.isAChoosableProduct( node as TreeNode ))
      {
         val newProduct = KMutableProduct()
         val newProductInfo = ((node as DefaultMutableTreeNode).getUserObject() as KProductInfo)
         newProduct.id = newProductInfo.id

         var alreadyInTableNr = -1                                           // Used to find out if the new product already exists in the proposal.

         for (n in proposal.products.indices)
            if (newProduct.id == proposal.products.get( n ).id)
               alreadyInTableNr = n

         if (alreadyInTableNr == -1)                                         // The product was not found in the proposal.
         {
            val vector = Utils.vectorize( newProductInfo )

            if (selectedProductNum == -1)                                    // Add product.
            {
               selectedProductNum = getRowCount()
               model?.addRow( vector )
               setRowSelectionInterval( selectedProductNum, selectedProductNum )
               proposal.products.add( newProduct )
            }
            else                                                             // Insert product.
            {
               selectedProductNum += 1
               model?.insertRow( selectedProductNum, vector )
               setRowSelectionInterval( selectedProductNum, selectedProductNum )
               proposal.products.add( selectedProductNum, newProduct )
            }
            success = true
         }
         else
            setRowSelectionInterval( alreadyInTableNr, alreadyInTableNr )    // Otherwise, select it.
      }

      return success
   }

   /** Removes selected product from the proposal. */
   fun remove () : Boolean
   {
      var success = false

      if (selectedProductNum != -1)
      {
         isChanging = true
         proposal.products.removeAt( selectedProductNum )
         model?.removeRow( selectedProductNum )

         if (selectedProductNum >= proposal.products.size)
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

      if (realRowIndex >= 0 && realRowIndex < proposal.products.size)
      {
         val product = proposal.products.getOrNull( realRowIndex )
         product?.let {
            val productInfo = Ref.productTree.productInfo( it )

            if (productInfo is KQuantableProductInfo)
               ret = productInfo.desc
         }
      }
      return ret
   }

   /** Makes the table uneditable except for product quantity. */
   inner class CustomTableModel (data : Vector<Vector<Object>>, columnNames : Vector<Object>) : DefaultTableModel (data, columnNames)
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
   inner class TableModelHandler () : TableModelListener
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

                  val product = proposal.products.getOrNull( selectedProductNum )
                  product?.run {
                     modified = true
                     updateTitle()
                     product.qty = qty
                  }
               }
            }
         }
      }
   }

   /** Handles selection events in the table.
     * @note Could´nt be done in EventHandler due to threading issues. */
   inner class ListSelectionHandler () : ListSelectionListener
   {
      override fun valueChanged (e : ListSelectionEvent)
      {
         if (!isChanging && !proposal.products.isEmpty())
         {
            val productInfoPath = "${USERS_DIR}${SLASH}${Ref.users().current.member.me.name}${SLASH}productinfo"
            selectedProductNum = (e.getSource() as DefaultListSelectionModel).getLeadSelectionIndex()
            Ref.summary.load( "${productInfoPath}${SLASH}${getValueAt(selectedProductNum, 0)}.html" )
         }
      }
   }
}
