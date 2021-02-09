package tedit

import javax.swing.JOptionPane
import javax.swing.JFileChooser
import pen.generateId
import pen.eco.KProposal
import tedit.Lang
import tedit.session.Session
import tedit.session.KTenderDocument
import tedit.gui.GUI
import tedit.gui.KRelationSelect

internal fun newDocument ()
{
   val relations = Session.user.relations
   val selectedRelation = KRelationSelect( relations ).selectedRelation()

   if (!selectedRelation.isVoid())
   {
      val settings = Session.settings
      val header = pen.eco.KHeader( generateId(), settings.year(), settings.iteration(), selectedRelation.target )
      val proposal = pen.eco.KProposal( header )
      val document = KTenderDocument( proposal, selectedRelation )
      val tab = document.proposalTable.tab

      GUI.tabs.addTab(Lang.word( 3 ), tab)
      GUI.tabs.setSelectedComponent( tab )
      document.proposalTable.setup()

      updateTitle()
      Session.documents.documentList.add( document )
   }
}


internal fun openDocument ()
{
   val fileChooser = GUI.fileChooser
   fileChooser.setDialogTitle(Lang.word( 17 ))
   fileChooser.setApproveButtonText(Lang.word( 17 ))

   if (fileChooser.showOpenDialog( GUI.frame ) == JFileChooser.APPROVE_OPTION)
   {
      val selectedFile = fileChooser.getSelectedFile()
      val pathname = selectedFile.getAbsolutePath()
      val filename = fileChooser.getName( selectedFile )
      val findResult = Session.documents.findOpen( pathname )

      if (findResult.isVoid())
      {
         if (selectedFile.exists() && pathname.endsWith( ".tdr" ))
         {
            val document = KTenderDocument.void()
            val proposer = document.load( pathname )

            if (proposer == Session.user.me.id)
            {
               val tab = document.proposalTable.tab
               GUI.tabs.addTab( filename, tab )
               GUI.tabs.setSelectedComponent( tab )

               document.proposalTable.setup()
               updateTitle()

               Session.documents.documentList.add( document )
            }
         }
      }
      else
         GUI.tabs.setSelectedComponent( findResult.proposalTable.tab )
   }
}

internal fun closeDocument (document : KTenderDocument)
{
   GUI.tabs.remove( document.proposalTable.tab )
   Session.documents.documentList.remove( document )
   updateTitle()
}

internal fun saveDocument (document : KTenderDocument) : Boolean
{
   var success = false
   if (document.isPathSet())
   {
      success = actualSave( document )
      if (!success)
         success = saveDocumentAs( document )
   }
   else
      success = saveDocumentAs( document )

   return success
}

internal fun saveDocumentAs (document : KTenderDocument) : Boolean
{
   var success = false
   val fileChooser = GUI.fileChooser
   fileChooser.dialogTitle = Lang.word( 19 )
   fileChooser.approveButtonText = Lang.word( 18 )
   fileChooser.approveButtonToolTipText = Lang.word( 50 )

   val result1 = fileChooser.showSaveDialog( GUI.frame )
   if (result1 == JFileChooser.APPROVE_OPTION)
   {
      val selectedFile = fileChooser.selectedFile
      val name = fileChooser.getName( selectedFile )

      document.pathname = selectedFile.absolutePath

      if (selectedFile.exists())
      {
         val buttonTexts = arrayOf( Lang.word( 8 ), Lang.word( 9 ) )
         val result2 = JOptionPane.showOptionDialog( GUI.frame, " \"$name\" ${Lang.word(69)}\n${Lang.word(72)}",
            Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, buttonTexts, buttonTexts[1] )

         if (result2 == JOptionPane.YES_OPTION)
            success = actualSave( document )
      }
      else
         success = actualSave( document )
   }
   return success
}
