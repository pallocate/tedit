package tedit.session

class KDocuments
{
   internal var activeDocument = KTenderDocument.void()
   internal val documentList = ArrayList<KTenderDocument>()

   internal fun activate ( tenderDocument : KTenderDocument )
   {
      activeDocument = tenderDocument
   }

   /** @return A list of unsaved documents. */
   internal fun unsaved () = documentList.filter { it.proposalTable.modified }

   /** Searches document list for a document with a matching pathname. */
   internal fun findOpen (pathname : String) : KTenderDocument
   {
      var ret = KTenderDocument.void()
      val result = documentList.find { it.pathname == pathname }

      if (result is KTenderDocument)
         ret = result

      return ret
   }
}
