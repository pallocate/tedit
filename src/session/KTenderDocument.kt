package tedit.session

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import pen.Log
import pen.slash
import pen.eco.KProductQuantities
import pen.par.*
import tedit.utils.KPasswordPopup
import tedit.Lang
import tedit.gui.KProposalTable

class KTenderDocument (internal var productQuantities : KProductQuantities, internal var relation : KRelation)
{
   companion object
   {fun void () = KTenderDocument( KProductQuantities.void(), KRelation.void() )}

   internal var filename = Lang.word( 3 )
   internal val proposalTable = KProposalTable( this )

   fun load (filename : String) : Long
   {
      val tender = KTender.read( filename )

      this.filename = filename
      productQuantities = tender.productQuantities
      relation = Session.user.findRelation( tender.conceder )

      return tender.proposer
   }

   fun save () : Boolean
   {
      var success = false
      try
      {
         toTender().write( filename )
         success = true
      }
      catch (e : Exception)
      { Log.warn( e.message ?: "Save document failed!" ) }

      return success
   }

   fun loadEncrypted (filename : String)
   {
      val input = DataInputStream(FileInputStream( filename ))
      val crypto = Session.user.me.crypto( KPasswordPopup() )

      try
      {
         val conceder = input.readLong()
         val proposer = input.readLong()
         val encryptedProposal = ByteArray( input.available() ).also {input.read( it )}

         val encodedProposal = crypto.decrypt( encryptedProposal )
         productQuantities = ProposalDecoder.decode( encodedProposal )

         relation = Session.user.findRelation( conceder )
         this.filename = filename
      }
      catch (e : Exception)
      { Log.warn( e.message ?: "Load encrypted failed!" ) }

   }


   fun saveEncrypted (filename : String) : Boolean
   {
      var success = false
      val crypto = Session.user.me.crypto( KPasswordPopup() )

      try
      {
         val encodedProposal = ProposalEncoder.encode( productQuantities )
         val encryptedProposal = crypto.encrypt( encodedProposal )

         val output = DataOutputStream(FileOutputStream( filename ))
         output.writeLong( toTender().conceder )
         output.writeLong( toTender().proposer )
         output.write( encryptedProposal )
         success = true
      }
      catch (e : Exception)
      { Log.warn( e.message ?: "Save encrypted failed!" ) }

      return success
   }

   fun filenameExcludePath () : String
   {
      var ret = Lang.word( 3 )
      try
      { ret = filename.drop(filename.lastIndexOf( slash() ) + 1) }
      catch (e : Exception) {}

      return ret
   }

   fun updateFromModel ()
   {
      productQuantities.products.clear()
      val productRows = proposalTable.proposalTableModel.productRows
      productQuantities.products.putAll( productRows.map { it.productQuantity.toPair() } )
   }

   fun toTender () = KTender( productQuantities, relation.other.id, Session.user.me.contact.id )
   fun isFilenameSet () = (filename != Lang.word( 3 ))
   fun isVoid () = productQuantities.isVoid() && relation.isVoid()
   fun isModified () = proposalTable.modified
}
