package tedit

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import pen.Log
import pen.writeObject
import pen.par.KMember

/** A basic user of an application. */
@Serializable
class KUser
{
   var language                                        = "English"
   var member                                          = KMember()
}

interface Users
class NoUsers : Users

@Serializable
class KUsers () : Users
{
   @SerialName( "users" )
   private var userArray : Array<KUser>                = Array<KUser>( 0, {KUser()} )
   @Transient
   val userMap                                         = HashMap<Long,KUser>()
   @Transient
   var current                                         = KUser()

   init
   {userArray.associateByTo( userMap, {it.member.me.contactId} )}

   /** Activates a user (sets as current and sets language) */
   fun activate (userId : Long)
   {
      if (userId > 0)
      {
         val user = userMap.get( userId )
         if (user != null)
         {
            current = user
            Lang.setLanguage( user.language )
         }
      }
      else
         Log.error( "Bad user id: $userId" )
   }

   /** Saves users to file.*/
   fun save () = writeObject<KUsers>( this, {KUsers.serializer()}, Main.USERS_FILE )



   fun testMode ()
   {
      userArray = arrayOf(
         KUser().apply {
            member = pen.tests.Examples.Participants.alice()
         },
         KUser().apply {
            member = pen.tests.Examples.Participants.bob()
         })

      userArray.associateByTo( userMap, {it.member.me.contactId} )
   }
}
