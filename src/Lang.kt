package tedit

import java.nio.file.Files
import java.util.Properties
import pen.Log
import tedit.utils.languageDir

/** Maps words and phrases using java.util.Properties read from file. */
class Dictionary (val language : String)
{
   private val properties = Properties()

   init
   {
      Log.debug( "Loading $language dictionary" )

      try
      {properties.load( Files.newBufferedReader(languageDir.resolve( language )) )}
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
   private var activeDictionary = Dictionary( DEFAULT_LANGUGE )
   private var dictionaries = HashMap<String,Dictionary>().apply {
      put( DEFAULT_LANGUGE, activeDictionary )
   }

   fun activate (language : String)
   {
      if (dictionaries.containsKey( language ))
         activeDictionary = dictionaries.getOrElse( language, {activeDictionary} )
      else
      {
         val dictionary = Dictionary( language )
         if (!dictionary.isEmpty())
         {
            dictionaries.put( language, dictionary )
            activeDictionary = dictionary
         }
      }
   }

   fun word (num : Int) = activeDictionary.lookUp( num )
}
