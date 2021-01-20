package tedit

import javax.swing.JOptionPane
import javax.swing.JFileChooser
import pen.Log
import pen.eco.KProposal
import tedit.utils.Constants.SLASH
import tedit.utils.Constants.USERS_DIR
import tedit.Lang
import tedit.session.Session
import tedit.session.KTenderDocument
import tedit.gui.GUI
import tedit.gui.KRelationSelector

/** Sets the title according to file name and modification status. */
internal fun updateTitle ()
{
   var title = Lang.word( 301 ) + " - "

   with( Session.documents.activeDocument )
   {
      title = title + pathname.substring( pathname.lastIndexOf( SLASH ) + 1 )

      if (proposalTable.modified)
         title = title + " *"
   }

   GUI.frame.setTitle( title )
}

internal fun progressPath () = USERS_DIR + SLASH + Session.users.activeUser.me.info.name + SLASH + Session.settings.progression()

/** Alerts user about unsaved documents. */
internal fun warnOnModified () : Boolean
{
   var procede = true

   if (Session.documents.unsaved().size > 0)
      procede = (JOptionPane.showConfirmDialog( GUI.frame, Lang.word( 49 ) + "\n" + Lang.word( 24 ),
      Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ) == JOptionPane.YES_OPTION)

   return procede
}

internal fun openTab ()
{
   val fileChooser = GUI.fileChooser
   fileChooser.setDialogTitle( "Open" )
   fileChooser.setApproveButtonText( "Open" )

   if (fileChooser.showOpenDialog( GUI.frame ) == JFileChooser.APPROVE_OPTION)
   {
      val selectedFile = fileChooser.getSelectedFile()
      val pathname = selectedFile.getAbsolutePath()
      val filename = fileChooser.getName( selectedFile )

      if (selectedFile.exists())
      {
         if (pathname.endsWith( ".tdr" ))
         {
            val document = KTenderDocument().apply {
               user = Session.users.activeUser
               load( pathname )
            }

            val tab = document.proposalTable.tab

            GUI.tabs.addTab( filename, tab )
            GUI.tabs.setSelectedComponent( tab )

            document.proposalTable.setup()
            updateTitle()
         }
         else
            Log.warn( "Open tender failed! (unspported file format)" )
      }
   }
}

/** Removes/clear the currently selected tab. */
internal fun closeTab ()
{
   if (GUI.tabs.getTabCount() > 1)
   {
      GUI.tabs.remove( GUI.tabs.activeTab )
      Session.documents.documentList.remove( Session.documents.activeDocument )
      updateTitle()
   }
   else
   {
      with (Session.documents.activeDocument) {
         proposal = KProposal()
         proposalTable.vanilla()
         pathname = Lang.word( 3 )
         proposalTable.setup()
      }
      updateTitle()
   }
}

internal fun saveTab ()
{
   val tenderEdit = GUI.frame
   val fileChooser = GUI.fileChooser
   fileChooser.setDialogTitle( "Save" )
   fileChooser.setApproveButtonText( "Save" )

   val selectedTab = GUI.tabs.getSelectedIndex()
   if (selectedTab >= 0)
   {
      val result1 = fileChooser.showSaveDialog( tenderEdit )
      if (result1 == JFileChooser.APPROVE_OPTION)
      {
         val document = Session.documents.activeDocument
         val selectedFile = fileChooser.selectedFile
         val name = fileChooser.getName( selectedFile )
         document.pathname = selectedFile.absolutePath

         if (selectedFile.exists())
         {
            val result2 = JOptionPane.showConfirmDialog( tenderEdit, " \"" + name + "\" " + Lang.word( 69 ) +
            "!\n" + Lang.word( 72 ) + " ?", Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE )
            if (result2 == JOptionPane.YES_OPTION)
            {
               if (document.save())
               {
                  document.proposalTable.modified = false
                  GUI.tabs.setTitleAt( selectedTab, name )
                  updateTitle()
               }
               else
                  JOptionPane.showMessageDialog(tenderEdit, Lang.word( 73 ) + " " + name, Lang.word( 74 ), JOptionPane.ERROR_MESSAGE )
            }
         }
         else
         {
            if (document.save())
            {
               Session.documents.activeDocument.proposalTable.modified = false
               GUI.tabs.setTitleAt(selectedTab, name)
               updateTitle()
            }
            else
               JOptionPane.showMessageDialog(tenderEdit, Lang.word( 73 ) + " " + document.pathname, Lang.word( 74 ), JOptionPane.ERROR_MESSAGE )
         }
      }
   }
}
