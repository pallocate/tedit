package tedit

import java.io.FileReader
import java.util.Properties
import pen.Log
import apps.Constants

class Dictionary (val language : String = "English")
{
   private val properties = Properties()

   /** Loads mappings from file. */
   init
   {
      Log.debug( "Loading $language dictionary" )

      try
      { properties.load(FileReader( Constants.LANGUAGE_DIR + Constants.SLASH + language )) }
      catch (e : Exception)
      { Log.warn( "Loading dictionary failed!" ) }
   }

   fun word (num : Int) : String
   {
      val key = num.toString()
      return properties.getProperty( key, key )
   }

   fun isLoaded () = (properties.size > 0)
}

