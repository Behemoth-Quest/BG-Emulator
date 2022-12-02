package world.behemoth.db.objects;

import java.util.HashSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class House extends Area {
   private JSONArray houseItems;
   private String ownerName;
   private String houseInfo;
   private int ownerId;

   public House(JSONArray houseItems, String houseInfo, String ownerName, int ownerId) {
      super();
      this.houseItems = houseItems;
      this.houseInfo = houseInfo;
      this.ownerName = ownerName;
      this.ownerId = ownerId;
      this.monsters = new HashSet();
   }

   public JSONObject getData() {
      JSONObject house = new JSONObject();
      house.put("strMapFileName", this.file);
      house.put("strMapName", "house");
      house.put("sHouseInfo", this.houseInfo);
      house.put("items", this.houseItems);
      house.put("roomName", "house-" + Integer.toString(this.ownerId));
      house.put("unm", this.ownerName);
      house.put("CharID", Integer.valueOf(this.ownerId));
      return house;
   }

   public void setFile(String file) {
      this.file = file;
   }

   public void setHouseInfo(String houseInfo) {
      this.houseInfo = houseInfo;
   }

   public void setHouseItems(JSONArray houseItems) {
      this.houseItems = houseItems;
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
}
