package apps

import javax.swing.JPasswordField
import javax.swing.JOptionPane
import pen.PasswordProvider

/** A graphical password input. */
class KPasswordPopup (val retain : Boolean = false) : PasswordProvider
{
   private var enteredPassword = ""

   override fun password () : String
   {
      var ret = ""

      if (retain)
         if (enteredPassword == "")
            ret = showDialog()
         else
            ret = enteredPassword
      else
         ret = showDialog()

      return ret
   }

   private fun showDialog () : String
   {
      var ret = ""

      val passwordField = JPasswordField()
      val result = JOptionPane.showConfirmDialog( null, passwordField, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE )

      if (result == 0)
          ret = String( passwordField.getPassword() )

      return ret
   }
}
