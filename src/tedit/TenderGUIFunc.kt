package tedit

import javax.swing.JFileChooser
import javax.swing.JOptionPane
import pen.Log
import pen.par.KRelation
import pen.par.KMutableTender
//import apps.Constants.SLASH

object TenderGUIFunc
{
   fun openTab ()
   {
      val fileChooser = Ref.fileChooser
      fileChooser.setDialogTitle( "Open" )
      fileChooser.setApproveButtonText( "Open" )

      if (fileChooser.showOpenDialog( KTenderEdit.instance ) == JFileChooser.APPROVE_OPTION)
      {
         val selectedFile = fileChooser.getSelectedFile()
         val pathname = selectedFile.getAbsolutePath()
         val name = fileChooser.getName( selectedFile )

         if (selectedFile.exists())
         {
            if (name.endsWith( ".tdr" ))
            {
               val relationSelector = KRelationSelector( KTenderEdit.instance )
               val rel = relationSelector.selectedRelation

               if (rel is KRelation)
               {
                  val tenderTab = KTenderTab()
                  tenderTab.load( pathname )
                  (tenderTab.tender as KMutableTender).relation = rel

                  Tabs.addTab( name, tenderTab )
                  Tabs.setSelectedComponent( tenderTab )
                  tenderTab.proposalTable.setup()

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

   /** Removes/clear the currently selected tab. */
   fun closeTab ()
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

   fun saveTab ()
   {
      val tenderEdit = KTenderEdit.instance
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
}
