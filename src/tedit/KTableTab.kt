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
import pen.readObject
import pen.writeObject
import pen.PasswordProvider
import pen.eco.KMutableProduct
import pen.eco.KProductInfo
import pen.eco.KQuantableProductInfo
import pen.eco.KMutableHeader
import pen.par.KMe
import pen.par.Tender
import pen.par.NoTender
import pen.par.KMutableTender
import pen.par.KRelation
import apps.Utils
import apps.Constants
import pen.Constants.SLASH

/** Tab for editing a proposal. Coupling a filename, a role, and a proposal. */
class KTableTab (var tender : Tender = NoTender()) : JScrollPane()
{
   val table                                    = Table()
   var filename                                 = Ref.word( 3 )

   init
   {setViewportView( table )}

   fun saveTender () = writeObject<KMutableTender>( tender as KMutableTender, {KMutableTender.serializer()}, filename )

   fun loadTender (filename : String)
   {
      val obj = readObject<KMutableTender>( {KMutableTender.serializer()}, filename )

      if (obj is Tender)
         tender = obj
      this.filename = filename
   }

   fun submitTender (me : KMe, passwordProvider : PasswordProvider)
   {
      if (tender is KMutableTender)
         (tender as KMutableTender).toKTender().submit( me, passwordProvider )
   }

   /** Sets the title according to file name and modification status. */
   fun updateTitle ()
   {
      var title = Ref.word( 301 ) + " - "

      title = title + filename.substring( filename.lastIndexOf( Constants.SLASH ) + 1 )

      if (table.modified)
         title = title + " *"

      val proposalEditor = Ref.pe()
      if (proposalEditor is KProposalEditor)
         proposalEditor.setTitle( title )
   }

   /** A table for showing products and edit quantities. */
   inner class Table : JTable()
   {
      private var isChanging                       = false                      // To mitigate effects of unwanted selection events.
      private var selectedProductNum               = -1
      var model : CustomTableModel?                = null
      var modified                                 = false

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

      /** Resets the table using info from proposal. */
      fun setup ()
      {
         isChanging = true

         val columnNames = Vector<Object>().apply {
            add( Ref.word( 45 ) as Object )
            add( Ref.word( 223 ) as Object )
            add( Ref.word( 46 ) as Object )
            add( Ref.word( 48 ) as Object )
         }

         model = CustomTableModel(Utils.vectorize( tender.proposal.products, Ref.productTree ), columnNames)
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
      fun vanilla (retainAssociation : Boolean = false)
      {
         selectedProductNum = -1
         if (tender is KMutableTender)
            tender.proposal.apply {
               (header as KMutableHeader).vanilla()
               (products as ArrayList<KMutableProduct>).clear()
            }

         model = null
         modified = false
         if (!retainAssociation)
         {
            filename = Ref.word( 3 )
         }
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

            for (n in tender.proposal.products.indices)
               if (newProduct.id == tender.proposal.products.get( n ).id)
                  alreadyInTableNr = n

            if (alreadyInTableNr == -1)                                         // The product was not found in the proposal.
            {
               val vector = Utils.vectorize( newProductInfo )

               if (selectedProductNum == -1)                                    // Add product.
               {
                  selectedProductNum = getRowCount()
                  model?.addRow( vector )
                  setRowSelectionInterval( selectedProductNum, selectedProductNum )
                  if (tender is KMutableTender)
                     (tender.proposal.products as ArrayList<KMutableProduct>).add( newProduct )
               }
               else                                                             // Insert product.
               {
                  selectedProductNum += 1
                  model?.insertRow( selectedProductNum, vector )
                  setRowSelectionInterval( selectedProductNum, selectedProductNum )
                  if (tender is KMutableTender)
                     (tender.proposal.products as ArrayList<KMutableProduct>).add( selectedProductNum, newProduct )
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
            if (tender is KMutableTender)
               (tender.proposal.products as ArrayList<KMutableProduct>).removeAt( selectedProductNum )
            model?.removeRow( selectedProductNum )

            if (selectedProductNum >= tender.proposal.products.size)
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

         if (realRowIndex >= 0 && realRowIndex < tender.proposal.products.size)
         {
            val product = tender.proposal.products.getOrNull( realRowIndex )
            product?.let {
               val productInfo = Ref.productTree.productInfo( it )

               if (productInfo is KQuantableProductInfo)
                  ret = productInfo.desc
            }
         }
         return ret
      }

      /** Makes the table largely uneditable. */
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

                     val product = tender.proposal.products.getOrNull( selectedProductNum )
                     product?.run {
                        modified = true
                        updateTitle()
                        (product as KMutableProduct).qty = qty
                     }
                  }
               }
            }
         }
      }

      /** Handles selection events to the table.
        * @note Could´nt be done in EventHandler due to threading issues. */
      inner class ListSelectionHandler () : ListSelectionListener
      {
         override fun valueChanged (e : ListSelectionEvent)
         {
            if (!isChanging && !tender.proposal.products.isEmpty())
            {
               val productInfoPath = "${Constants.USERS_DIR}${SLASH}${Ref.settings.currentUser().member.me.name}${SLASH}productinfo"
               selectedProductNum = (e.getSource() as DefaultListSelectionModel).getLeadSelectionIndex()
               Ref.summary.load( "${productInfoPath}${SLASH}${getValueAt(selectedProductNum, 0)}.html" )
            }
         }
      }
   }
}
