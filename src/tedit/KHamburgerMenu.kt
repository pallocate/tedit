package tedit

import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.ImageIcon
import javax.swing.JPopupMenu
import javax.swing.JButton
import javax.swing.AbstractButton
import javax.swing.JMenuItem
import javax.swing.border.BevelBorder
import apps.Constants

/** The "hamburger" menu in simple mode. */
class KHamburgerMenu () : JPopupMenu(), ActionListener
{
   var hamburgerButton : AbstractButton = JMenuItem()

   init
   {
      var menuItem : JMenuItem = JMenuItem(Lang.word( 303 ), ImageIcon( "${Constants.ICONS_DIR}${Constants.SLASH}preferences-system.png" ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.SAVE_SETTINGS )
      add( menuItem );

      menuItem = JMenuItem(Lang.word( 27 ), ImageIcon( "${Constants.ICONS_DIR}${Constants.SLASH}emblem-downloads.png" ))
      menuItem.setEnabled( false )
      menuItem.setActionCommand( EventHandler.GET_UPDATES )
      add( menuItem );

      menuItem = JMenuItem( Lang.word( 331 ), ImageIcon( "${Constants.ICONS_DIR}${Constants.SLASH}accessories-text-editor.png" ))
      menuItem.setEnabled( false )
      menuItem.setActionCommand( EventHandler.ACCOUNT_EDIT )
      add( menuItem )

      setBorder(BevelBorder( BevelBorder.RAISED ))
      hamburgerButton = JButton()
   }

   override fun show () = super.show( hamburgerButton, -100, 28 )
   override fun actionPerformed (e : ActionEvent) = EventHandler.handle( e.getActionCommand() )
}
