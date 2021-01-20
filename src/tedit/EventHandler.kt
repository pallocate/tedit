package tedit

import javax.swing.JCheckBox
import javax.swing.JOptionPane
import javax.swing.JLabel
import javax.swing.tree.DefaultMutableTreeNode
import pen.generateId
import pen.eco.Target
import pen.eco.KProposal
import pen.eco.KProductInfo
import tedit.utils.Constants.SLASH
import tedit.utils.Constants.HELP_DIR
import tedit.utils.Constants.USERS_DIR
import tedit.session.Session
import tedit.session.KTenderDocument
import tedit.gui.GUI
import tedit.gui.KRelationSelector

/** A central place to handle events. */
object EventHandler
{
   val NEW                                     = "NEW"
   val OPEN                                    = "OPEN"
   val SAVE                                    = "SAVE"
   val SAVE_AS                                 = "SAVE_AS"
   val CLOSE                                   = "CLOSE"
   val CLEAR                                   = "CLEAR"
   val QUIT                                    = "QUIT"
   val DELETE                                  = "DELETE"
   val SEARCH                                  = "SEARCH"
   val SEARCH_NEXT                             = "SEARCH_NEXT"
   val HELP                                    = "HELP"
   val ABOUT                                   = "ABOUT"
   val HAMBURGER                               = "HAMBURGER"
   val ECONOMIC_RELATIONS                      = "ECONOMIC_RELATIONS"
   val TREE_SELECTION                          = "TREE_SELECTION"
   val ADD                                     = "ADD"
   val REMOVE                                  = "REMOVE"
   val SUBMIT                                  = "SUBMIT"
   val SAVE_SETTINGS                           = "SAVE_SETTINGS"
   val GET_UPDATES                             = "GET_UPDATES"
   val ACCOUNT_EDIT                            = "ACCOUNT_EDIT"

   private val chkBox                          = JCheckBox(Lang.word( 242 ))

   /** Handles most of the events. */
   fun handle (event : String)
   {
      val tenderEdit = GUI.frame

      when (event)
      {
         NEW ->
         {
            val relationSelector = KRelationSelector( tenderEdit )
            val relation = relationSelector.selectedRelation
            val user = relationSelector.selectedUser

            if (!user.isVoid() && !relation.isVoid())
            {
               val settings = Session.settings
               val header = pen.eco.KHeader( generateId(), settings.year(), settings.iteration(), relation.target )
               val proposal = pen.eco.KProposal( header )
               val document = KTenderDocument( proposal, relation, user )
               val tab = document.proposalTable.tab

               GUI.tabs.addTab(Lang.word( 3 ), tab)
               GUI.tabs.setSelectedComponent( tab )
               document.proposalTable.setup()
               updateTitle()
            }
         }

         OPEN ->
            openTab()

         SAVE ->
            if (Session.documents.activeDocument.proposalTable.modified == true && Session.documents.activeDocument.pathname != Lang.word( 3 ))
            {
               if (Session.documents.activeDocument.save())
               {
                  Session.documents.activeDocument.proposalTable.modified = false
                  updateTitle()
               }
               else
                  saveTab()
            }
            else
               saveTab()

         SAVE_AS ->
            saveTab()

//         SAVE_ENCRYPTED ->
//            saveTab()

         CLOSE ->
            if (Session.documents.activeDocument.proposalTable.modified == false)
               closeTab()
            else
               if (JOptionPane.showConfirmDialog(tenderEdit, Lang.word( 49 ) + "\n" + Lang.word( 24 ),
               Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)
                  closeTab()

         ECONOMIC_RELATIONS ->
         {
            KRelationSelector( tenderEdit )
         }

         SAVE_SETTINGS ->
         {
            Session.settings.save()
            Session.users.save()
         }

         GET_UPDATES ->
            println( GET_UPDATES )

         SUBMIT ->
         {
            with (Session.documents.activeDocument) {
               if (proposalTable.modified == false)
                  println( "commit" )
               else
                  if (JOptionPane.showConfirmDialog(tenderEdit, Lang.word( 49 ) + "\n" + Lang.word( 24 ),
                  Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)
                     println( "commit" )
            }
         }

         QUIT ->
         {
            if (warnOnModified())
            {
               tenderEdit.setVisible( false )
               tenderEdit.dispose()
            }
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
            if (Session.documents.activeDocument.proposalTable.add())
            {
               Session.documents.activeDocument.proposalTable.modified = true
               updateTitle()
            }

         REMOVE ->
            if (Session.documents.activeDocument.proposalTable.remove())
            {
               Session.documents.activeDocument.proposalTable.modified = true
               updateTitle()
            }

         CLEAR ->
         {
            with (Session.documents.activeDocument) {
               proposal = KProposal()
               proposalTable.vanilla()
               proposalTable.setup()
               updateTitle()
            }
            GUI.productTree.resetSearch()
         }

         HELP ->
            GUI.info.load( HELP_DIR + SLASH + "index.html" )

         ABOUT ->
            JOptionPane.showMessageDialog( tenderEdit, Lang.word( 301 ), "information", JOptionPane.INFORMATION_MESSAGE )

         TREE_SELECTION ->
         {
            val dmt = GUI.productTree.getLastSelectedPathComponent()

            dmt?.run {
               val selectedObject = (dmt as DefaultMutableTreeNode).getUserObject() as KProductInfo

               val relation = Session.documents.activeDocument.relation
               if (!relation.isVoid() && relation.target > Target.UNDEFINED)
               {
                  val productsDir = if (relation.target == Target.PRODUCTION)
                                       "jobinfo"
                                    else
                                       "productinfo"

                  val productsInfoPath = "${USERS_DIR}${SLASH}${Session.users.activeUser.me.info.name}${SLASH}$productsDir"
                  GUI.info.load( "${productsInfoPath}${SLASH}${selectedObject.id}.html" )
               }
            }
         }

         HAMBURGER ->
            GUI.hamburgerMenu.show()

         else -> {}
      }
   }
}
