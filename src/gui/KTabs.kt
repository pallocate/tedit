package tedit.gui

import java.io.File
import javax.swing.JTabbedPane
import javax.swing.JScrollPane
import javax.swing.event.ChangeListener
import javax.swing.event.ChangeEvent
import pen.eco.Target
import pen.par.KRelation
import tedit.updateTitle
import tedit.utils.progressPath
import tedit.session.Session

class KTabs () : JTabbedPane(), ChangeListener
{
   internal var activeTab : JScrollPane = VoidTab

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

            val otherId = proposalTable.tenderDocument.relation.other.id
            GUI.productTree.load(progressPath().resolve( otherId.toString() + ".xml" ).toFile())

            updateTitle()
         }
      }
   }
}
