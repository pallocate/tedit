package tedit.gui

import java.awt.event.ActionEvent
import javax.swing.JComboBox
import javax.swing.ImageIcon
import javax.swing.DefaultComboBoxModel
import pen.par.KRelation
import tedit.Lang
import tedit.utils.iconsDir

/** A selection dialog to let the user select a relation. */
class KRelationSelect (relations : ArrayList<KRelation>) : SelectionDialog(GUI.frame, Lang.word( 330 ))
{
   override protected val TEXT                         = Lang.word( 206 )
   override protected val selectionCombo               = JComboBox<KRelation>()

   init
   {
      selectionCombo.setModel(DefaultComboBoxModel( relations.toTypedArray<KRelation>() ))
      setIconImage( ImageIcon(iconsDir.resolve( "system-users.png" ).toString()).getImage() )
      defaultSetup()
   }

   override fun actionPerformed (e : ActionEvent)
   {
      setVisible( false )
      dispose()
   }

   fun selectedRelation () : KRelation
   {
      var ret = KRelation.void()
      val selected = selectionCombo.getSelectedItem()

      if (selected is KRelation)
         ret = selected

      return ret

   }
}
