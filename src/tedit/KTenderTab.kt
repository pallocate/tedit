package tedit

import javax.swing.JScrollPane
import pen.readObject
import pen.writeObject
import pen.PasswordProvider
import pen.eco.KMutableProposal
import pen.par.KMe
import pen.par.Tender
import pen.par.NoTender
import pen.par.KMutableTender
import pen.Constants.SLASH

class KTenderTab (var tender : Tender = NoTender()) : JScrollPane()
{
   var filename                                        = Lang.word( 3 )
   val proposalTable                                   = KProposalTable()

   init
   {
      if (tender.proposal is KMutableProposal)
         proposalTable.proposal = tender.proposal as KMutableProposal

      setViewportView( proposalTable )
   }

   fun load (filename : String)
   {
      val obj = readObject<KMutableTender>( {KMutableTender.serializer()}, filename )

      if (obj is KMutableTender)
      {
         tender = obj
         proposalTable.proposal = tender.proposal as KMutableProposal
      }
      this.filename = filename
   }

   fun save () = writeObject<KMutableTender>( tender as KMutableTender, {KMutableTender.serializer()}, filename )

   fun submit (me : KMe, passwordProvider : PasswordProvider)
   {
      if (tender is KMutableTender)
         (tender as KMutableTender).toKTender().submit( me, passwordProvider )
   }
}
