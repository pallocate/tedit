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
      val obj = KTender.read( pathname )

      if (obj is KTender)
      {
         if (obj.proposer == user.me.id)
         {
            this.pathname = pathname
            proposal = obj.proposal
            relation = user.findRelation( obj.conceder )
         }
      }
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
