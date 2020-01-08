package apps

import java.io.ByteArrayInputStream
import java.io.FileInputStream
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import javax.swing.tree.DefaultMutableTreeNode
import java.util.Stack
import pen.eco.KProductInfo
import pen.Log

/** Builds a tree structure of products from an XML file. */
object ProductTreeParser
{
   /** @return Top of the product tree. */
   fun parse (filename : String) : DefaultMutableTreeNode?
   {
      var fis : FileInputStream? = null
      var ret : DefaultMutableTreeNode? = null
      Log.debug( "Parsing XML file \"$filename\"" )

      try
      {
         fis = FileInputStream( filename )
         val saxParser : SAXParser = SAXParserFactory.newInstance().newSAXParser()
         val handler = object : DefaultHandler()
         {
            val stack = Stack<DefaultMutableTreeNode>()

            override fun startElement (uri : String, localName : String, qName : String, attr : Attributes)
            {
               if (qName == "product")
               {
                  val dmtn : DefaultMutableTreeNode = DefaultMutableTreeNode()
                  var product = KProductInfo( attr.getValue( "id" ).orEmpty(), attr.getValue( "name" ).orEmpty(),
                  attr.getValue( "desc" ).orEmpty(), attr.getValue( "amount" ).orEmpty(), attr.getValue( "prefix" ).orEmpty(),
                  attr.getValue( "unit" ).orEmpty(), attr.getValue( "down" ).orEmpty(), attr.getValue( "up" ).orEmpty(),
                  attr.getValue( "absolute" ).orEmpty(), attr.getValue( "price" ).orEmpty(),
                  attr.getValue( "sensetive" ).orEmpty(), attr.getValue( "analogue" ).orEmpty() )

                  dmtn.setUserObject( product )

                  if (!stack.empty())
                     stack.peek().add( dmtn )

                  stack.push( dmtn )
               }
            }
            override fun endElement (uri : String, localName : String, qName : String)
            {
               if (qName == "product")
                  ret = stack.pop()
            }
         }

         saxParser.parse( fis, handler )
      }
      catch (t : Throwable)
      { Log.error( "Parse XML file failed! ${t.message}" ) }
      finally
      { fis?.close() }

      return ret
   }
}
