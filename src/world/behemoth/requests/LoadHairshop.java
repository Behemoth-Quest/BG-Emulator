package world.behemoth.requests;

import world.behemoth.db.objects.Hair;
import world.behemoth.db.objects.Hairshop;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class LoadHairshop implements IRequest {
   public LoadHairshop() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int shopId = Integer.parseInt(params[0]);
      JSONObject hairshop = new JSONObject();
      JSONArray arrHairs = new JSONArray();
      if(world.hairshops.containsKey(Integer.valueOf(shopId))) {
         Hairshop hairshopObj = (Hairshop)world.hairshops.get(Integer.valueOf(shopId));
         Set shopItems = hairshopObj.getShopItems((String)user.properties.get("gender"));
         Iterator i$ = shopItems.iterator();

         while(i$.hasNext()) {
            int hairId = ((Integer)i$.next()).intValue();
            Hair hairObj = (Hair)world.hairs.get(Integer.valueOf(hairId));
            if(hairObj != null) {
               JSONObject hair = new JSONObject();
               hair.put("sFile", hairObj.getFile());
               hair.put("HairID", Integer.valueOf(hairId));
               hair.put("sName", hairObj.getName());
               hair.put("sGen", hairObj.getGender());
               arrHairs.add(hair);
            }
         }

         hairshop.put("HairShopID", Integer.valueOf(shopId));
         hairshop.put("cmd", "loadHairShop");
         hairshop.put("hair", arrHairs);
         world.send(hairshop, user);
      }
   }
}
