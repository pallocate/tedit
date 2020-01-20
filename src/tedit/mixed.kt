package tedit

import javax.swing.JOptionPane
import apps.Constants.SLASH

/** Sets the title according to file name and modification status. */
fun updateTitle ()
{
   var title = Lang.word( 301 ) + " - "

   with( Tabs.current )
   {
      title = title + filename.substring( filename.lastIndexOf( SLASH ) + 1 )

      if (proposalTable.modified)
         title = title + " *"
   }

   GUI.frame.setTitle( title )
}

/** Alerts user to modifications.
  * @return True if we shoud precede doing what we where doing. */
fun handleModifications () : Boolean
{
   var modified = false

   for (a in 0 until Tabs.getTabCount())
   {
      val tabComponent = Tabs.getComponentAt( a )
      if (tabComponent is KTenderTab)
      {
         val tenderTab = tabComponent
         modified = modified || tenderTab.proposalTable.modified
      }
   }
   var procede = true

   if (modified == true)
      procede = (JOptionPane.showConfirmDialog( GUI.frame, Lang.word( 49 ) + "\n" + Lang.word( 24 ),
      Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)

   return procede
}
