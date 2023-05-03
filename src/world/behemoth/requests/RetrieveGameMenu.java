package world.behemoth.requests;

import it.gotoandplay.smartfoxserver.SmartFoxServer;
import world.behemoth.db.objects.GameMenu;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import java.util.Map;

import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RetrieveGameMenu implements IRequest
{
    @Override
    public void process(String[] params, User user, World world, Room room) throws RequestException
    {
        String option = params[0];
        JSONArray menuList = new JSONArray();
        JSONObject lmn = new JSONObject();
        lmn.put("cmd", "gameMenu");

        for (Map.Entry<Integer, GameMenu> entry : world.gameMenu.entrySet())
        {
            if (option.equals(entry.getValue().getType())) {
                JSONObject menu = new JSONObject();
                menu.put("id", entry.getValue().getId());
                menu.put("Name", entry.getValue().getName());
                menu.put("NameColor", entry.getValue().getNameColor());
                menu.put("Subtitle", entry.getValue().getSubtitle());
                menu.put("SubtitleColor", entry.getValue().getSubtitleColor());
                menu.put("Type", entry.getValue().getType());
                menu.put("Data", entry.getValue().getData().equals("Shop")
                        ? Integer.valueOf(entry.getValue().getData())
                        : entry.getValue().getData());
                menuList.add(menu);
            }
        }

        lmn.put("option", option);
        lmn.put("menu", menuList);
        lmn.put("bitSuccess", 1);
        world.send(lmn, user);
    }
}
