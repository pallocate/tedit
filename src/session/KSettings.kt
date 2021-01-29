package tedit.session

import kotlinx.serialization.Serializable
import pen.deserializeFromFile
import pen.serializeToFile
import pen.toInt
import tedit.utils.Constants.SETTINGS_FILE

/** Application settings. */
@Serializable
class KSettings ()
{
   companion object
   {
      internal fun load () : KSettings
      {
         var ret = KSettings()
         val obj = deserializeFromFile<KSettings>( SETTINGS_FILE, serializer() )

         if (obj is KSettings)
            ret = obj

         return ret
      }
   }

   private val progress                                        = "2021:1"
   val toolbar                                         = true
   val defaultUser                                     = 0L

   fun progression () : String
   {
      var ret = "2021:1"

      if (progress.length > 5 && progress.length < 8)
         if (progress.replaceFirst( ":", "." ).toFloatOrNull() != null)
            ret = progress

      return ret
   }
   fun iteration () = progression().substringAfter( ':' ).toInt()
   fun year () = progression().substringBefore( ':' ).toInt()

   /** Saves settings to file. */
   internal fun save () = serializeToFile<KSettings>( this, SETTINGS_FILE, serializer() )
}
