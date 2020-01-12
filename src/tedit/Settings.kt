package tedit

import kotlinx.serialization.Serializable
import pen.writeObject

interface Settings
class NoSettings : Settings

@Serializable
class KSettings () : Settings
{
   var progress                                        = "2020:1"
   var toolbar                                         = true
   var defaultUser : Long                              = 0L

   fun save () = writeObject<KSettings>( this, {KSettings.serializer()}, Main.SETTINGS_FILE )
}
