package tedit.utils

import java.io.File
import java.io.FilenameFilter
import java.util.Vector
import pen.eco.KProduct
import pen.eco.KProductInfo
import pen.eco.KQuantableProductInfo

/** Creates a vector with product information. */
fun vectorize (productInfo : KProductInfo) : Vector<Object>
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

/** Creates a two dimensional product vector from list. */
fun vectorize (products : List<KProduct>, productTree : KProductTree) : Vector<Vector<Object>>
{
   val ret = Vector<Vector<Object>>()

   for (product in products)
   {
      val productInfo = productTree.productInfo( product )
      ret.add(vectorize( productInfo ))
   }

   return ret
}
