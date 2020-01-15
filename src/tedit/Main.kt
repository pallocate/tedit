package tedit

import pen.Log
import pen.LogLevel
import pen.Constants
import pen.readObject

/** The main class. */
object Main
{
   val USERS_FILE = "dist${Constants.SLASH}users.json"
   val SETTINGS_FILE = "dist${Constants.SLASH}settings.json"

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

   private fun start ()
   {
      Log.info( "Welcome to Tender Edit!" )

      try
      {
         /* Tries to read settings. */
         val settingsObject = readObject<KSettings>( {KSettings.serializer()}, SETTINGS_FILE )
         if (settingsObject is KSettings)
            Ref.settings = settingsObject
         else
            Ref.settings = KSettings()

         /* Tries to read users. */
         val usersObject = readObject<KUsers>( {KUsers.serializer()}, USERS_FILE )
         if (usersObject is KUsers)
         {
            Ref.setUsers( usersObject )

            if (Ref.users().userMap.isEmpty())
               throw Exception( "no users found" )
            else
            {
               if (Ref.settings.defaultUser > 0L)
                  Ref.users().activate( Ref.settings.defaultUser )
               else
                  Ref.users().activate( Ref.users().userMap[0]!! )
            }
         }
         else
            testMode()

         /* Calls KTenderEdit to create the GUI. */
         KTenderEdit.tenderEdit()
      }
      catch (e : Exception)
      {
         Log.critical( "Critical exception, (${e.message})" )
         kotlin.system.exitProcess( 1 )
      }
   }


   private fun testMode ()
   {
      val users = KUsers()
      users.testMode()
      Ref.settings.defaultUser = 3L

      Ref.setUsers( users )
      Ref.users().activate( Ref.settings.defaultUser )
   }
}
