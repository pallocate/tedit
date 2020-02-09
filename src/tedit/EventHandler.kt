package tedit

import java.io.File
import javax.swing.JCheckBox
import javax.swing.JOptionPane
import javax.swing.JLabel
import javax.swing.tree.DefaultMutableTreeNode
import pen.Constants.SLASH
import pen.eco.Target
import pen.eco.KProductInfo
import pen.par.KMember
import pen.par.KMutableTender
import pen.par.KRelation
import apps.Constants

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

   /** Handles events.
    * @param event What event to handle. */
   fun handle (event : String)
   {
      val tenderEdit = GUI.frame

      when (event)
      {
         NEW ->
         {
            val relationSelector = KRelationSelector( tenderEdit )
            val relation = relationSelector.selectedRelation

            if (relation is KRelation)
            {
               val settings = KSettings.instance
               val header = pen.eco.KMutableHeader( pen.net.Network.generateId(), settings.year(), settings.iteration(), relation.target )
               val proposal = pen.eco.KMutableProposal( header )
               val tender = KMutableTender( proposal, relation )
               val tenderTab = KTenderTab( tender )

               Tabs.addTab(Lang.word( 3 ), tenderTab)
               Tabs.setSelectedComponent( tenderTab )
               tenderTab.proposalTable.setup()

               updateTitle()
            }
         }

         OPEN ->
            EHFunctions.openTab()

         SAVE ->
            if (Tabs.current.proposalTable.modified == true && Tabs.current.filename != Lang.word( 3 ))
            {
               if (Tabs.current.save())
               {
                  Tabs.current.proposalTable.modified = false
                  updateTitle()
               }
               else
                  EHFunctions.saveTab()
            }
            else
               EHFunctions.saveTab()


         SAVE_AS ->
            EHFunctions.saveTab()

         CLOSE ->
            if (Tabs.current.proposalTable.modified == false)
               EHFunctions.closeTab()
            else
               if (JOptionPane.showConfirmDialog(tenderEdit, Lang.word( 49 ) + "\n" + Lang.word( 24 ),
               Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)
                  EHFunctions.closeTab()

         ECONOMIC_RELATIONS ->
         {
            KRelationSelector( tenderEdit )
         }

         SAVE_SETTINGS ->
         {
            KSettings.instance.save()
            KUsers.instance.save()
         }

         GET_UPDATES ->
            println( GET_UPDATES )

         SUBMIT ->
         {
            Tabs.current.apply {
               if (proposalTable.modified == false)
                  submit()
               else
                  if (JOptionPane.showConfirmDialog(tenderEdit, Lang.word( 49 ) + "\n" + Lang.word( 24 ),
                  Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)
                     submit()
            }
         }

         QUIT ->
         {
            if (handleModifications())
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
            if (Tabs.current.proposalTable.add())
            {
               Tabs.current.proposalTable.modified = true
               updateTitle()
            }

         REMOVE ->
            if (Tabs.current.proposalTable.remove())
            {
               Tabs.current.proposalTable.modified = true
               updateTitle()
            }

         CLEAR ->
         {
            with (Tabs.current) {
               proposalTable.vanilla()
               proposalTable.setup()
               updateTitle()
            }
            GUI.productTree.resetSearch()
         }

         HELP ->
            GUI.info.load( Constants.HELP_DIR + SLASH + "index.html" )

         ABOUT ->
            JOptionPane.showMessageDialog( tenderEdit, Lang.word( 301 ), "information", JOptionPane.INFORMATION_MESSAGE )

         TREE_SELECTION ->
         {
            val dmt = GUI.productTree.getLastSelectedPathComponent()

            dmt?.run {
               val selectedObject = (dmt as DefaultMutableTreeNode).getUserObject() as KProductInfo

               val currentRelation = Tabs.current.tender.relation
               if (currentRelation is KRelation)
               {
                  val productsDir = if (currentRelation.target == Target.PRODUCTION)
                                       "jobinfo"
                                    else
                                       "productinfo"

                  val productsInfoPath = "${Constants.USERS_DIR}${SLASH}${KUsers.instance.current.member.me.name}${SLASH}$productsDir"
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
