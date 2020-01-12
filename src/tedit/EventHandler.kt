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
   val SUBMIT                                  = "SUBMIT"
   val SAVE_SETTINGS                           = "SAVE_SETTINGS"
   val GET_UPDATES                             = "GET_UPDATES"
   val ACCOUNT_EDIT                            = "ACCOUNT_EDIT"

   private val chkBox                          = JCheckBox(Lang.word( 242 ))

   /** Handles events.
    * @param event What event to handle. */
   fun handle (event : String)
   {
      val tenderEdit = KTenderEdit.tenderEdit()

      when (event)
      {
         NEW ->
         {
            val member = Ref.users().current.member
            val relationSelector = KRelationSelector( member, tenderEdit )
            val rel = relationSelector.relation

            if (rel is KRelation)
            {
               val tenderTab = KTenderTab(KMutableTender( relation = rel ))

               Tabs.addTab(Lang.word( 3 ), tenderTab)
               Tabs.setSelectedComponent( tenderTab )

               tenderTab.proposalTable.setup()
               updateTitle()

               Tabs.current = tenderTab
            }
         }

         OPEN ->
            openTender()

         SAVE ->
            if (Tabs.current.proposalTable.modified == true && Tabs.current.filename != Lang.word( 3 ))
            {
               if (Tabs.current.save())
               {
                  Tabs.current.proposalTable.modified = false
                  updateTitle()
               }
               else
                  saveTender()
            }
            else
               saveTender()


         SAVE_AS ->
            saveTender()

         CLOSE ->
            if (Tabs.current.proposalTable.modified == false)
               removeCurrentTab()
            else
               if (JOptionPane.showConfirmDialog(tenderEdit, Lang.word( 49 ) + "\n" + Lang.word( 24 ),
               Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)
                  removeCurrentTab()

         SELECT_USER ->
            {}

         SAVE_SETTINGS ->
         {
            Ref.settings.save()
            Ref.users().save()
         }

         GET_UPDATES ->
            println( GET_UPDATES )

         SUBMIT ->
         {
            val passwordPopup = KPasswordPopup( true )
            val member = Ref.users().current.member

            Tabs.current.apply {
               if (proposalTable.modified == false)
                  submit( member.me, passwordPopup )
               else
                  if (JOptionPane.showConfirmDialog(tenderEdit, Lang.word( 49 ) + "\n" + Lang.word( 24 ),
                  Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)
                     submit( member.me, passwordPopup )
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
            val productTree = Ref.productTree
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
            val productTree = Ref.productTree
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
            Ref.productTree.resetSearch()
         }

         HELP ->
            Ref.summary.load( Constants.RESOURCES_DIR + SLASH + "help.html" )

         ABOUT ->
            JOptionPane.showMessageDialog( tenderEdit, Lang.word( 301 ), "information", JOptionPane.INFORMATION_MESSAGE )

         TREE_SELECTION ->
         {
            val dmt = Ref.productTree.getLastSelectedPathComponent()

            dmt?.run {
               val selectedObject = (dmt as DefaultMutableTreeNode).getUserObject() as KProductInfo
               val productInfoPath = "${Constants.USERS_DIR}${SLASH}${Ref.users().current.member.me.name}${SLASH}productinfo"
               Ref.summary.load( "${productInfoPath}${SLASH}${selectedObject.id}.html" )
            }
         }

         HAMBURGER ->
            Ref.hamburgerMenu.show()

         else -> {}
      }
   }
}
