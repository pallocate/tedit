package tedit.session

import pen.Log
import pen.slash
import pen.eco.KProposal
import pen.par.KTender
import pen.par.KRelation
import pen.par.KUser
import tedit.utils.KPasswordPopup
import tedit.Lang
import tedit.gui.KProposalTable

class KTenderDocument (internal var proposal : KProposal, internal var relation : KRelation)
{
   companion object
   {fun void () = KTenderDocument( KProposal.void(), KRelation.void() )}

   internal var pathname = Lang.word( 3 )
   internal val proposalTable = KProposalTable( this )

   internal fun load (pathname : String) : Long
   {
      val tender = KTender.read( pathname )

      this.pathname = pathname
      proposal = tender.proposal
      relation = Session.user.findRelation( tender.conceder )

      return tender.proposer
   }

   internal fun saveEncrypted (filename : String)
   {
      val tender = KTender( proposal, relation.other.id, Session.user.me.contact.id )
      try
      {
         val passwordPopup = KPasswordPopup()
         val encryptor = Session.user.me.crypto( passwordPopup )

         tender.write( filename, encryptor )
      }
      catch (e : Exception)
      { Log.warn( e.message ?: "Export encrypted failed!" ) }
   }

   internal fun save () : Boolean
   {
      var success = false
      val tender = KTender( proposal, relation.other.id, Session.user.me.contact.id )

      try
      {
         tender.write( pathname )
         success = true
      }
      catch (e : Exception)
      { Log.warn( e.message ?: "Save document failed!" ) }

      return success
   }

   fun filename () : String
   {
      var ret = Lang.word( 3 )
      try
      { ret = pathname.drop(pathname.lastIndexOf( slash() ) + 1) }
      catch (e : Exception) {}

      return ret
   }

   fun isPathSet () = pathname != Lang.word( 3 )
   fun isVoid () = proposal.isVoid() && relation.isVoid()
   fun isModified () = proposalTable.modified
}
