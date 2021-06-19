package tedit

import javax.swing.JCheckBox
import javax.swing.JOptionPane
import javax.swing.JLabel
import javax.swing.tree.DefaultMutableTreeNode
import pen.eco.Target
import pen.eco.KProposal
import pen.eco.KProduct_v1
import tedit.session.Session
import tedit.session.KTenderDocument
import tedit.gui.GUI
import tedit.utils.helpDir

/** A central place to handle events. */
object EventHandler
{
   val NEW                                     = "NEW"
   val OPEN                                    = "OPEN"
   val SAVE                                    = "SAVE"
   val SAVE_AS                                 = "SAVE_AS"
   val EXPORT_ENCRYPTED                        = "EXPORT_ENCRYPTED"
   val CLOSE                                   = "CLOSE"
   val CLEAR                                   = "CLEAR"
   val QUIT                                    = "QUIT"
   val SEARCH                                  = "SEARCH"
   val SEARCH_NEXT                             = "SEARCH_NEXT"
   val HELP                                    = "HELP"
   val ABOUT                                   = "ABOUT"
   val HAMBURGER                               = "HAMBURGER"
   val TREE_SELECTION                          = "TREE_SELECTION"
   val ADD                                     = "ADD"
   val REMOVE                                  = "REMOVE"
   val SAVE_SETTINGS                           = "SAVE_SETTINGS"
   val GET_UPDATES                             = "GET_UPDATES"
   val ACCOUNT_EDIT                            = "ACCOUNT_EDIT"

   private val chkBox                          = JCheckBox(Lang.word( 242 ))

   /** Handles most of the events. */
   fun handle (event : String)
   {
      val activeDocument = Session.documents.activeDocument
      val tenderEdit = GUI.frame

      when (event)
      {
         NEW ->
            newDocument()
         OPEN ->
            openDocument()

         /* TODO: Iterate proposal table to get quatities into the tender document. */
         SAVE ->
            if (activeDocument.isModified() || !activeDocument.isPathSet())
            {
               activeDocument.updateFromModel()
               saveDocument( activeDocument )
            }
         SAVE_AS ->
            if (activeDocument.isModified() || !activeDocument.isPathSet())
            {
               activeDocument.updateFromModel()
               saveDocumentAs( activeDocument )
            }
         EXPORT_ENCRYPTED ->
            exportEncrypted( activeDocument )

         CLOSE ->
            if (activeDocument.isModified())
            {
               if (saveDocument( activeDocument ))
                  closeDocument( activeDocument )
            }
            else
               closeDocument( activeDocument )

         QUIT ->
         {
            val unsavedDocuments = Session.documents.unsaved()

            for (unsavedDocument in unsavedDocuments)
            {
               GUI.tabs.setSelectedComponent( unsavedDocument.proposalTable.tab )
               if (!optionalSave( unsavedDocument ))
                  return
            }

            GUI.frame.setVisible( false )
            GUI.frame.dispose()
         }

         SEARCH ->
         {
            val productTree = GUI.productTree
            val term = JOptionPane.showInputDialog(tenderEdit, arrayOf(JLabel(Lang.word( 20 ) + ": " + Lang.word( 208 )),
                  chkBox), Lang.word( 20 ), JOptionPane.QUESTION_MESSAGE )

            if (term != null && term != "")
            {
               productTree.searchResultIndex = 0
               productTree.searchResult = productTree.search( term, productTree.treeTop, chkBox.isSelected )
               if (!productTree.searchResult.isEmpty())
                  productTree.showNode(productTree.searchResult.get( productTree.searchResultIndex ))
            }
         }

         SEARCH_NEXT ->
         {
            val productTree = GUI.productTree
            if (productTree.searchResult.size > 0)
            {
               if (productTree.searchResultIndex + 1 < productTree.searchResult.size)
                  productTree.searchResultIndex += 1

               productTree.showNode( productTree.searchResult.get(productTree.searchResultIndex) )
            }
         }

         ADD -> 
            Session.documents.activeDocument.proposalTable.add()

         REMOVE -> 
            Session.documents.activeDocument.proposalTable.remove()

         CLEAR ->
         {
            with (Session.documents.activeDocument) {
               proposal = KProposal()
               pathname = Lang.word( 3 )
               proposalTable.vanilla()
               proposalTable.setup()
            }
            GUI.productTree.resetSearch()
         }

         HELP ->
            GUI.info.load(helpDir.resolve( "index.html" ).toFile())

         ABOUT ->
            JOptionPane.showMessageDialog( tenderEdit, Lang.word( 301 ), "information", JOptionPane.INFORMATION_MESSAGE )

         TREE_SELECTION ->
         {
            val dmt = GUI.productTree.getLastSelectedPathComponent()

            dmt?.let {
               val selectedObject = (it as DefaultMutableTreeNode).getUserObject() as KProduct_v1
               showProduct( selectedObject.id.toString() )
            }
         }

         HAMBURGER ->
            GUI.hamburgerMenu.show()

         SAVE_SETTINGS ->
            Session.settings.save()

         GET_UPDATES ->
            println( GET_UPDATES )

         ACCOUNT_EDIT ->
            println( ACCOUNT_EDIT )

         else -> {}
      }
   }
}
