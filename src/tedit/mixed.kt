package tedit

import javax.swing.JFileChooser
import javax.swing.JOptionPane
import pen.Log
import pen.par.KRelation
import pen.par.KMutableTender
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

   KTenderEdit.tenderEdit().setTitle( title )
}

/** Removes/clear the currently selected tab. */
fun removeCurrentTab ()
{
   if (Tabs.getTabCount() > 1)
   {
      Tabs.remove( Tabs.current )
      Tabs.current = Tabs.getSelectedComponent() as KTenderTab
      updateTitle()
   }
   else
   {
      Tabs.current.proposalTable.vanilla()
      Tabs.current.filename = Lang.word( 3 )
      Tabs.current.proposalTable.setup()
      updateTitle()
   }
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
      procede = (JOptionPane.showConfirmDialog( KTenderEdit.tenderEdit(), Lang.word( 49 ) + "\n" + Lang.word( 24 ),
      Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)

   return procede
}

fun openTender ()
{
   val fileChooser = Ref.fileChooser
   fileChooser.setDialogTitle( "Open" )
   fileChooser.setApproveButtonText( "Open" )

   if (fileChooser.showOpenDialog( KTenderEdit.tenderEdit() ) == JFileChooser.APPROVE_OPTION)
   {
      val selectedFile = fileChooser.getSelectedFile()
      val pathname = selectedFile.getAbsolutePath()
      val name = fileChooser.getName( selectedFile )

      if (selectedFile.exists())
      {
         if (name.endsWith( ".tdr" ))
         {
            val member = Ref.users().current.member
            val relationSelector = KRelationSelector( member, KTenderEdit.tenderEdit() )
            val rel = relationSelector.relation

            if (rel is KRelation)
            {
               val tenderTab = KTenderTab(KMutableTender())
               tenderTab.load( pathname )
               (tenderTab.tender as KMutableTender).relation = rel

               Tabs.addTab( name, tenderTab )
               Tabs.setSelectedComponent( tenderTab )
               Tabs.current.proposalTable.setup()

               updateTitle()
            }
            else
               Log.warn( "Open tender failed (no role selected)" )
         }
         else
            Log.warn( "Open tender failed (unspported file format)" )
      }
   }
}

fun saveTender ()
{
   val tenderEdit = KTenderEdit.tenderEdit()
   val fileChooser = Ref.fileChooser
   fileChooser.setDialogTitle( "Save" )
   fileChooser.setApproveButtonText( "Save" )

   val result1 = fileChooser.showSaveDialog( tenderEdit )
   if (result1 == JFileChooser.APPROVE_OPTION)
   {
      val selectedFile = fileChooser.getSelectedFile()
      val pathname = selectedFile.getAbsolutePath()
      val name = fileChooser.getName( selectedFile )

      if (fileChooser.getSelectedFile().exists())
      {
         val result2 = JOptionPane.showConfirmDialog(tenderEdit, " \"" + name + "\" " + Lang.word( 69 ) +
         "!\n" + Lang.word( 72 ) + " ?", Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE )
         if (result2 == JOptionPane.YES_OPTION)
         {
            Tabs.current.filename = pathname
            if (Tabs.current.save())
            {
               Tabs.current.proposalTable.modified = false
               Tabs.setTitleAt(Tabs.getSelectedIndex(), name)
               updateTitle()
            }
            else
               JOptionPane.showMessageDialog(tenderEdit, Lang.word( 73 ) + " " + name, Lang.word( 74 ), JOptionPane.ERROR_MESSAGE )
         }
      }
      else
      {
         Tabs.current.filename = pathname
         if (Tabs.current.save())
         {
            Tabs.current.proposalTable.modified = false
            Tabs.setTitleAt(Tabs.getSelectedIndex(), name)
            updateTitle()
         }
         else
            JOptionPane.showMessageDialog(tenderEdit, Lang.word( 73 ) + " " + pathname, Lang.word( 74 ), JOptionPane.ERROR_MESSAGE )
      }
   }
}
