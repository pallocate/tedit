package tedit.gui

import java.awt.Dimension
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.AbstractButton
import javax.swing.JToolBar
import javax.swing.ImageIcon
import tedit.EventHandler
import tedit.Lang
import tedit.utils.iconsDir

/** The application toolbar. */
class KToolbar (hamburgerButton : AbstractButton) : JToolBar(), ActionListener
{
   init
   {
      setFloatable( false )
      var button : JButton = JButton()

      addSeparator(Dimension( 7, 24 ))
      button.setIcon(ImageIcon(iconsDir.resolve( "document-save.png" ).toString()) )
      button.setBorder( null )
      button.setToolTipText( Lang.word( 18 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.SAVE )
      add( button )

      addSeparator(Dimension( 7, 24 ))
      button = JButton()
      button.setIcon(ImageIcon( iconsDir.resolve( "edit-find.png" ).toString()) )
      button.setBorder( null )
      button.setToolTipText( Lang.word( 251 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.SEARCH )
      add( button )

      addSeparator(Dimension( 7, 24 ))
      button = JButton()
      button.setIcon(ImageIcon( iconsDir.resolve( "list-add.png" ).toString()) )
      button.setBorder( null )
      button.setToolTipText( Lang.word( 352 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.ADD )
      add( button )

      addSeparator(Dimension( 7, 24 ))
      button = JButton()
      button.setIcon(ImageIcon( iconsDir.resolve( "list-remove.png" ).toString()) )
      button.setBorder( null )
      button.setToolTipText( Lang.word( 253 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.REMOVE )
      add( button )


      addSeparator(Dimension( 7, 24 ))
      button = JButton()
      button.setIcon(ImageIcon(iconsDir.resolve( "changes-prevent.png" ).toString()) )
      button.setBorder( null )
      button.setToolTipText( Lang.word( 354 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.EXPORT_ENCRYPTED )
      add( button )

      addSeparator(Dimension( 21, 24 ))
      button = JButton()
      button.setIcon(ImageIcon(iconsDir.resolve( "help-browser.png" ).toString()) )
      button.setBorder( null )
      button.setToolTipText( Lang.word( 28 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.HELP )
      add( button )

      add( javax.swing.Box.createHorizontalGlue() )
      hamburgerButton.setIcon(ImageIcon(iconsDir.resolve( "hamburger_icon_24.png" ).toString()) )
      hamburgerButton.setBorder( null )
      hamburgerButton.setPreferredSize(Dimension( 28, 28 ))
      hamburgerButton.setToolTipText( Lang.word( 35 ) )
      hamburgerButton.addActionListener( this )
      hamburgerButton.setActionCommand( EventHandler.HAMBURGER )

      add( hamburgerButton )
   }

   override fun actionPerformed (e : ActionEvent)
   { EventHandler.handle( e.getActionCommand() ) }
}
