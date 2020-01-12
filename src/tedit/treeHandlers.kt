package tedit

import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.event.TreeSelectionEvent
import javax.swing.SwingUtilities

class KTreeSelectionHandler : TreeSelectionListener
{
   override fun valueChanged (e : TreeSelectionEvent)
   { EventHandler.handle( EventHandler.TREE_SELECTION ) }
}

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

