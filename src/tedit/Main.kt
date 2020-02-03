package tedit

import pen.Log
import pen.LogLevel

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
      Log.info( "Welcome to Tender Edit!" )
      try
      {
         val defaultUser = KSettings.instance.defaultUser

         if (KUsers.instance.userMap.isEmpty())
            throw Exception( "no users found" )
         else
            with( KUsers.instance )
            {
               if (defaultUser > 0L && !activate( defaultUser ))
                  activate( userArray[0] )
            }

         GUI.createInstance()
      }
      catch (e : Exception)
      {
         Log.critical( "Critical exception, (${e.message})" )
         kotlin.system.exitProcess( 1 )
      }
   }
}
