package tedit.session

object Session
{
   internal val settings = KSettings.load()
   internal val documents = KDocuments()
   internal val users = 
      exampleUsers()
      //KUsers.load()

   internal fun start ()
   {
      users.activate( settings.defaultUser )
   }

   private fun exampleUsers () = KUsers(arrayListOf( pen.tests.Patricia.user(), pen.tests.David.user() ))
}
