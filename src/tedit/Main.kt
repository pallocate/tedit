package tedit

import java.io.File
import pen.Log
import pen.LogLevel
import pen.Constants
import pen.readObject

/** The main class. */
object Main
{
   val CONFIG_FILE = "dist${Constants.SLASH}tedit.json"

   @JvmStatic
   fun main (args : Array<String>)
   {
      if (args.size > 0)
         when (args[0])
         {
            "-d" ->
            {
               Log.level = LogLevel.DEBUG
               start()
            }

            "-q" ->
            {
               Log.level = LogLevel.QUIET
               start()
            }

            else ->
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

   fun start ()
   {
      Log.info( "Welcome to Tender Edit!" )

      try
      {
         val obj = readObject<KSettings>( {KSettings.serializer()}, CONFIG_FILE )
         if (obj is KSettings)
            Ref.settings = obj
         else
            Ref.settings = KSettings()

         Ref.pe()
      }
      catch (e : Exception)
      {
         Log.critical( "Unexpected exception, " + e.message )
         Log.critical( "Quiting.." )
         kotlin.system.exitProcess( 1 )
      }
   }
}
