package tedit.gui

import java.awt.Frame
import java.awt.Dimension
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ActionListener
import javax.swing.*

abstract class SelectionDialog (owner : Frame?, title : String) : JDialog( owner, title, true ), ActionListener
{
   abstract protected val TEXT : String
   abstract protected val selectionCombo : JComboBox<out Any>

   protected fun defaultSetup ()
   {
      selectionCombo.setSelectedItem( null )
      selectionCombo.setSelectedIndex( -1 )
      selectionCombo.addActionListener( this )

      getContentPane().apply {
         setLayout(GridLayout( 4, 1 ))
         add( JLabel() )

         add( JPanel().apply {
            add(JLabel( TEXT ))
         })

         add( JPanel().apply {
            add( selectionCombo )  
         })

         add( JLabel() )
      }

      setSize( Dimension( 300, 400 ) )
      setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE )
      setLocationRelativeTo( null )
      setVisible( true )
   }
}

