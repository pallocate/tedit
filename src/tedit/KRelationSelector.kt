package tedit

import java.awt.Font
import java.awt.Dimension
import java.awt.Frame
import java.awt.GridLayout
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.border.EmptyBorder
import pen.par.KMember
import pen.par.Relation
import pen.par.NoRelation
import pen.par.KRelation
import apps.Constants

class KRelationSelector (val member : KMember, frame : Frame) : JDialog( frame, Ref.word( 206 ), true ), ActionListener
{
   var relation : Relation                             = NoRelation()

   private val okButton                                = JButton(Ref.word( 10 ))
   private val comboBox                                = JComboBox<KRelation>()

   private val OK                                      = "OK"
   private val RELATION_SELECT                         = "RELATION_SELECT"

   init
   {
      setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE )
      setSize( Dimension( 350, 350 ))
      setLocationRelativeTo( null )
      setLayout(GridLayout( 4, 1 ))
      add( JLabel() )

      with( comboBox )
      {
         setModel( DefaultComboBoxModel( member.economicRelations() ) )
         setActionCommand( RELATION_SELECT )
      }

      add( JPanel().apply {add( comboBox )} )
      add( JLabel() )

      val font = getFont().getName()
      with( okButton )
      {
         setFont(Font( font, Font.BOLD, 15 ))
         setEnabled( false )
         setActionCommand( OK )
      }

      add( JPanel().apply {
         setLayout(BoxLayout( this, BoxLayout.LINE_AXIS ))
         setBorder(EmptyBorder( 15, 0, 15, 0 ))
         add(Box.createHorizontalStrut( 270 ))
         add( okButton )
      }, BorderLayout.SOUTH )

      comboBox.addActionListener( this )
      okButton.addActionListener( this )

      setIconImage(ImageIcon( Constants.ICONS_DIR + Constants.SLASH+ "system-users.png" ).getImage())
      setVisible( true )
   }

   override fun actionPerformed (e : ActionEvent)
   {
      val actionCommand = e.getActionCommand()

      if (actionCommand == RELATION_SELECT)
         okButton.setEnabled( true )
      else
         if (actionCommand == OK)
         {
            val selected = comboBox.getSelectedItem()
            if (selected is KRelation)
            {
               relation = selected
               setVisible( false )
               dispose()
            }
         }
   }
}
