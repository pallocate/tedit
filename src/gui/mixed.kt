package tedit.gui

import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.event.TreeSelectionEvent
import javax.swing.SwingUtilities
import javax.swing.JScrollPane
import pen.eco.KProduct_v1
import pen.eco.KProductQuantity
import tedit.EventHandler

/** Maps a product to a product quantity. */
data class KProductRow (val product : KProduct_v1, val productQuantity : KProductQuantity)

object VoidTab : JScrollPane()

/** Handles selections in the product tree. */
class KTreeSelectionHandler : TreeSelectionListener
{
   override fun valueChanged (e : TreeSelectionEvent)
   { EventHandler.handle( EventHandler.TREE_SELECTION ) }
}

/** Handles mouseclicks in the product tree. */
class KMouseHandler : MouseListener
{
   override fun mouseClicked (e : MouseEvent)
   {
      if (SwingUtilities.isLeftMouseButton( e ) && e.getClickCount() == 2)
         EventHandler.handle( EventHandler.ADD )
   }

   override fun mouseEntered (e : MouseEvent) {}
   override fun mouseExited (e : MouseEvent) {}
   override fun mousePressed (e : MouseEvent) {}
   override fun mouseReleased (e : MouseEvent) {}
}
