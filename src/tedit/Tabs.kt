package tedit

import javax.swing.JTabbedPane
import javax.swing.event.ChangeListener
import javax.swing.event.ChangeEvent
import pen.eco.Target
import pen.par.KMutableTender
import apps.Constants
import apps.Constants.SLASH

object Tabs : JTabbedPane(), ChangeListener
{
   var current = KTenderTab()

   init
   {addChangeListener( this )}

   /** Responds to tab selection changes. Sets current tab, and application title accordingly. */
   override fun stateChanged (e : ChangeEvent)
   {
      val selectedTab = Tabs.getSelectedComponent()
      if (selectedTab is KTenderTab)
      {
         val member = KUsers.instance.current.member
         val tender = selectedTab.tender

         if (tender is KMutableTender)
         {
            val target = tender.relation.target
            if (target != Target.UNDEFINED)
            {
               val progressPath = Constants.USERS_DIR + SLASH + member.me.name + SLASH + KSettings.instance.progress
               GUI.productTree.load( progressPath + SLASH + target.name.toLowerCase() + ".xml" )
            }
         }

         current = selectedTab
         updateTitle()
      }
   }
}
