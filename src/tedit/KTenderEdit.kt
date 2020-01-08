package tedit

import java.awt.Dimension
import java.awt.BorderLayout
import java.awt.event.WindowListener
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.WindowConstants
import javax.swing.JSplitPane
import javax.swing.event.ChangeListener
import javax.swing.event.ChangeEvent
import javax.swing.filechooser.FileNameExtensionFilter
import pen.par.KMember
import pen.par.KMutableTender
import pen.eco.Target
import apps.Constants
import pen.Constants.SLASH

interface ProposalEditor
class NoProposalEditor : ProposalEditor
/** Creates the content, and contains references to some key components. */
class KProposalEditor () : JFrame(), ProposalEditor, WindowListener, ChangeListener
{
   init
   {
      val contentPane = getContentPane()
      val progressPath = Constants.USERS_DIR + SLASH + Ref.settings.currentUser().member.me.name + SLASH + Ref.settings.progress

      /** Split panes */
      val topSplitPane = JSplitPane().apply {
         setDividerSize( 6 )
         setDividerLocation( 450 )
         setLeftComponent( Ref.tabbedPane )
         setRightComponent( Ref.summary )
      }

      val splitpane = JSplitPane().apply {
         setDividerSize( 4 )
         setDividerLocation( 275 )
         setLeftComponent(JScrollPane( Ref.productTree ))
         setRightComponent( topSplitPane )
      }

      /** Other stuff */
      Ref.summary.load( "${progressPath}${SLASH}index.html" )
      setJMenuBar( KMenu() )
      contentPane.add(Ref.statusBar, BorderLayout.SOUTH)
      with( Ref.fileChooser ) {
         resetChoosableFileFilters()
         setFileFilter(FileNameExtensionFilter( "Economic tender", "tdr" ))
         setAcceptAllFileFilterUsed( true )
      }

      Ref.tabbedPane.addChangeListener( this )
      setTitle( Ref.word( 301 ) + " - " + Ref.word( 3 ) )

      /** Toolbar */
      if (Ref.settings.toolbar)
         contentPane.add(KToolbar( Ref.hamburgerMenu.hamburgerButton ), BorderLayout.NORTH)

      contentPane.add( splitpane, BorderLayout.CENTER )
      addWindowListener( this )

      setSize( Dimension( 1360, 768 ) )
      setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE )
      setVisible( true )
   }

   /** Responds to tab selection changes. Sets Ref.currentTab and application title accordingly. */
   override fun stateChanged (e : ChangeEvent)
   {
      val selectedTab = Ref.tabbedPane.getSelectedComponent()
      if (selectedTab is KTableTab)
      {
         val member = Ref.settings.currentUser().member
         if (member is KMember)
         {
            val tender = selectedTab.tender
            if (tender is KMutableTender)
            {
               val target = tender.relation.target
               if (target != Target.UNDEFINED)
               {
                  val progressPath = Constants.USERS_DIR + SLASH + member.me.name + SLASH + Ref.settings.progress
                  Ref.productTree.load( progressPath + SLASH + target.name.toLowerCase() + ".xml" )
               }
            }
         }
         Ref.currentTab = selectedTab
         Ref.currentTab.updateTitle()
      }
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
