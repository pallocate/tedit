package tedit.gui

import java.awt.Font
import java.io.File
import java.net.URL
import javax.swing.JEditorPane
import javax.swing.event.HyperlinkListener
import javax.swing.event.HyperlinkEvent
import pen.Log

/** Web browser pane for showing product info etc. */
class KInfoPane () : JEditorPane(), HyperlinkListener
{
   init
   {
      setEditorKit(JEditorPane.createEditorKitForContentType( "text/html" ))
      addHyperlinkListener( this )

      putClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES, true )
      setFont(Font( "SansSerif", Font.PLAIN, 13 ))      
      setEditable( false )
  }

   /** Loads file into info pane.
     * @return True if successful */
   internal fun load (infoFile : File) : Boolean
   {
      var success = false
      Log.debug( "Loading info page \"$infoFile\"" )

      if (infoFile.exists())
      {
         try
         {
            setPage( infoFile.toURL() )
            success = true
         }
         catch (e : Exception) { Log.warn( "Page load failed!" ) }
      }

      return success
   }

   /** Handles hyperlink events (clicking links). */
   override fun hyperlinkUpdate (e : HyperlinkEvent)
   {
      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
         setPage( e.getURL() )
   }
}
