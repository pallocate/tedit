package tedit.gui

import java.awt.Dimension
import java.awt.BorderLayout
import java.awt.event.WindowListener
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE
import javax.swing.JSplitPane
import pen.Constants.SLASH
import tedit.utils.Constants.USERS_DIR
import tedit.EventHandler
import tedit.Lang

import tedit.session.Session

/** The main frame/GUI of the app. */
class KFrame () : JFrame(), WindowListener
{
   init
   {
      val splitpane = JSplitPane().apply {
         setDividerSize( 4 )
         setDividerLocation( 275 )
         setLeftComponent(JScrollPane( GUI.productTree ))
         setRightComponent( JSplitPane().apply {
            setDividerSize( 6 )
            setDividerLocation( 450 )
            setLeftComponent( GUI.tabs )
            setRightComponent( GUI.info )
         })
      }

      with( getContentPane() ) {
         if (Session.settings.toolbar)
            add(KToolbar( GUI.hamburgerMenu.hamburgerButton ), BorderLayout.NORTH)

         add( splitpane, BorderLayout.CENTER )
         add(GUI.statusBar, BorderLayout.SOUTH)
      }

      val START_PAGE = USERS_DIR + SLASH + Session.user.me.info.name + SLASH + Session.settings.progression() + SLASH + "index.html"
      GUI.info.load( START_PAGE )
      setJMenuBar( KMenu() )

      setTitle(Lang.word( 301 ))
      setSize( Dimension( 1360, 768 ) )
      setDefaultCloseOperation( DO_NOTHING_ON_CLOSE )
      addWindowListener( this )
      setVisible( true )
   }

   override fun windowClosing (e : WindowEvent)
   {
      EventHandler.handle( EventHandler.QUIT )
   }
   override fun windowActivated (e : WindowEvent) {}
   override fun windowClosed (e : WindowEvent) {}
   override fun windowDeactivated (e : WindowEvent) {}
   override fun windowDeiconified (e : WindowEvent) {}
   override fun windowIconified (e : WindowEvent) {}
   override fun windowOpened (e : WindowEvent) {}
}
