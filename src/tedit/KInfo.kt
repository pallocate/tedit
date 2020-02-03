package tedit

import java.io.File
import java.net.URL
import javax.swing.JEditorPane
import javax.swing.event.HyperlinkListener
import javax.swing.event.HyperlinkEvent
import pen.Log

/** Web browser pane for showing product info etc. */
class KInfo () : JEditorPane(), HyperlinkListener
{
   init
   {
      setEditorKit(JEditorPane.createEditorKitForContentType( "text/html" ))
      setEditable( false )
      addHyperlinkListener( this )

  }

   /** Loads file into info pane.
     * @return True if successful */
   fun load (filename : String) : Boolean
   {
      var success = false
      Log.debug( "Loading info page \"$filename\"" )

      if ((File( filename )).exists())
      {
         try
         {
            setPage( URL(URL( "file:" ), filename) )
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