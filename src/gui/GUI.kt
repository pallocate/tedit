package tedit.gui

import java.awt.Color
import java.awt.Dimension
import java.io.File
import javax.swing.JLabel
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.border.LineBorder
import tedit.utils.KProductTree
import tedit.FileCooserMode
import tedit.Lang

/** GUI starting point as well as some miscellany internally used references. */
object GUI
{
   internal val frame by lazy { KFrame() }
   internal val tabs                                            = KTabs()
   internal val productTree                                     = KProductTree( KTreeSelectionHandler(), KMouseHandler() )
   internal val infoPane                                            = KInfoPane()
   internal val hamburgerMenu                                   = KHamburgerMenu()
   private val _fileChooser                                     = JFileChooser(File(System.getProperty( "user.dir" )))

   internal fun fileChooser (mode : FileCooserMode) = _fileChooser.apply {

      resetChoosableFileFilters()
      when (mode)
      {
         FileCooserMode.OPEN ->
         {
            dialogTitle = Lang.word( 17 )
            approveButtonText = Lang.word( 17 )
            fileFilter = FileNameExtensionFilter( "Tender document", "tdr" )
         }

         FileCooserMode.SAVE_AS ->
         {
            dialogTitle = Lang.word( 19 )
            approveButtonText = Lang.word( 18 )
            approveButtonToolTipText = Lang.word( 18 ) + " " + Lang.word( 50 )
            fileFilter = FileNameExtensionFilter( "Tender document", "tdr" )
         }

         FileCooserMode.EXPORT ->
         {
            dialogTitle = Lang.word( 304 )
            approveButtonText = Lang.word( 304 )
            approveButtonToolTipText = Lang.word( 304 ) + " " + Lang.word( 50 )
            fileFilter = FileNameExtensionFilter( "Encrypted tender", "etr" )
         }

      }

      selectedFile = File(System.getProperty( "user.dir" ))
      setAcceptAllFileFilterUsed( true )
   }

   internal val statusBar = object : JLabel( "  " + Lang.word( 5 ) )
   {
      override fun getBorder() = LineBorder( Color.white, 1 )
      override fun getPreferredSize() = Dimension( 1000, 16 )
   }

   internal fun start () = frame
}
