package tedit.gui

import java.awt.event.ActionEvent
import javax.swing.ImageIcon
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import pen.par.KUser
import tedit.utils.iconsDir

/** A user selection dialog. */
class KUserSelect (users : List<KUser>) : SelectionDialog( null, "Select user" )
{
   override val TEXT = "Who are you?"
   override val selectionCombo = JComboBox<KUser>()

   init
   {
      selectionCombo.setModel(DefaultComboBoxModel( users.toTypedArray<KUser>() ))
      setIconImage(ImageIcon(iconsDir.resolve( "avatar-default.png" ).toString()).getImage())
      defaultSetup()
   }

   override fun actionPerformed (e : ActionEvent)
   {
      setVisible( false )
      dispose()
   }

   fun selectedUser () : KUser
   {
      var ret = KUser.void()
      val selected = selectionCombo.getSelectedItem()

      if (selected is KUser)
         ret = selected

      return ret
   }
}
