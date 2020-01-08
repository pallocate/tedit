package tedit

import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.event.TreeSelectionListener
import javax.swing.event.TreeSelectionEvent
import pen.Log
import pen.par.KMember
import pen.par.KRelation
import pen.par.KMutableTender

/** Removes/clear the currently selected tab. */
fun removeCurrentTab ()
{
   with (Ref)
   {
      if (tabbedPane.getTabCount() > 1)
      {
         tabbedPane.remove( currentTab )
         currentTab = tabbedPane.getSelectedComponent() as KTableTab
         currentTab.updateTitle()
      }
      else
      {
         currentTab.table.vanilla()
         currentTab.table.setup()
         currentTab.updateTitle()
      }
   }
}

/** Alerts user to modifications.
  * @return True if we shoud precede doing what we where doing. */
fun handleModifications () : Boolean
{
   var modified = false

   for (a in 0 until Ref.tabbedPane.getTabCount())
   {
      val tabComponent = Ref.tabbedPane.getComponentAt( a )
      if (tabComponent is KTableTab)
      {
         val tableTab = tabComponent
         modified = modified || tableTab.table.modified
      }
   }
   var procede = true

   if (modified == true)
      procede = (JOptionPane.showConfirmDialog( Ref.pe(), Ref.word( 49 ) + "\n" + Ref.word( 24 ),
      Ref.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)

   return procede
}

fun openTender ()
{
   val fileChooser = Ref.fileChooser
   fileChooser.setDialogTitle( "Open" )
   fileChooser.setApproveButtonText( "Open" )

   if (fileChooser.showOpenDialog( Ref.pe() ) == JFileChooser.APPROVE_OPTION)
   {
      val selectedFile = fileChooser.getSelectedFile()
      val pathname = selectedFile.getAbsolutePath()
      val name = fileChooser.getName( selectedFile )

      if (selectedFile.exists())
      {
         if (name.endsWith( ".tdr" ))
         {
            val member = Ref.settings.currentUser().member
            if (member is KMember)
            {
               val relationSelector = KRelationSelector( member, Ref.pe() )
               val rel = relationSelector.relation

               if (rel is KRelation)
               {
                  val tableTab = KTableTab(KMutableTender( relation = rel ))
                  tableTab.loadTender( pathname )

                  Ref.tabbedPane.addTab( name, tableTab )
                  Ref.tabbedPane.setSelectedComponent( tableTab )
                  Ref.currentTab.table.setup()

                  tableTab.updateTitle()
               }
               else
                  Log.warn( "Open proposal failed (no role selected)" )
            }
         }
         else
            Log.warn( "Open proposal failed (unspported file formet)" )
      }
   }
}

fun saveTender ()
{
   val proposalEditor = Ref.pe()
   val fileChooser = Ref.fileChooser
   fileChooser.setDialogTitle( "Save" )
   fileChooser.setApproveButtonText( "Save" )

   val result1 = fileChooser.showSaveDialog( proposalEditor )
   if (result1 == JFileChooser.APPROVE_OPTION)
   {
      val selectedFile = fileChooser.getSelectedFile()
      val pathname = selectedFile.getAbsolutePath()
      val name = fileChooser.getName( selectedFile )

      if (fileChooser.getSelectedFile().exists())
      {
         val result2 = JOptionPane.showConfirmDialog(proposalEditor, " \"" + name + "\" " + Ref.word( 69 ) +
         "!\n" + Ref.word( 72 ) + " ?", Ref.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE )
         if (result2 == JOptionPane.YES_OPTION)
         {
            Ref.currentTab.filename = pathname
            if (Ref.currentTab.saveTender())
            {
               Ref.currentTab.table.modified = false
               Ref.tabbedPane.setTitleAt(Ref.tabbedPane.getSelectedIndex(), name)
               Ref.currentTab.updateTitle()
            }
            else
               JOptionPane.showMessageDialog(proposalEditor, Ref.word( 73 ) + " " + name, Ref.word( 74 ), JOptionPane.ERROR_MESSAGE )
         }
      }
      else
      {
         Ref.currentTab.filename = pathname
         if (Ref.currentTab.saveTender())
         {
            Ref.currentTab.table.modified = false
            Ref.tabbedPane.setTitleAt(Ref.tabbedPane.getSelectedIndex(), name)
            Ref.currentTab.updateTitle()
         }
         else
            JOptionPane.showMessageDialog(proposalEditor, Ref.word( 73 ) + " " + pathname, Ref.word( 74 ), JOptionPane.ERROR_MESSAGE )
      }
   }
}

class KTreeSelectionHandler : TreeSelectionListener
{
   override fun valueChanged (e : TreeSelectionEvent)
   { EventHandler.handle( EventHandler.TREE_SELECTION ) }
}

class KMouseHandler : MouseListener
{
   override fun mouseClicked (e : MouseEvent)
   {
      if (SwingUtilities.isLeftMouseButton( e ) && e.getClickCount() == 2)
         EventHandler.handle( EventHandler.ADD )
   }

   override fun mouseEntered (e : MouseEvent) {}
   override fun mouseExited (e : MouseEvent) {}
   override fun mousePressed (e : MouseEvent) {}
   override fun mouseReleased (e : MouseEvent) {}
}

