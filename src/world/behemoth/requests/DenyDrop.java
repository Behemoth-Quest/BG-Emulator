package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.Users;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Map;
import net.sf.json.JSONObject;

/**
 *
 * @author Mystical
 */
public class DenyDrop implements IRequest
{
    @Override
    public void process(String[] params, User user, World world, Room room) throws RequestException
    {
        Map drops = (Map) user.properties.get(Users.DROPS);
        int itemId = Integer.parseInt(params[0]);
        drops.remove(itemId);
    }
}
