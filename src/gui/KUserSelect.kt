package tedit.gui

import java.awt.event.ActionEvent
import javax.swing.ImageIcon
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import pen.par.KUser
import tedit.utils.Constants

class KUserSelect (users : List<KUser>) : SelectionDialog( null, "Select user" )
{
   override protected val TEXT                         = "Who are you?"
   override protected val selectionCombo               = JComboBox<KUser>()

   init
   {
      selectionCombo.setModel(DefaultComboBoxModel( users.toTypedArray<KUser>() ))
      setIconImage(ImageIcon( Constants.ICONS_DIR + Constants.SLASH + "avatar-default.png" ).getImage())
      defaultSetup()
   }

   override fun actionPerformed (e : ActionEvent)
   {
      setVisible( false )
      dispose()
   }

   fun selectedUser () : KUser
   {
      val selected = selectionCombo.getSelectedItem()

      return if (selected is KUser)
         selected
      else
         KUser.void()
   }
}
