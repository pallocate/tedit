package tedit.gui

import java.awt.Font
import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.swing.DefaultListSelectionModel
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.tree.TreeNode
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.event.ListSelectionListener
import javax.swing.event.ListSelectionEvent
import pen.toLong
import pen.eco.toProductQuantity
import pen.eco.KProduct_v1
import pen.eco.KProductQuantity
import pen.eco.KProductQuantities
import tedit.showProduct
import tedit.updateTitle
import tedit.Lang
import tedit.session.KTenderDocument
import tedit.gui.KProposalTable

/** A table for showing products and edit quantities. */
class KProposalTable (internal val tenderDocument : KTenderDocument) : JTable()
{
   /** The proposal table is added to this JScrollPane. */
   internal val tab = JScrollPane()
   internal var modified = false
      set(state : Boolean)
      {
         if (state)
         {
            proposalTableModel.fireTableDataChanged()
            updateTitle()
         }
         field = state
      }
   internal val proposalTableModel = KProposalTableModel()
   private var selectedProductNum = -1

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
      proposalTableModel.addAll( tenderDocument.productQuantities )
      setModel( proposalTableModel )

      with (columnModel) {
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
   }

   /** Dumps information returning tab to a prestine state. */
   internal fun vanilla ()
   {
      selectedProductNum = -1
      proposalTableModel.productRows.clear()
      proposalTableModel.fireTableDataChanged()
      modified = false
   }

   /** Adds selected product to the proposal if not already present. */
   internal fun add ()
   {
      val node = GUI.productTree.getLastSelectedPathComponent()

      if (node != null && GUI.productTree.isAChoosableProduct( node as TreeNode ))
      {
         val newProduct = ((node as DefaultMutableTreeNode).getUserObject() as KProduct_v1)
         
         proposalTableModel.add( newProduct )
         selectedProductNum = proposalTableModel.productRows.size - 1
         proposalTableModel.fireTableDataChanged()
         modified = true
      }
   }

   /** Removes selected product from the proposal. */
   internal fun remove ()
   {
      if (selectedProductNum > -1)
      {
         proposalTableModel.productRows.removeAt( selectedProductNum )

         if (selectedProductNum >= proposalTableModel.productRows.size)
         {
            selectedProductNum -= 1
            if (selectedProductNum >= 0)
               setRowSelectionInterval( selectedProductNum, selectedProductNum )
         }
         else
            setRowSelectionInterval( selectedProductNum, selectedProductNum )

         modified = true
      }
   }

   /** The product description is shown as a tooltip. */
   override fun getToolTipText (e : MouseEvent) : String
   {
      var ret : String = ""
      val rowIndex = rowAtPoint( e.getPoint() )
      val realRowIndex = convertRowIndexToModel( rowIndex )

      if (proposalTableModel != null && realRowIndex >= 0 && realRowIndex < rowCount)
      {
         val productId = proposalTableModel.getValueAt( realRowIndex, 0 ) as Long
         val product = GUI.productTree.product( productId )

         if (product is KProduct_v1)
            ret = product.desc
      }
      return ret
   }

   internal inner class KProposalTableModel () : AbstractTableModel ()
   {
      val columnNames = arrayOf(Lang.word( 45 ), Lang.word( 223 ), Lang.word( 46 ), Lang.word( 48 ))
      val productRows = ArrayList<KProductRow>()

      fun add (product : KProduct_v1)
      {
         if (!productRows.any { it.product.id == product.id })
            productRows.add( KProductRow(product, KProductQuantity( product.id )) )
      }

      fun add (productQuantity : KProductQuantity)
      {
         if (!productRows.any { it.product.id == productQuantity.id })
         {
            val product = GUI.productTree.product( productQuantity.id )
            productRows.add(KProductRow( product, productQuantity ))
         }
      }

      fun addAll (productQuatities : KProductQuantities) = productQuatities.toList().forEach {add( it.toProductQuantity() )}

      override fun getValueAt (row : Int, col : Int) : Object
      {
         val product = productRows[row.coerceIn( 0, productRows.size-1 )].product
         return when (col)
         {
            0 -> product.id as Object
            1 ->
            {
               val apu = product.apu()
               (product.toString() + if (apu == "")
                                       ""
                                     else
                                       ", " + apu) as Object
            }
            2 -> product.price.toString() as Object
            3 -> productRows[row].productQuantity as Object
            else -> Object()
         }
      }

      override fun getColumnClass (c : Int) = "".javaClass

      /** Only accept changes to product quantity. */
      override fun isCellEditable (row : Int, col : Int) = if (col == 3) true   else false

      @Override
      fun setValueAt (value : Object, row : Int, col : Int)
      {
         if (col == 3)
         {
            productRows[row].productQuantity.qty = (value as String).toLong()
            fireTableCellUpdated(row, col);
            modified = true
         }
      }

      override fun getColumnName (col : Int) = columnNames[col]
      override fun getColumnCount () = columnNames.size
      override fun getRowCount() = productRows.size
   }

   /** Handles selection events in the table.
     * @note Could´nt be done in EventHandler due to threading issues. */
   internal inner class ListSelectionHandler () : ListSelectionListener
   {
      override fun valueChanged (e : ListSelectionEvent)
      {
         if (proposalTableModel.productRows.isNotEmpty())
         {
            selectedProductNum = (e.getSource() as DefaultListSelectionModel).getLeadSelectionIndex()
            showProduct(getValueAt( selectedProductNum, 0 ).toString())
         }
      }
   }
}
