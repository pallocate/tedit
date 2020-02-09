package apps

import java.io.File
import java.io.FilenameFilter
import java.util.Vector
import pen.eco.Product
import pen.eco.ProductInfo
import pen.eco.KQuantableProductInfo

/** Creates two dimensional product Vectors for use in JTable. */
object Utils
{
   /** Returns a directory listing.
     * @param extension File name extension. */
   fun listDir (directory : String, extension : String) : Array<String>
   {
      var ret = Array<String>( 0, {""} )
      try
      {
         val dir = File( directory )
         if (dir.exists() && dir.isDirectory())
            ret = dir.list(
               object : FilenameFilter
               {
                  override fun accept (dir : File, name : String) : Boolean = name.endsWith( ".${extension}" )
               }
            )
      }
      catch (e : Exception) {}

      return ret
   }

   fun vectorize (productInfo : ProductInfo) : Vector<Object>
   {
      val ret = Vector<Object>()
      ret.add( productInfo.id as Object )

      val apu = productInfo.apu()
      ret.add( (productInfo.toString() +  if (apu == "")
                                             ""
                                          else
                                             ", " + apu
      ) as Object )

      ret.add( productInfo.price.toString() as Object )
      ret.add( if (productInfo is KQuantableProductInfo)
                  productInfo.qty.toString() as Object
               else
                  "0" as Object )

      return ret
   }

   /** Using list of quantable products as input. */
   fun vectorize (products : List<Product>, productTree : KProductTree) : Vector<Vector<Object>>
   {
      val ret = Vector<Vector<Object>>()

      for (product in products)
      {
         val productInfo = productTree.productInfo( product )
         ret.add(vectorize( productInfo ))
      }

      return ret
   }
}
