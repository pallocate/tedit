package tedit

import kotlinx.serialization.Serializable
import pen.Constants.SLASH
import pen.readObject
import pen.writeObject
import pen.toInt

/** Application settings. */
@Serializable
class KSettings ()
{
   companion object
   {
      private val FILE_NAME                            = "dist${SLASH}settings.json"
      val instance                                     = load()

      private fun load () : KSettings
      {
         var ret = KSettings()
         val obj = readObject<KSettings>({ serializer() }, FILE_NAME)

         if (obj is KSettings)
            ret = obj

         return ret
      }
   }

   var progress                                        = "2020:1"
      /* Field access validation, since this object probably have been read from file. */
      get ()
      {
         var ret = "2020:1"

         if (field.length > 5 && field.length < 8)
            if (field.replaceFirst( ":", "." ).toFloatOrNull() != null)
               ret = field

         return ret
      }

   fun year () = progress.substringBefore( ':' ).toInt()
   fun iteration () = progress.substringAfter( ':' ).toInt()

   var toolbar                                         = true
   var defaultUser                                     = 0L

   /** Saves settings to file. */
   fun save () = writeObject<KSettings>(this, { serializer() }, FILE_NAME)
}
