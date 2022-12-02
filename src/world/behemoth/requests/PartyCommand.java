package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.requests.party.PartyAccept;
import world.behemoth.requests.party.PartyAcceptSummon;
import world.behemoth.requests.party.PartyDecline;
import world.behemoth.requests.party.PartyDeclineSummon;
import world.behemoth.requests.party.PartyInvite;
import world.behemoth.requests.party.PartyKick;
import world.behemoth.requests.party.PartyLeave;
import world.behemoth.requests.party.PartyPromote;
import world.behemoth.requests.party.PartySummon;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class PartyCommand implements IRequest {
   public PartyCommand() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      if(params[0].equals("pi")) {
         (new PartyInvite()).process(params, user, world, room);
      } else if(params[0].equals("pk")) {
         (new PartyKick()).process(params, user, world, room);
      } else if(params[0].equals("pl")) {
         (new PartyLeave()).process(params, user, world, room);
      } else if(params[0].equals("ps")) {
         (new PartySummon()).process(params, user, world, room);
      } else if(params[0].equals("psa")) {
         (new PartyAcceptSummon()).process(params, user, world, room);
      } else if(params[0].equals("psd")) {
         (new PartyDeclineSummon()).process(params, user, world, room);
      } else if(params[0].equals("pp")) {
         (new PartyPromote()).process(params, user, world, room);
      } else if(params[0].equals("pa")) {
         (new PartyAccept()).process(params, user, world, room);
      } else if(params[0].equals("pd")) {
         (new PartyDecline()).process(params, user, world, room);
      }

   }
}
