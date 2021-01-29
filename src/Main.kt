package tedit

import pen.Log
import pen.LogLevel
import tedit.gui.GUI
import tedit.session.Session

/** The main class. */
object Main
{
   /** Handles command line arguments, then starts the application. */
   @JvmStatic
   fun main (args : Array<String>)
   {
      if (args.size > 0)
         when (args[0])
         {
            "-d" ->  // Debug
            {
               Log.level = LogLevel.DEBUG
               start()
            }

            "-q" ->  // Quiet
            {
               Log.level = LogLevel.QUIET
               start()
            }

            else ->  // Print usage
            {
               println( "tedit" )
               println( "tedit -d         Debug" )
               println( "tedit -q         Quiet" )
               println( "tedit -h         Show help" )
            }
         }
      else
          start()
   }

   /** Instanciates settings, users and GUI. */
   private fun start ()
   {
      Log.info( "Starting Tender Edit" )
      try
      {
         Session.start()
         GUI.start()
      }
      catch (t : Throwable)
      {
         Log.critical( "Critical exception, (${t.message})" )
         kotlin.system.exitProcess( 1 )
      }
   }
}
