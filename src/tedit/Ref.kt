package tedit

import java.awt.Color
import java.awt.Dimension
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.border.LineBorder
import pen.par.NoTender
import apps.KProductTree

/** Some miscellany references */
object Ref
{
   var settings                                        = KSettings()
   val summary                                         = KSummary()
   val productTree                                     = KProductTree( KTreeSelectionHandler(), KMouseHandler() )
   val fileChooser                                     = JFileChooser(File( "." ))
   val hamburgerMenu                                   = KHamburgerMenu()
   var statusBar = object : JLabel( "  " + Lang.word( 5 ) )
   {
      override fun getBorder() = LineBorder( Color.white, 1 )
      override fun getPreferredSize() = Dimension( 1000, 16 )
   }

   private var _users : Users = NoUsers()
   fun setUsers (users : KUsers) { _users = users }
   fun users () : KUsers = if (_users is KUsers)
                              _users as KUsers
                           else
                           {
                              _users = KUsers()
                              _users as KUsers
                           }
}
