@file:kotlinx.serialization.UseSerializers( pen.ByteArraySerialiser::class )

package tedit.session

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import pen.Log
import pen.deserializeFromFile
import pen.serializeToFile
import pen.Constants.SLASH
import pen.par.*
import tedit.utils.Constants.USERS_DIR
import tedit.utils.Constants.USERS_FILE
import tedit.Lang

@Serializable
class KUsers (@SerialName( "users" ) internal val userList : ArrayList<KUser> = ArrayList<KUser>())
{
   companion object
   {internal fun load () : KUsers = deserializeFromFile<KUsers>( USERS_FILE, serializer() ) ?: KUsers()}

   @Transient
   internal var activeUser = KUser.void()

   /** Activates a user (sets activeUser and language). */
   internal fun activate (userId : Long) : KUser
   {
      val user = userList.find { user -> userId == user.me.id }

      if (user == null || user.isVoid())
         Log.error( "User with id $userId was not found!" )
      else
      {
         activeUser = user
         Lang.activate( user.language )
      }

      return user ?: KUser.void()
   }

   /** Saves users to file.*/
   internal fun save () = serializeToFile<KUsers>( this, USERS_FILE, KUsers.serializer() )
}
