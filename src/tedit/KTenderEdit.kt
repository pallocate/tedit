package tedit

import java.awt.Dimension
import java.awt.BorderLayout
import java.awt.event.WindowListener
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.WindowConstants
import javax.swing.JSplitPane
import pen.par.KMutableTender
import apps.Constants
import pen.Constants.SLASH

interface TenderEdit
class NoTenderEdit : TenderEdit

/** The main frame/GUI of the app. */
class KTenderEdit () : JFrame(), TenderEdit, WindowListener
{
   companion object
   {
      private var _tenderEdit : TenderEdit = NoTenderEdit()
      /** Returns a KTenderEdit, creating it if does not yet exist. */
      fun tenderEdit () : KTenderEdit =   if (_tenderEdit is KTenderEdit)
                                             _tenderEdit as KTenderEdit
                                          else
                                          {
                                             _tenderEdit = KTenderEdit()
                                             _tenderEdit as KTenderEdit
                                          }
   }

   init
   {
      val splitpane = JSplitPane().apply {
         setDividerSize( 4 )
         setDividerLocation( 275 )
         setLeftComponent(JScrollPane( Ref.productTree ))
         setRightComponent( JSplitPane().apply {
            setDividerSize( 6 )
            setDividerLocation( 450 )
            setLeftComponent( Tabs )
            setRightComponent( Ref.summary )
         })
      }

      with( getContentPane() ) {
         if (Ref.settings.toolbar)
            add(KToolbar( Ref.hamburgerMenu.hamburgerButton ), BorderLayout.NORTH)

         add( splitpane, BorderLayout.CENTER )
         add(Ref.statusBar, BorderLayout.SOUTH)
      }

      val START_PAGE = Constants.USERS_DIR + SLASH + Ref.users().current.member.me.name + SLASH + Ref.settings.progress + SLASH + "index.html"
      Ref.summary.load( START_PAGE )
      setJMenuBar( KMenu() )

      setTitle( Lang.word( 301 ) + " - " + Lang.word( 3 ) )
      setSize( Dimension( 1360, 768 ) )
      setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE )
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
