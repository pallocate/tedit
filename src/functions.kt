package tedit

import java.io.File
import javax.swing.JOptionPane
import javax.swing.JFileChooser
import pen.eco.Target
import tedit.utils.Constants.SLASH
import tedit.utils.Constants.USERS_DIR
import tedit.Lang
import tedit.session.Session
import tedit.session.KTenderDocument
import tedit.gui.GUI

class VoidFile : File( "" )

enum class FileCooserMode
{ OPEN, SAVE_AS, EXPORT }

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

internal fun progressPath () = USERS_DIR + SLASH + Session.user.me.info.name + SLASH + Session.settings.progression()

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

internal fun overwriteAccept (file : File) : Boolean
{
   val buttonTexts = arrayOf(Lang.word( 8 ), Lang.word( 9 ))
   val result = JOptionPane.showOptionDialog( GUI.frame, " \"${file.name}\" ${Lang.word(69)}\n${Lang.word(72)}",
      Lang.word( 34 ), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, buttonTexts, buttonTexts[1] )

   return result == JOptionPane.YES_OPTION
}

internal fun showProductInfo (productId : String)
{
   val relation = Session.documents.activeDocument.relation

   if (!relation.isVoid() && relation.target > Target.UNDEFINED)
   {
      val productsDir = if (relation.target == Target.PRODUCTION)
            "jobinfo"
         else
            "productinfo"

      val productInfoPath = "${USERS_DIR}${SLASH}${Session.user.me.info.name}${SLASH}$productsDir"
      GUI.info.load( "${productInfoPath}${SLASH}${productId}.html" )
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
