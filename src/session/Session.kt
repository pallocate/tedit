package tedit.session

import pen.par.KUser

object Session
{
   val settings = KSettings.load()
   val documents = KDocuments()
   var user = KUser.void()
}
