package tedit.gui

import java.awt.Dimension
import java.awt.BorderLayout
import java.awt.event.WindowListener
import java.awt.event.WindowEvent
import java.nio.file.Paths
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE
import javax.swing.JSplitPane
import tedit.EventHandler
import tedit.Lang
import tedit.utils.usersDir

import tedit.session.Session

/** The main frame of the app. */
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

      val startPage = Paths.get( usersDir.toString(), Session.user.me.info.name, Session.settings.progression(), "index.html" )
      GUI.info.load( startPage.toFile() )
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
