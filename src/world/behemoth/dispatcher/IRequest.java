package world.behemoth.dispatcher;

import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public interface IRequest {
   void process(String[] var1, User var2, World var3, Room var4) throws RequestException;
}
