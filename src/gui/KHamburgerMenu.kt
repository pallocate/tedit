package tedit.gui

import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.ImageIcon
import javax.swing.JPopupMenu
import javax.swing.JButton
import javax.swing.AbstractButton
import javax.swing.JMenuItem
import javax.swing.border.BevelBorder
import tedit.EventHandler
import tedit.Lang
import tedit.utils.iconsDir

/** The "hamburger" menu in simple mode. */
class KHamburgerMenu () : JPopupMenu(), ActionListener
{
   internal var hamburgerButton : AbstractButton = JMenuItem()

   init
   {
      var menuItem : JMenuItem = JMenuItem(Lang.word( 303 ), ImageIcon(iconsDir.resolve( "preferences-system.png" ).toString()) )
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.SAVE_SETTINGS )
      add( menuItem );

      menuItem = JMenuItem(Lang.word( 27 ), ImageIcon(iconsDir.resolve( "emblem-downloads.png" ).toString()) )
      menuItem.setEnabled( false )
      menuItem.setActionCommand( EventHandler.GET_UPDATES )
      add( menuItem );

      menuItem = JMenuItem( Lang.word( 331 ), ImageIcon(iconsDir.resolve( "accessories-text-editor.png" ).toString()) )
      menuItem.setEnabled( false )
      menuItem.setActionCommand( EventHandler.ACCOUNT_EDIT )
      add( menuItem )

      setBorder(BevelBorder( BevelBorder.RAISED ))
      hamburgerButton = JButton()
   }

   override fun show () = super.show( hamburgerButton, -100, 28 )
   override fun actionPerformed (e : ActionEvent) = EventHandler.handle( e.getActionCommand() )
}
