package tedit

import java.awt.*
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
   var selectedUser : User                             = NoUser()
   var selectedRelation : Relation                     = NoRelation()

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
      val font = Font( getFont().getName(), Font.BOLD, 15 )
      okButton.setFont( font )
      okButton.setActionCommand( OK )
      okButton.addActionListener( this )
      cancelButton.setActionCommand( CANCEL )
      cancelButton.setFont( font )
      cancelButton.addActionListener( this )
      relationCombo.setActionCommand( RELATION_SELECT )
      userCombo.setActionCommand( USER_SELECT )

      /* Start with everything disabled */
      okButton.setEnabled( false )
      relationCombo.setEnabled( false )
      relationCombo.setSelectedItem( null )
      userCombo.setEnabled( false )

      /* Users */
      if (user is KUser)
      {
         userCombo.setModel( DefaultComboBoxModel(arrayOf( user )) )
         userCombo.setSelectedIndex( 0 )
         doTheRelations()
      }
      else
      {
         val userArray = KUsers.instance.userArray

         if(userArray.isEmpty())
            throw Exception( "No users found!" )
         else
         {
            userCombo.setModel( DefaultComboBoxModel( userArray ) )
            userCombo.setSelectedIndex( -1 )

            if (userCombo.getItemCount() == 1)
               doTheRelations()
            else
            {
               userCombo.setEnabled( true )
               userCombo.addActionListener( this )
            }
         }
      }

      layoutGUI()

      setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE )
      setLocationRelativeTo( null )
      setIconImage(ImageIcon( Constants.ICONS_DIR + Constants.SLASH+ "system-users.png" ).getImage())
      setVisible( true )
   }

   override fun actionPerformed (e : ActionEvent)
   {
      val actionCommand = e.getActionCommand()

      when (actionCommand)
      {
         USER_SELECT ->
            doTheRelations()
         RELATION_SELECT ->
            okButton.setEnabled( true )
         OK->
         {
            val selected = relationCombo.getSelectedItem()
            if (selected is KRelation)
            {
               KUsers.instance.activate( selectedUser as KUser )
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

   /* Relations */
   private fun doTheRelations ()
   {
//      userCombo.setEnabled( false )
      selectedUser = userCombo.getSelectedItem() as KUser
      val relations = (selectedUser as KUser).member.economicRelations()

      if (relations.size == 0)
         throw Exception( "No relations" )
      else
      {
         relationCombo.setModel(DefaultComboBoxModel( relations ))
         relationCombo.setSelectedIndex( -1 )

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

   /** The layout is a crude NetBeans-GUI traslation to Kotlin.  */
   private fun layoutGUI ()
   {
      val jLabel1 = JLabel().apply { setText(Lang.word( 205 )) }
      val jLabel2 = JLabel().apply { setText(Lang.word( 206 )) }


      val jPanel1 = JPanel()
      val jPanel2 = JPanel()
      val jPanel1Layout = GroupLayout( jPanel1 )
      jPanel1.setLayout( jPanel1Layout )

      jPanel1Layout.setHorizontalGroup( jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING ).addGroup(
         GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addContainerGap( GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE )
            .addComponent( cancelButton )
            .addPreferredGap( LayoutStyle.ComponentPlacement.UNRELATED )
            .addComponent( okButton )
            .addContainerGap()
         ) )

      jPanel1Layout.setVerticalGroup( jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
         jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent( okButton )
            .addComponent( cancelButton ))
            .addContainerGap( 33, Int.MAX_VALUE )
         ) )

      val jPanel2Layout = GroupLayout( jPanel2 )
      jPanel2.setLayout( jPanel2Layout )

      jPanel2Layout.setHorizontalGroup( jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
         GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
            .addContainerGap( 96, Int.MAX_VALUE )
            .addGroup(
               jPanel2Layout.createParallelGroup( GroupLayout.Alignment.LEADING )
               .addComponent( relationCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
               .addComponent( userCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
               .addComponent( jLabel2 )
               .addComponent( jLabel1 )
            )
            .addGap( 86, 86, 86 )
      ) )

      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            jPanel2Layout.createSequentialGroup()
            .addGap( 44, 44, 44 )
            .addComponent( jLabel1 )
            .addPreferredGap( LayoutStyle.ComponentPlacement.RELATED )
            .addComponent( userCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
            .addGap( 28, 28, 28 )
            .addComponent( jLabel2 )
            .addPreferredGap( LayoutStyle.ComponentPlacement.RELATED )
            .addComponent( relationCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
            .addContainerGap( 62, Int.MAX_VALUE )
         )
      )

      val layout = GroupLayout( getContentPane() )
      getContentPane().setLayout( layout )

      layout.setHorizontalGroup(
         layout.createParallelGroup( GroupLayout.Alignment.LEADING )
         .addComponent( jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE )
         .addComponent( jPanel2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE )
      )

      layout.setVerticalGroup(
         layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
      )

      pack()
   }
}
