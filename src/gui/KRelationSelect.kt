package tedit.gui

import java.awt.event.ActionEvent
import javax.swing.JComboBox
import javax.swing.ImageIcon
import javax.swing.DefaultComboBoxModel
import pen.par.KRelation
import tedit.Lang
import tedit.utils.Constants

class KRelationSelect (relations : ArrayList<KRelation>) : SelectionDialog(GUI.frame, Lang.word( 330 ))
{
   override protected val TEXT                         = Lang.word( 206 )
   override protected val selectionCombo               = JComboBox<KRelation>()

   init
   {
      selectionCombo.setModel(DefaultComboBoxModel( relations.toTypedArray<KRelation>() ))
      setIconImage(ImageIcon( Constants.ICONS_DIR + Constants.SLASH + "system-users.png" ).getImage())
      defaultSetup()
   }

   override fun actionPerformed (e : ActionEvent)
   {
      setVisible( false )
      dispose()
   }

   fun selectedRelation () : KRelation
   {
      val selected = selectionCombo.getSelectedItem()

      return if (selected is KRelation)
         selected
      else
         KRelation.void()
   }
}
