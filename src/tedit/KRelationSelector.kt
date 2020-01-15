package tedit

import java.awt.Font
import java.awt.Dimension
import java.awt.Frame
import java.awt.GridLayout
import java.awt.BorderLayout
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.border.EmptyBorder
import pen.par.Relation
import pen.par.NoRelation
import pen.par.KRelation
import apps.Constants

class KRelationSelector (frame : Frame, user : User = NoUser()) : JDialog( frame, Lang.word( 330 ), true ), ActionListener
{
   var selectedUser : User                                     = NoUser()
   var selectedRelation : Relation                             = NoRelation()

   private val okButton                                = JButton(Lang.word( 10 ))
   private val cancelButton                            = JButton(Lang.word( 11 ))
   private val userCombo                               = JComboBox<KUser>()
   private val relationCombo                           = JComboBox<KRelation>()

   private val USER_SELECT                             = "USER_SELECT"
   private val RELATION_SELECT                         = "RELATION_SELECT"
   private val OK                                      = "OK"
   private val CANCEL                                  = "CANCEL"

   init
   {
      setLayout( BorderLayout() )
      val font = getFont().getName()

      okButton.setFont(Font( font, Font.BOLD, 15 ))
      okButton.setActionCommand( OK )
      okButton.addActionListener( this )
      cancelButton.setActionCommand( CANCEL )
      cancelButton.setFont(Font( font, Font.BOLD, 15 ))
      cancelButton.addActionListener( this )

      relationCombo.setActionCommand( RELATION_SELECT )
      userCombo.setActionCommand( USER_SELECT )

      /* Start with everything disabled */
      okButton.setEnabled( false )
      relationCombo.setSelectedItem( null )
      relationCombo.setEnabled( false )
      userCombo.setSelectedItem( null )
      userCombo.setEnabled( false )

      /* Users */
      if (user is KUser)
      {
         userCombo.setModel( DefaultComboBoxModel(arrayOf( user )) )
         userCombo.setSelectedIndex( 0 )
         doRelations()
      }
      else
      {
         val userArray = Ref.users().userArray

         if(userArray.isEmpty())
            throw Exception( "No users found!" )
         else
         {
            userCombo.setModel( DefaultComboBoxModel( userArray ) )
            userCombo.setSelectedIndex( 0 )

            if (userCombo.getItemCount() == 1)
               doRelations()
            else
            {
               userCombo.setEnabled( true )
               userCombo.addActionListener( this )
            }
         }
      }

      /* Add stuff */
      add(JPanel().apply {
         setLayout(GridLayout( 8, 1 ))
         add( JLabel() )
         add( JPanel().apply {add( JLabel(Lang.word( 205 )) )} )
         add( JPanel().apply {add( userCombo )} )

         add( JLabel() )
         add( JPanel().apply {add( JLabel(Lang.word( 206 ) + ":") )} )
         add( JPanel().apply {add( relationCombo )} )

      }, BorderLayout.CENTER)

      add(JPanel().apply {
         setLayout(GridLayout( 1, 5 ))
         setBorder(EmptyBorder( 15, 0, 15, 0 ))
         add( JLabel() )
         add( cancelButton )
         add( JLabel() )
         add( okButton )
         add( JLabel() )
      }, BorderLayout.SOUTH)

      setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE )
      setSize( Dimension( 460, 460 ))
      setLocationRelativeTo( null )
      setIconImage(ImageIcon( Constants.ICONS_DIR + Constants.SLASH+ "system-users.png" ).getImage())
      setVisible( true )
   }

   /* Relations */
   private fun doRelations ()
   {
//      userCombo.setEnabled( false )
      selectedUser = userCombo.getSelectedItem() as KUser
      val relations = (selectedUser as KUser).member.economicRelations()

      if (relations.size == 0)
         throw Exception( "No relations" )
      else
      {
         relationCombo.setModel(DefaultComboBoxModel( relations ))
         relationCombo.setSelectedIndex( 0 )

         if (relationCombo.getItemCount() == 1)
            okButton.setEnabled( true )
         else
         {
            relationCombo.setEnabled( true )
            if (relationCombo.getActionListeners().size < 1)
               relationCombo.addActionListener( this )
         }
      }
   }

   override fun actionPerformed (e : ActionEvent)
   {
      val actionCommand = e.getActionCommand()

      when (actionCommand)
      {
         USER_SELECT ->
            doRelations()
         RELATION_SELECT ->
            okButton.setEnabled( true )
         OK->
         {
            val selected = relationCombo.getSelectedItem()
            if (selected is KRelation)
            {
               Ref.users().activate( selectedUser as KUser )
               selectedRelation = selected
               setVisible( false )
               dispose()
            }
         }
         CANCEL ->
         {
            selectedRelation = NoRelation()
            setVisible( false )
            dispose()
         }
         else -> {}
      }
   }
}
