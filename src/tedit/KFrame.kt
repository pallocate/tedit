package tedit

import java.awt.Dimension
import java.awt.BorderLayout
import java.awt.event.WindowListener
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE
import javax.swing.JSplitPane
import pen.par.KMutableTender
import apps.Constants.USERS_DIR
import pen.Constants.SLASH

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
            setLeftComponent( Tabs )
            setRightComponent( GUI.info )
         })
      }

      with( getContentPane() ) {
         if (KSettings.instance.toolbar)
            add(KToolbar( GUI.hamburgerMenu.hamburgerButton ), BorderLayout.NORTH)

         add( splitpane, BorderLayout.CENTER )
         add(GUI.statusBar, BorderLayout.SOUTH)
      }

      val START_PAGE = USERS_DIR + SLASH + KUsers.instance.current.member.me.name() + SLASH + KSettings.instance.progress + SLASH + "index.html"
      GUI.info.load( START_PAGE )
      setJMenuBar( KMenu() )

      setTitle( Lang.word( 301 ) + " - " + Lang.word( 3 ) )
      setSize( Dimension( 1360, 768 ) )
      setDefaultCloseOperation( DO_NOTHING_ON_CLOSE )
      addWindowListener( this )
      setVisible( true )
   }

   override fun windowClosing (e : WindowEvent)
   {
      if (handleModifications())
      {
         setVisible( false )
         dispose()
      }
   }
   override fun windowActivated (e : WindowEvent) {}
   override fun windowClosed (e : WindowEvent) {}
   override fun windowDeactivated (e : WindowEvent) {}
   override fun windowDeiconified (e : WindowEvent) {}
   override fun windowIconified (e : WindowEvent) {}
   override fun windowOpened (e : WindowEvent) {}
}
