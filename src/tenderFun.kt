package tedit

import javax.swing.JOptionPane
import javax.swing.JFileChooser
import pen.generateId
import pen.eco.KProposal
import pen.eco.KHeader
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
      val header = KHeader( generateId(), settings.year(), settings.iteration(), selectedRelation.target )
      val proposal = KProposal( header )
      val document = KTenderDocument( proposal, selectedRelation )
      val tab = document.proposalTable.tab

      GUI.tabs.addTab(Lang.word( 3 ), tab)
      GUI.tabs.setSelectedComponent( tab )
      document.proposalTable.setup()

      Session.documents.documentList.add( document )
      updateTitle()
   }
}

internal fun openDocument ()
{
   val fileChooser = GUI.fileChooser( FileCooserMode.OPEN )
   if (fileChooser.showOpenDialog( GUI.frame ) == JFileChooser.APPROVE_OPTION)
   {
      val selectedFile = fileChooser.getSelectedFile()
      val pathname = selectedFile.getAbsolutePath()
      val findResult = Session.documents.findOpen( pathname )

      if (findResult.isVoid())
      {
         if (selectedFile.exists() && pathname.endsWith( ".tdr" ))
         {
            val document = KTenderDocument.void()
            val documentOwner = document.load( pathname )

            if (documentOwner == Session.user.me.contact.id)
            {
               val tab = document.proposalTable.tab
               GUI.tabs.addTab( document.filename(), tab )
               GUI.tabs.setSelectedComponent( tab )

               document.proposalTable.setup()
               Session.documents.documentList.add( document )
               updateTitle()
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
   val choosenFile = chooseFile()

   if (choosenFile !is VoidFile)
   {
      document.pathname = choosenFile.absolutePath
      val name = choosenFile.name

      if (choosenFile.exists())
      {
         if (overwriteAccept( choosenFile ))
            success = actualSave( document )
      }
      else
         success = actualSave( document )
   }

   return success
}

internal fun exportEncrypted (document : KTenderDocument)
{
   val choosenFile = chooseFile( true )

   if (choosenFile !is VoidFile)
   {
      if (choosenFile.exists())
      {
         if (overwriteAccept( choosenFile ))
            document.saveEncrypted( choosenFile.name )
      }
      else
         document.saveEncrypted( choosenFile.name )
   }
}
