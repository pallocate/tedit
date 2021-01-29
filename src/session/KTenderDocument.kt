package tedit.session

import pen.Log
import pen.eco.KProposal
import pen.par.KTender
import pen.par.KRelation
import pen.par.KUser
import tedit.utils.KPasswordPopup
import tedit.Lang
import tedit.gui.KProposalTable

/** Hello! */
class KTenderDocument (
   internal var proposal : KProposal = KProposal.void(),
   internal var relation : KRelation = KRelation.void(),
   internal var user : KUser         = KUser.void()
)
{
   internal var pathname = Lang.word( 3 )
   internal val proposalTable = KProposalTable( this )

   internal fun load (pathname : String)
   {
      val tender = KTender.read( pathname )

      this.pathname = pathname
      proposal = tender.proposal
      user = Session.users.activate( tender.proposer )

      relation = user.findRelation( tender.conceder )
   }

   internal fun save (encrypt : Boolean = false) : Boolean
   {
      var success = false
      val tender = KTender( proposal, relation.other.id, user.me.id )

      try
      {
         if (encrypt)
         {
            val passwordPopup = KPasswordPopup()
            val othersPublicKey = ByteArray( 0 )
            val encryptor = user.me.crypto( passwordPopup, othersPublicKey )

            tender.write( pathname, encryptor )
         }
         else
            tender.write( pathname )

         success = true
      }
      catch (e : Exception)
      { Log.warn( e.message ?: "Save document failed!" ) }

      return success
   }
}
