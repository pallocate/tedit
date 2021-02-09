package tedit

import javax.swing.JOptionPane
import pen.eco.Target
import tedit.utils.Constants.SLASH
import tedit.utils.Constants.USERS_DIR
import tedit.Lang
import tedit.session.Session
import tedit.session.KTenderDocument
import tedit.gui.GUI

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
