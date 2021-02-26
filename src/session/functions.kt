package tedit.session

import kotlinx.serialization.builtins.ListSerializer
import pen.Log
import pen.serializeToFile
import pen.deserializeFromFile
import pen.par.KUser
import tedit.Lang
import tedit.gui.KUserSelect
import tedit.utils.usersFile

/** From an id activates a user. If not found pops up a user selection dialog. */
fun activateUser (userId : Long) : KUser
{
   var ret = KUser.void()
   val users = loadUsers()
//      listOf( pen.tests.Patricia.user(), pen.tests.David.user() ); saveUsers( users )

   if (users.size > 0)
   {
      ret = users.find { userId == it.me.id } ?: KUser.void()
      if (ret.isVoid())
         ret = KUserSelect( users ).selectedUser()
   }
   else
      Log.error( "No users found!" )

   return ret
}

internal fun saveUsers (users : List<KUser>) =
   serializeToFile<List<KUser>>(users, usersFile.toString(), ListSerializer( KUser.serializer() ))

internal fun loadUsers () : List<KUser> =
   deserializeFromFile<List<KUser>>(usersFile.toString(), ListSerializer( KUser.serializer() )) ?: ArrayList<KUser>()
