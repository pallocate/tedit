package tedit.utils

import java.awt.GridBagConstraints

/** Sets up constraints for the components to be added to a GridBagLayout. */
class KConstraints
{
   private var xOrder = 0
   private var yOrder = 0

   fun next (weight : Double = 0.1, remainder : Boolean = false) : GridBagConstraints
   {
      val ret = GridBagConstraints().apply {
         fill = GridBagConstraints.HORIZONTAL
         weightx = weight
         gridx = xOrder
         gridy = yOrder
      }
      xOrder += 1

      if (remainder)
      {
        ret.fill = GridBagConstraints.REMAINDER
        yOrder += 1
        xOrder = 0
      }
      else
         ret.fill = GridBagConstraints.HORIZONTAL

      return ret
   }
}
