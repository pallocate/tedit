package tedit.session

class KDocuments
{
   internal var activeDocument                                  = KTenderDocument()
   internal val documentList                                    = ArrayList<KTenderDocument>()

   internal fun activate ( tenderDocument : KTenderDocument )
   {
      activeDocument = tenderDocument
   }

   /** @return A list of unsaved documents. */
   internal fun unsaved () = documentList.filter { it.proposalTable.modified }
}
