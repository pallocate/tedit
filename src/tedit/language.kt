package tedit

import java.io.FileReader
import java.util.Properties
import pen.Log
import apps.Constants

/** Maps words and phrases as java.util.Properties read from file. */
class Dictionary (val language : String)
{
   private val properties = Properties()

   init
   {
      Log.debug( "Loading $language dictionary" )

      try
      { properties.load(FileReader( Constants.LANGUAGE_DIR + Constants.SLASH + language )) }
      catch (e : Exception)
      { Log.warn( "Loading dictionary failed!" ) }
   }

   fun lookUp (num : Int) : String
   {
      val key = num.toString()
      return properties.getProperty( key, key )
   }

   fun isEmpty () = properties.isEmpty()
}

/** Dynamically loads dictionaries as needed. */
object Lang
{
   private val DEFAULT_LANGUGE = "English"
   private var currentDictionary = Dictionary( DEFAULT_LANGUGE )
   private var dictionaries = HashMap<String,Dictionary>().apply {
      put( DEFAULT_LANGUGE, currentDictionary )
   }

   fun setLanguage (language : String)
   {
      if (dictionaries.containsKey( language ))
         currentDictionary = dictionaries.getOrElse( language, {currentDictionary} )
      else
      {
         val dictionary = Dictionary( language )
         if (!dictionary.isEmpty())
         {
            dictionaries.put( language, dictionary )
            currentDictionary = dictionary
         }
      }
   }

   fun word (num : Int) = currentDictionary.lookUp( num )
}
