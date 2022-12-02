package world.behemoth.requests;

import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class SummonPet implements IRequest {
   public SummonPet() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int petParam = Integer.parseInt(params[0]);
      Item pet = (Item)world.items.get(Integer.valueOf(petParam));
      if(pet != null) {
         if(pet.isTemporary() && pet.getEquipment().equals("pe")) {
            JSONObject ei = new JSONObject();
            JSONObject ai = new JSONObject();
            JSONObject ii = new JSONObject();
            JSONObject arrPet = new JSONObject();
            ei.element("cmd", "equipItem");
            ei.element("uid", user.getUserId());
            ei.element("ItemID", petParam);
            ei.element("strES", pet.getEquipment());
            ei.element("sFile", pet.getFile());
            ei.element("sLink", pet.getLink());
            ei.element("sMeta", "Necromancer");
            world.send(ei, room.getChannellList());
            ii.element("ItemID", petParam);
            ii.element("sElmt", pet.getElement());
            ii.element("sLink", pet.getLink());
            ii.element("iRng", pet.getRange());
            ii.element("bStaff", pet.isStaff());
            ii.element("iDPS", pet.getDPS());
            ii.element("bCoins", pet.isCoins());
            ii.element("sES", pet.getEquipment());
            ii.element("sType", pet.getType());
            ii.element("iCost", pet.getCost());
            ii.element("iRty", pet.getRarity());
            ii.element("iQSValue", pet.getQuestStringValue());
            ii.element("iQty", pet.getQuantity());
            ii.element("sReqQuests", pet.getReqQuests());
            ii.element("sIcon", pet.getIcon());
            ii.element("iLvl", pet.getLevel());
            ii.element("bTemp", "1");
            ii.element("bPTR", "0");
            ii.element("sFile", pet.getFile());
            ii.element("iQSIndex", pet.getQuestStringIndex());
            ii.element("iStk", pet.getStack());
            ii.element("sDesc", pet.getDescription());
            ii.element("bHouse", pet.isHouse());
            ii.element("bUpg", pet.isUpgrade());
            ii.element("sName", pet.getName());
            ii.element("sMeta", "Necromancer");
            arrPet.put(Integer.valueOf(petParam), ii);
            ai.element("cmd", "addItems");
            ai.element("items", arrPet);
            world.send(ai, user);
            JSONObject eqp = (JSONObject)user.properties.get("equipment");
            JSONObject eqpObj = new JSONObject();
            eqpObj.element("ItemID", petParam);
            eqpObj.element("sFile", pet.getFile());
            eqpObj.element("sLink", pet.getLink());
            eqp.put(pet.getEquipment(), eqpObj);
         }

      } else {
         throw new RequestException("Pet entity isn\'t existing!");
      }
   }
}
