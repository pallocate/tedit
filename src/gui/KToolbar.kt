package tedit.gui

import java.awt.Dimension
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.AbstractButton
import javax.swing.JToolBar
import javax.swing.ImageIcon
import pen.Constants.SLASH
import tedit.utils.Constants.ICONS_DIR
import tedit.EventHandler
import tedit.Lang

/** The application toolbar. */
class KToolbar (hamburgerButton : AbstractButton) : JToolBar(), ActionListener
{
   init
   {
      setFloatable( false )
      var button : JButton = JButton()

      addSeparator(Dimension( 7, 24 ))
      button.setIcon(ImageIcon( "${ICONS_DIR}${SLASH}document-save.png" ))
      button.setBorder( null )
      button.setToolTipText( Lang.word( 18 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.SAVE )
      add( button )

      addSeparator(Dimension( 7, 24 ))
      button = JButton()
      button.setIcon(ImageIcon( "${ICONS_DIR}${SLASH}edit-find.png" ))
      button.setBorder( null )
      button.setToolTipText( Lang.word( 251 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.SEARCH )
      add( button )

      addSeparator(Dimension( 7, 24 ))
      button = JButton()
      button.setIcon(ImageIcon( "${ICONS_DIR}${SLASH}list-add.png" ))
      button.setBorder( null )
      button.setToolTipText( Lang.word( 352 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.ADD )
      add( button )

      addSeparator(Dimension( 7, 24 ))
      button = JButton()
      button.setIcon(ImageIcon( "${ICONS_DIR}${SLASH}list-remove.png" ))
      button.setBorder( null )
      button.setToolTipText( Lang.word( 253 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.REMOVE )
      add( button )


      addSeparator(Dimension( 7, 24 ))
      button = JButton()
      button.setIcon(ImageIcon( "${ICONS_DIR}${SLASH}emblem-default.png" ))
      button.setBorder( null )
      button.setToolTipText( Lang.word( 354 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.COMMIT )
      add( button )

      addSeparator(Dimension( 21, 24 ))
      button = JButton()
      button.setIcon(ImageIcon( "${ICONS_DIR}${SLASH}help-browser.png" ))
      button.setBorder( null )
      button.setToolTipText( Lang.word( 28 ) )
      button.addActionListener( this )
      button.setActionCommand( EventHandler.HELP )
      add( button )

      add( javax.swing.Box.createHorizontalGlue() )
      hamburgerButton.setIcon(ImageIcon( "${ICONS_DIR}${SLASH}hamburger_icon_24.png" ))
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
