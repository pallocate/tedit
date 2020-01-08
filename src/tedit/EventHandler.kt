package tedit

import java.io.File
import javax.swing.JCheckBox
import javax.swing.JOptionPane
import javax.swing.JLabel
import javax.swing.tree.DefaultMutableTreeNode
import pen.par.KMember
import pen.par.KMutableTender
import pen.par.KRelation
import pen.eco.KProductInfo
import apps.KPasswordPopup
import apps.Constants
import pen.Constants.SLASH

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
   val SELECT_USER                             = "SELECT_USER"
   val TREE_SELECTION                          = "TREE_SELECTION"
   val ADD                                     = "ADD"
   val REMOVE                                  = "REMOVE"
   val TENDER                                  = "TENDER"
   val SAVE_SETTINGS                           = "SAVE_SETTINGS"
   val GET_UPDATES                             = "GET_UPDATES"
   val ACCOUNT_EDIT                            = "ACCOUNT_EDIT"

   private val chkBox                          = JCheckBox(Ref.word( 242 ))

   /** Handles events.
    * @param event What event to handle. */
   fun handle (event : String)
   {
      val proposalEditor = Ref.pe()

      when (event)
      {
         NEW ->
         {
            val member = Ref.settings.currentUser().member
            if (member is KMember)
            {
               val relationSelector = KRelationSelector( member, proposalEditor )
               val rel = relationSelector.relation

               if (rel is KRelation)
               {
                  val tableTab = KTableTab(KMutableTender( relation = rel ))

                  Ref.tabbedPane.addTab(Ref.word( 3 ), tableTab)
                  Ref.tabbedPane.setSelectedComponent( tableTab )

                  tableTab.table.setup()
                  tableTab.updateTitle()

                  Ref.currentTab = tableTab
               }
            }
         }

         OPEN ->
            openTender()

         SAVE ->
            if (Ref.currentTab.table.modified == true && Ref.currentTab.filename != Ref.word( 3 ))
            {
               if (Ref.currentTab.saveTender())
               {
                  Ref.currentTab.table.modified = false
                  Ref.currentTab.updateTitle()
               }
               else
                  saveTender()
            }
            else
               saveTender()


         SAVE_AS ->
            saveTender()

         CLOSE ->
            if (Ref.currentTab.table.modified == false)
               removeCurrentTab()
            else
               if (JOptionPane.showConfirmDialog(proposalEditor, Ref.word( 49 ) + "\n" + Ref.word( 24 ),
               Ref.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)
                  removeCurrentTab()

         SELECT_USER ->
            {}

         SAVE_SETTINGS ->
            Ref.settings.save()

         GET_UPDATES ->
            println( GET_UPDATES )

         TENDER ->
         {
            val passwordPopup = KPasswordPopup( true )
            val member = Ref.settings.currentUser().member

            Ref.currentTab.apply {
               if (table.modified == false)
                  submitTender( member.me, passwordPopup )
               else
                  if (JOptionPane.showConfirmDialog(proposalEditor, Ref.word( 49 ) + "\n" + Ref.word( 24 ),
                  Ref.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)
                     submitTender( member.me, passwordPopup )
            }
         }

         QUIT ->
         {
            if (handleModifications())
            {
               proposalEditor.setVisible( false )
               proposalEditor.dispose()
            }
         }

         SEARCH ->
         {
            val productTree = Ref.productTree
            val term = JOptionPane.showInputDialog(proposalEditor, arrayOf(JLabel(Ref.word( 20 ) + ": " + Ref.word( 208 )),
                  chkBox), Ref.word( 20 ), JOptionPane.QUESTION_MESSAGE )

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
            val productTree = Ref.productTree
            if (productTree.searchResult.size > 0)
            {
               if (productTree.searchResultIndex + 1 < productTree.searchResult.size)
                  productTree.searchResultIndex += 1

               productTree.showNode( productTree.searchResult.get(productTree.searchResultIndex) )
            }
         }

         ADD ->
            if (Ref.currentTab.table.add())
            {
               Ref.currentTab.table.modified = true
               Ref.currentTab.updateTitle()
            }

         REMOVE ->
            if (Ref.currentTab.table.remove())
            {
               Ref.currentTab.table.modified = true
               Ref.currentTab.updateTitle()
            }

         CLEAR ->
         {
            with (Ref.currentTab) {
               table.vanilla( true )
               table.setup()
               updateTitle()
            }
            Ref.productTree.resetSearch()
         }

         HELP ->
            Ref.summary.load( Constants.RESOURCES_DIR + SLASH + "help.html" )

         ABOUT ->
            JOptionPane.showMessageDialog( proposalEditor, Ref.word( 301 ), "information", JOptionPane.INFORMATION_MESSAGE )

         TREE_SELECTION ->
         {
            val dmt = Ref.productTree.getLastSelectedPathComponent()

            dmt?.run {
               val selectedObject = (dmt as DefaultMutableTreeNode).getUserObject() as KProductInfo
               val productInfoPath = "${Constants.USERS_DIR}${SLASH}${Ref.settings.currentUser().member.me.name}${SLASH}productinfo"
               Ref.summary.load( "${productInfoPath}${SLASH}${selectedObject.id}.html" )
            }
         }

         HAMBURGER ->
            Ref.hamburgerMenu.show()

         else -> {}
      }
   }
}
