package world.behemoth.world;

import it.gotoandplay.smartfoxserver.data.User;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Parties {
   private AtomicInteger partyId = new AtomicInteger();
   private Map<Integer, PartyInfo> parties = new HashMap();

   public Parties() {
      super();
   }

   public PartyInfo getPartyInfo(int partyId) {
      return (PartyInfo)this.parties.get(Integer.valueOf(partyId));
   }

   public int size() {
      return this.parties.size();
   }

   public void removeParty(int partyId) {
      this.parties.remove(Integer.valueOf(partyId));
   }

   public int getPartyId(User owner) {
      int pid = this.partyId.incrementAndGet();
      this.parties.put(Integer.valueOf(pid), new PartyInfo(owner, pid));
      return pid;
   }
}
