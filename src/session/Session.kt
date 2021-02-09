package tedit.session

import pen.par.KUser

object Session
{
   internal val settings = KSettings.load()
   internal val documents = KDocuments()
   internal var user = KUser.void()
}
