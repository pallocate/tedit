package tedit

import java.io.File
import java.nio.file.Paths
import javax.swing.JOptionPane
import javax.swing.JFileChooser
import pen.eco.Target
import tedit.Lang
import tedit.session.Session
import tedit.session.KTenderDocument
import tedit.gui.GUI
import tedit.utils.usersDir

class VoidFile : File( "" )

enum class FileCooserMode
{ OPEN, SAVE_AS, EXPORT }

/** Sets the title according to file name and modification status. */
internal fun updateTitle () = with( Session.documents.activeDocument )
{
   val title = Lang.word( 301 ) + " - " + filename() + if (proposalTable.modified) " *" else ""
   GUI.frame.setTitle( title )
}

/** Lets the user choose what file to save or export. */
internal fun chooseFile (encrypted : Boolean = false) : File
{
   var ret : File = VoidFile()
   val fileChooser = if (encrypted)
         GUI.fileChooser( FileCooserMode.EXPORT )
      else
         GUI.fileChooser( FileCooserMode.SAVE_AS )

   val result = fileChooser.showSaveDialog( GUI.frame )
   if (result == JFileChooser.APPROVE_OPTION)
      ret = fileChooser.selectedFile

   return ret
}

/** Gives the user a chance to bail out before overwriting a file. */
internal fun overwriteAccept (file : File) : Boolean
{
   val buttonTexts = arrayOf(Lang.word( 8 ), Lang.word( 9 ))
   val result = JOptionPane.showOptionDialog( GUI.frame, " \"${file.name}\" ${Lang.word(69)}\n${Lang.word(72)}",
      Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, buttonTexts, buttonTexts[1] )

   return result == JOptionPane.YES_OPTION
}

/** Displays product info in the info panel. */
internal fun showProductInfo (productId : String)
{
   val relation = Session.documents.activeDocument.relation

   if (!relation.isVoid() && relation.target > Target.UNDEFINED)
   {
      val infoPath = Paths.get( "users", Session.user.me.contact.info.name, if (relation.target == Target.PRODUCTION)
            "jobinfo"
         else
            "productinfo"
         , productId + ".html" )

      GUI.info.load( infoPath.toFile() )
   }
}

/** Informs about unsaved document and lets the user choose what to do. */
internal fun optionalSave (unsavedDocument : KTenderDocument) : Boolean
{
   var procede = true
   val buttonTexts = arrayOf( Lang.word( 18 ), Lang.word( 9 ), Lang.word( 11 ) )
   val choise = JOptionPane.showOptionDialog( GUI.frame, "\"${unsavedDocument.filename()}\" ${Lang.word(49)}\n${Lang.word(71)}",
   Lang.word( 34 ), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, buttonTexts, buttonTexts[2]);

   if (choise == JOptionPane.YES_OPTION)
      procede = saveDocument( unsavedDocument )
   else
      if (choise == JOptionPane.CANCEL_OPTION)
         procede = false

   return procede
}

/** Tries to save document, shows error message if it fails. */
internal fun actualSave (document : KTenderDocument) : Boolean
{
   val success = document.save()
   if (success)
   {
      document.proposalTable.modified = false
      GUI.tabs.setTitleAt( GUI.tabs.selectedIndex, document.filename() )
      updateTitle()
   }
   else
      JOptionPane.showMessageDialog(GUI.frame, "\"${document.filename()}\" ${Lang.word(73)}", Lang.word( 74 ), JOptionPane.ERROR_MESSAGE )

   return success
}
