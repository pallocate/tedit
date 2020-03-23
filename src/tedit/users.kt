package tedit

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import pen.Log
import pen.Constants.SLASH
import pen.readObject
import pen.writeObject
import pen.par.KMe
import pen.par.KMember

interface User
class NoUser : User

/** A basic user of an application. */
@Serializable
class KUser : User
{
   var language                                        = "English"
   var member                                          = KMember(KMe( 0L ))

   override fun toString ()                            = "${member.me.name()}"
}

@Serializable
class KUsers ()
{
   companion object
   {
      private val FILE_NAME                            = "dist${SLASH}users.json"
//      val instance                                     = load()                            // Load instance from file
val instance = KUsers().apply {userArray = arrayOf(KUser().apply {member = pen.tests.ExampleMembers.patricia()}, KUser().apply {member = pen.tests.ExampleMembers.david()}); userArray.associateByTo( userMap, {it.member.me.contactId} )}

      fun load () : KUsers
      {
         var ret = KUsers()
         val obj = readObject<KUsers>({ serializer() }, FILE_NAME)

         if (obj is KUsers)
            ret = obj

         return ret
      }
   }

   @SerialName( "users" )
   var userArray : Array<KUser>                        = Array<KUser>( 0, {KUser()} )
   @Transient
   val userMap                                         = HashMap<Long,KUser>()
   @Transient
   var current                                         = KUser()

   init
   {userArray.associateByTo( userMap, {it.member.me.contactId} )}

   /** Activates a user (sets as current and sets language) */
   fun activate (userId : Long) : Boolean
   {
      var success = false

      val user = userMap.get( userId )
      if (user != null)
      {
         activate( user )
         success= true
      }
      else
         Log.error( "Bad user id: $userId" )

      return success
   }

   /** Activates a user (sets as current and sets language) */
   fun activate (user : KUser)
   {
      current = user
      Lang.setLanguage( user.language )
   }

   /** Saves users to file.*/
   fun save () = writeObject<KUsers>( this, {KUsers.serializer()}, FILE_NAME )
}
