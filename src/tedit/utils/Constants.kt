package tedit.utils

object Constants
{
   val SLASH                                      = pen.Constants.SLASH

   /** Directory where application resources are stored. */
   val RESOURCES_DIR                              = "build${SLASH}dist${SLASH}resources"
   /** Directory where application icons are stored. */
   val ICONS_DIR                                  = "${RESOURCES_DIR}${SLASH}icons"
   /** Directory where avatar objects are stored. */
   val HELP_DIR                                   = "${RESOURCES_DIR}${SLASH}help"
   /** Directory where language translations are stored. */
   val LANGUAGE_DIR                               = "${RESOURCES_DIR}${SLASH}language"

   /** Directory where user data is stored. */
   val USERS_DIR                                  = "users"
   /** File where the users are stored. */
   val USERS_FILE                                 = "${USERS_DIR}${SLASH}users.json"
   val SETTINGS_FILE                              = "settings.json"
}
