package tedit.gui

import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JMenuBar
import javax.swing.JMenu
import javax.swing.JMenuItem
import tedit.EventHandler
import tedit.Lang

/** The application menubar. */
class KMenu () : JMenuBar(), ActionListener
{
   init
   {
      add( fileMenu() )
      add( searchMenu() )
      add( productMenu() )
      add( toolsMenu() )
      add( helpMenu() )
   }

   private fun fileMenu () : JMenu
   {
      val ret = JMenu(Lang.word( 14 ))
      var menuItem : JMenuItem

      menuItem = JMenuItem(Lang.word( 16 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.NEW )
      ret.add( menuItem )

      menuItem = JMenuItem(Lang.word( 17 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.OPEN )
      ret.add( menuItem )

      menuItem = JMenuItem(Lang.word( 18 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.SAVE )
      ret.add( menuItem )

      menuItem = JMenuItem(Lang.word( 19 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.SAVE_AS )
      ret.add( menuItem )

      ret.addSeparator()

      menuItem = JMenuItem(Lang.word( 302 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.EXPORT_ENCRYPTED )
      ret.add( menuItem )

      ret.addSeparator()

      menuItem = JMenuItem(Lang.word( 23 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.CLOSE )
      ret.add( menuItem )

      menuItem = JMenuItem(Lang.word( 12 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.QUIT )
      ret.add( menuItem )

      return ret
   }

   private fun searchMenu () : JMenu
   {
      val ret = JMenu(Lang.word( 20 ))
      var menuItem : JMenuItem

      menuItem = JMenuItem(Lang.word( 20 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.SEARCH )
      ret.add( menuItem )

      menuItem = JMenuItem(Lang.word( 22 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.SEARCH_NEXT )
      ret.add( menuItem )

      return ret
   }

   private fun productMenu () : JMenu
   {
      val ret = JMenu(Lang.word( 208 ))
      var menuItem : JMenuItem

      menuItem = JMenuItem(Lang.word( 25 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.ADD )
      ret.add( menuItem )

      menuItem = JMenuItem(Lang.word( 26 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.REMOVE )
      ret.add( menuItem )

      menuItem = JMenuItem(Lang.word( 7 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.CLEAR )
      ret.add( menuItem )

      return ret
   }

   private fun toolsMenu () : JMenu
   {
      val ret = JMenu(Lang.word( 21 ))
      var menuItem : JMenuItem

      menuItem = JMenuItem(Lang.word( 303 ) )
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.SAVE_SETTINGS )
      ret.add( menuItem );

      menuItem = JMenuItem(Lang.word( 27 ))
      menuItem.setEnabled( false )
      menuItem.setActionCommand( EventHandler.GET_UPDATES )
      ret.add( menuItem )

      menuItem = JMenuItem(Lang.word( 331 ))
      menuItem.setEnabled( false )
      menuItem.setActionCommand( EventHandler.ACCOUNT_EDIT )
      ret.add( menuItem )

      return ret
   }

   private fun helpMenu () : JMenu
   {
      val ret = JMenu(Lang.word( 28 ))
      var menuItem : JMenuItem

      menuItem = JMenuItem(Lang.word( 30 ))
      menuItem.addActionListener( this )
      menuItem.setActionCommand( EventHandler.ABOUT )
      ret.add( menuItem )

      return ret
   }

   override fun actionPerformed (e : ActionEvent)
   { EventHandler.handle( e.getActionCommand() ) }
}
