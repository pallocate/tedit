package tedit.gui

import javax.swing.JTabbedPane
import javax.swing.JScrollPane
import javax.swing.event.ChangeListener
import javax.swing.event.ChangeEvent
import pen.eco.Target
import pen.par.KRelation
import tedit.utils.Constants.USERS_DIR
import tedit.utils.Constants.SLASH
import tedit.updateTitle
import tedit.progressPath
import tedit.session.Session

object NoTab : JScrollPane()

class KTabs () : JTabbedPane(), ChangeListener
{
   internal var activeTab : JScrollPane = NoTab

   init
   {addChangeListener( this )}

   /** Responds to tab selection changes. Sets current tab, and application title accordingly. */
   override fun stateChanged (e : ChangeEvent)
   {
      val selected = getSelectedComponent()
      if (selected is JScrollPane)
      {
         activeTab = selected
         val proposalTable = activeTab.viewport.view

         if (proposalTable is KProposalTable)
         {
            Session.documents.activate( proposalTable.tenderDocument )
            val target = proposalTable.tenderDocument.relation.target

            if (target > Target.UNDEFINED)
               GUI.productTree.load( progressPath() + SLASH + target.name.toLowerCase() + ".xml" )

            updateTitle()
         }
      }
   }
}
