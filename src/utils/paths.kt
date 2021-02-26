package tedit.utils

import java.nio.file.Paths
import tedit.session.Session

val settingsFile = "settings.json"

/** Directory where user data is stored. */
val usersDir = Paths.get( "users" )
val usersFile = usersDir.resolve( "users.json" )

/** Directory where application resources are stored. */
val resourcesDir = Paths.get( "build", "dist", "resources" )
/** Directory where application icons are stored. */
val iconsDir = resourcesDir.resolve( "icons" )
/** Directory where help files are stored. */
val helpDir = resourcesDir.resolve( "help" )
/** Directory where language translations are stored. */
val languageDir = resourcesDir.resolve( "language" )

fun progressPath () = Paths.get( usersDir.toString(), Session.user.me.info.name, Session.settings.progression() )
