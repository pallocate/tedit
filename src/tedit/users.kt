package tedit

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import pen.Log
import pen.writeObject
import pen.par.KMember

interface User
class NoUser : User

/** A basic user of an application. */
@Serializable
class KUser : User
{
   var language                                        = "English"
   var member                                          = KMember()

   override fun toString () = "${member.me.name}"
}

interface Users
class NoUsers : Users

@Serializable
class KUsers () : Users
{
   @SerialName( "users" )
   var userArray : Array<KUser>                        = Array<KUser>( 0, {KUser()} )
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
            activate( user )
      }
      else
         Log.error( "Bad user id: $userId" )
   }

   /** Activates a user (sets as current and sets language) */
   fun activate (user : KUser)
   {
      if (user is KUser)
      {
         current = user
         Lang.setLanguage( user.language )
      }
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
