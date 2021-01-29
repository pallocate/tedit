package tedit.gui

import java.awt.Color
import java.awt.Dimension
import java.io.File
import javax.swing.JLabel
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.border.LineBorder
import tedit.utils.KProductTree
import tedit.Lang

/** GUI starting point and some miscellany references */
object GUI
{
   internal val frame by lazy { KFrame() }
   internal val tabs                                            = KTabs()
   internal val productTree                                     = KProductTree( KTreeSelectionHandler(), KMouseHandler() )
   internal val info                                            = KInfo()
   internal val hamburgerMenu                                   = KHamburgerMenu()
   internal val fileChooser                                     = JFileChooser(File( "." )).apply {
      resetChoosableFileFilters()
      setFileFilter(FileNameExtensionFilter( "Economic tender", "tdr" ))
      setAcceptAllFileFilterUsed( true )
   }

   internal val statusBar = object : JLabel( "  " + Lang.word( 5 ) )
   {
      override fun getBorder() = LineBorder( Color.white, 1 )
      override fun getPreferredSize() = Dimension( 1000, 16 )
   }

   internal fun start () = frame
}
