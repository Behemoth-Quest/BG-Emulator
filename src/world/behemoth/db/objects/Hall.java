package world.behemoth.db.objects;

import java.util.HashSet;

public class Hall extends Area {
   private int guildId;

   public Hall(int guildId) {
      super();
      this.guildId = guildId;
      this.monsters = new HashSet();
   }

   public int getReqLevel() {
      return 0;
   }

   public boolean isStaff() {
      return false;
   }

   public boolean isUpgrade() {
      return false;
   }

   public boolean isPvP() {
      return false;
   }

   public int getMaxPlayers() {
      return 100000;
   }

   public String getFile() {
      return "Guildhall/guildHallTest.swf";
   }

   public int getGuildId() {
      return this.guildId;
   }
}
