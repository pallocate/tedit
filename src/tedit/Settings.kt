package tedit

import kotlinx.serialization.Serializable
import pen.Log
import pen.loadConf
import pen.writeObject
import apps.KUser
import apps.Constants

interface Settings
class NoSettings : Settings

@Serializable
class KSettings () : Settings
{
   var progress                                   = "2020:1"
   var toolbar                                    = true
   var defaultUser                                = 3L
   @Transient private var _currentUser            = KUser().apply {
      member = pen.tests.Examples.Participants.alice()
   }

   val users                                      = ArrayList<KUser>().apply {
      add( _currentUser )
      add( KUser().apply {
         member = pen.tests.Examples.Participants.bob()
      })
   }

   /** Loads persistent data and dictionary. */
   fun currentUser (userId : Long)
   {
      val user = users.find<KUser>( {it.member.me.contactId == userId} )

      if (user != null)
      {
         Ref.setLanguage( user.language )
         _currentUser = user
      }
   }

   fun currentUser () : KUser = _currentUser
   fun save () = writeObject<KSettings>( this, {KSettings.serializer()}, Main.CONFIG_FILE )
}


