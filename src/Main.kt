package tedit

import pen.Log
import pen.LogLevel
import pen.toLong
import tedit.gui.GUI
import tedit.session.activateUser
import tedit.session.Session

/** The main class. */
object Main
{
   /** Handles command line arguments, then starts the application. */
   @JvmStatic
   fun main (args : Array<String>)
   {
      val userId = if (args.size == 0)
         0L
      else
      {
         for (arg in args)
         {
            when (arg)
            {
               "-d" -> Log.level = LogLevel.DEBUG
               "-q" -> Log.level = LogLevel.QUIET
               "-h" -> printUsageAndExit()
               else -> {}
            }
         }

         args.last().toLong( 0L )
      }

      val user = activateUser( userId )
      if (!user.isVoid())
      {
         Lang.activate( user.language )
         Session.user = user
         startGUI()
      }
   }

   private fun startGUI ()
   {
      Log.info( "Starting Tender Edit" )
      try
      { GUI.start() }
      catch (t : Throwable)
      {
         Log.critical( "Critical exception, (${t.message})" )
         kotlin.system.exitProcess( 1 )
      }
   }

   private fun printUsageAndExit ()
   {
      println( "Usage: tedit [OPTION]... [USER_ID]\n" )
      println( "Options are:" )
      println( "  -d     Debug" )
      println( "  -q     Quiet" )
      println( "  -h     Show this help" )
      kotlin.system.exitProcess( 0 )
   }
}
