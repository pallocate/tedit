package tedit.session

object Session
{
   internal val settings = KSettings.load()
   internal val documents = KDocuments()
   internal val users = //KUsers.load()
               testUsers()

   internal fun start ()
   {
      users.activate( settings.defaultUser )
   }

   private fun testUsers () = KUsers(arrayListOf( pen.tests.Patricia.user(), pen.tests.David.user() ))
}
