package apps

object Constants
{
   val SLASH                                      = pen.Constants.SLASH
   val JSON_EXTENSION                             = pen.Constants.JSON_EXTENSION
   /** Directory where recources are stored. */
   val RESOURCES_DIR                              = "dist${SLASH}resources"
   /** Directory where avatar objects are stored. */
   val PARTICIPANTS_DIR                           = "${RESOURCES_DIR}${SLASH}participants"
   /** Directory where language translations are stored. */
   val LANGUAGE_DIR                               = "${RESOURCES_DIR}${SLASH}language"
   /** Directory where application icons are stored. */
   val ICONS_DIR                                  = pen.Constants.ICONS_DIR

   /** Directory where proposals are stored. */
   val USERS_DIR                                  = "users"
   /** Directory where product information files are stored. */
   val PRODUCTS_DIR                               = "products"

}
