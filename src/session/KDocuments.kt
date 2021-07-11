package tedit.session

class KDocuments
{
   internal var activeDocument = KTenderDocument.void()
   val documentList = ArrayList<KTenderDocument>()

   fun activate ( tenderDocument : KTenderDocument )
   {
      activeDocument = tenderDocument
   }

   /** @return A list of unsaved documents. */
   fun unsaved () = documentList.filter { it.proposalTable.modified }

   /** Searches document list for a document with a matching filename. */
   fun findOpen (filename : String) : KTenderDocument
   {
      var ret = KTenderDocument.void()
      val result = documentList.find { it.filename == filename }

      if (result is KTenderDocument)
         ret = result

      return ret
   }
}
