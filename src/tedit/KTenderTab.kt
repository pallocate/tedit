package tedit

import javax.swing.JScrollPane
import kotlin.properties.Delegates
import pen.readObject
import pen.writeObject
import pen.PasswordProvider
import pen.eco.KMutableProposal
import pen.par.KMe
import pen.par.Tender
import pen.par.NoTender
import pen.par.KMutableTender
import pen.Constants.SLASH
import apps.KPasswordPopup

class KTenderTab (tender : Tender = NoTender()) : JScrollPane()
{
   var filename                                        = Lang.word( 3 )
   val proposalTable                                   = KProposalTable()
   var tender : Tender                                 = NoTender()
      set( t : Tender )
      {
         proposalTable.updateFromTender( t )
         field = t
      }

   init
   {
      this.tender = tender
      setViewportView( proposalTable )
   }

   fun load (filename : String)
   {
      val obj = readObject<KMutableTender>( {KMutableTender.serializer()}, filename )

      if (obj is KMutableTender)
      {
         tender = obj
         this.filename = filename
      }
   }

   fun save () = writeObject<KMutableTender>( tender as KMutableTender, {KMutableTender.serializer()}, filename )

   fun submit ()
   {
      val passwordPopup = KPasswordPopup( true )
      val me = KUsers.instance.current.member.me

      if (tender is KMutableTender)
         (tender as KMutableTender).toKTender().submit( me, passwordPopup )
   }
}
