package tedit

import kotlinx.serialization.Serializable
import pen.Constants.SLASH
import pen.readObject
import pen.writeObject

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

   private var progress                                = "2020:1"
      get ()
      {
         var ret = "2020:1"

         if (field.length > 5 && field.length < 8)
         {
            if (field[4] == ':')
               ret = field
         }

         return ret
      }

   fun year () : Int
   {
      val y = progress.substring( 0..3 ).toIntOrNull()
      return   if (y == null)
                  2020
               else
                  y
   }

   fun iteration () : Int
   {
      val p = progress
      val i = p.substring( 5 until p.length ).toIntOrNull()
      return   if (i == null)
                  1
               else
                  i
   }

   fun progression () = year().toString() +  ":" + iteration()

   var toolbar                                         = true
   var defaultUser                                     = 0L

   /** Saves settings to file. */
   fun save () = writeObject<KSettings>(this, { serializer() }, FILE_NAME)
}
