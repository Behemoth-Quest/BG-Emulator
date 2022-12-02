package world.behemoth.requests;

import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import world.behemoth.world.stats.Stats;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

public class UnequipItem
  implements IRequest
{
  public void process(String[] paramArrayOfString, User paramUser, World paramWorld, Room paramRoom)
    throws RequestException
  {
    int i = Integer.parseInt(paramArrayOfString[0]);
    JSONObject localJSONObject = new JSONObject();
    Item localItem = (Item)paramWorld.items.get(Integer.valueOf(i));
    String str = localItem.getEquipment();
    localJSONObject.put("cmd", "unequipItem");
    localJSONObject.put("ItemID", Integer.valueOf(i));
    localJSONObject.put("uid", Integer.valueOf(paramUser.getUserId()));
    localJSONObject.put("strES", str);
    if ((localItem.getEquipment().equals("ar")) || (localItem.getEquipment().equals("ba")) || (localItem.getEquipment().equals("he")) || (localItem.getEquipment().equals("Weapon")))
    {
      Stats localObject = (Stats)paramUser.properties.get("stats");
      Iterator localIterator;
      Map.Entry localEntry;
      if (localItem.getEquipment().equals("ar"))
      {
        localIterator = ((Stats)localObject).armor.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localEntry = (Map.Entry)localIterator.next();
          ((Stats)localObject).armor.put((String) localEntry.getKey(), Double.valueOf(0.0D));
        }
      }
      if (localItem.getEquipment().equals("ba"))
      {
        localIterator = ((Stats)localObject).cape.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localEntry = (Map.Entry)localIterator.next();
          ((Stats)localObject).cape.put((String) localEntry.getKey(), Double.valueOf(0.0D));
        }
      }
      if (localItem.getEquipment().equals("he"))
      {
        localIterator = ((Stats)localObject).helm.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localEntry = (Map.Entry)localIterator.next();
          ((Stats)localObject).helm.put((String) localEntry.getKey(), Double.valueOf(0.0D));
        }
      }
      if (localItem.getEquipment().equals("Weapon"))
      {
        localIterator = ((Stats)localObject).weapon.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localEntry = (Map.Entry)localIterator.next();
          ((Stats)localObject).weapon.put((String) localEntry.getKey(), Double.valueOf(0.0D));
        }
      }
      paramWorld.users.sendStats(paramUser);
    }
    Object localObject = (JSONObject)paramUser.properties.get("equipment");
    ((JSONObject)localObject).remove(str);
    paramWorld.sendToRoom(localJSONObject, paramUser, paramRoom);
    paramWorld.db.jdbc.run("UPDATE users_items SET Equipped = 0 WHERE ItemID = ? AND UserID = ?", new Object[] { Integer.valueOf(i), paramUser.properties.get("dbId") });
  }
}

/* Location:           C:\Users\acer\Downloads\pie\
 * Qualified Name:     pie.requests.UnequipItem
 * JD-Core Version:    0.6.2
 */