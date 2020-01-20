package tedit

import java.awt.Color
import java.awt.Dimension
import java.io.File
import javax.swing.JLabel
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.border.LineBorder
import apps.KProductTree

/** Some miscellany references */
object GUI
{
   val frame by lazy { KFrame() }
   val productTree                                     = KProductTree( KTreeSelectionHandler(), KMouseHandler() )
   val summary                                         = KSummary()
   val hamburgerMenu                                   = KHamburgerMenu()
   val fileChooser                                     = JFileChooser(File( "." )).apply {
      resetChoosableFileFilters()
      setFileFilter(FileNameExtensionFilter( "Economic tender", "tdr" ))
      setAcceptAllFileFilterUsed( true )
   }

   var statusBar = object : JLabel( "  " + Lang.word( 5 ) )
   {
      override fun getBorder() = LineBorder( Color.white, 1 )
      override fun getPreferredSize() = Dimension( 1000, 16 )
   }
}
