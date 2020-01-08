package tedit

import java.awt.Color
import java.awt.Dimension
import java.io.File
import javax.swing.JTabbedPane
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.border.LineBorder
import pen.par.NoTender
import apps.KProductTree

object Ref
{
   var settings                                        = KSettings()
   var currentTab                                      = KTableTab( NoTender() )
   val summary                                         = KSummary()
   val productTree                                     = KProductTree( KTreeSelectionHandler(), KMouseHandler() )
   val tabbedPane                                      = JTabbedPane()
   val fileChooser                                     = JFileChooser(File( "." ))
   val hamburgerMenu                                   = KHamburgerMenu()
   var statusBar = object : JLabel( "  " + word( 5 ) )
   {
      override fun getBorder() = LineBorder( Color.white, 1 )
      override fun getPreferredSize() = Dimension( 1000, 16 )
   }
   private var proposalEditor : ProposalEditor         = NoProposalEditor()
   fun pe () : KProposalEditor = if (proposalEditor is KProposalEditor)
                                    proposalEditor as KProposalEditor
                                 else
                                 {
                                    proposalEditor = KProposalEditor()
                                    proposalEditor as KProposalEditor
                                 }

   var currentDictionary = Dictionary()

   var dictionaries = HashMap<String,Dictionary>().apply {
      put("English", currentDictionary)
   }

   fun setLanguage (language : String)
   {currentDictionary = dictionaries.getOrElse( language, {currentDictionary} )}

   fun word (num : Int) : String
   {
      var ret = ""

      if (currentDictionary != null)
         ret = currentDictionary.word( num )

      return ret
   }
}
