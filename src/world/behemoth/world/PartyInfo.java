package world.behemoth.world;

import it.gotoandplay.smartfoxserver.data.User;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.sf.json.JSONArray;

public class PartyInfo {
   private int id;
   private List<User> members = new ArrayList();
   private User owner;

   public PartyInfo(User owner, int id) {
      super();
      this.owner = owner;
      this.id = id;
      owner.properties.put("partyId", Integer.valueOf(this.id));
   }

   public JSONArray getUsers() {
      JSONArray partyMembers = new JSONArray();
      Iterator i$ = this.members.iterator();

      while(i$.hasNext()) {
         User u = (User)i$.next();
         partyMembers.add(u.properties.get("username"));
      }

      partyMembers.add(this.owner.properties.get("username"));
      return partyMembers;
   }

   public int getMemberCount() {
      return this.members.size();
   }

   public User getNextOwner() {
      return (User)this.members.get(0);
   }

   public boolean isMember(User user) {
      return this.members.contains(user);
   }

   public void addMember(User user) {
      if(!this.members.contains(user)) {
         this.members.add(user);
         user.properties.put("partyId", Integer.valueOf(this.id));
      } else {
         throw new UnsupportedOperationException("unable to add member already in the party");
      }
   }

   public void removeMember(User user) {
      if(this.members.contains(user)) {
         this.members.remove(user);
         user.properties.put("partyId", Integer.valueOf(-1));
      } else {
         throw new UnsupportedOperationException("unable to remove member not in the party");
      }
   }

   public LinkedList<SocketChannel> getChannelListButOne(User user) {
      LinkedList partyMembers = new LinkedList();
      Iterator i$ = this.members.iterator();

      while(i$.hasNext()) {
         User u = (User)i$.next();
         if(user != u) {
            partyMembers.add(u.getChannel());
         }
      }

      partyMembers.add(this.owner.getChannel());
      return partyMembers;
   }

   public LinkedList<SocketChannel> getChannelList() {
      LinkedList partyMembers = new LinkedList();
      Iterator i$ = this.members.iterator();

      while(i$.hasNext()) {
         User u = (User)i$.next();
         partyMembers.add(u.getChannel());
      }

      partyMembers.add(this.owner.getChannel());
      return partyMembers;
   }

   public String getOwner() {
      return (String)this.owner.properties.get("username");
   }

   public User getOwnerObject() {
      return this.owner;
   }

   public void setOwner(User user) {
      this.addMember(this.owner);
      this.removeMember(user);
      this.owner = user;
      this.owner.properties.put("partyId", Integer.valueOf(this.id));
   }
}
