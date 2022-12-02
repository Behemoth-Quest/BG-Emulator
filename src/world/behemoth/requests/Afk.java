package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class Afk implements IRequest {
   public Afk() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      boolean afk = Boolean.parseBoolean(params[0]);
      if(afk != ((Boolean)user.properties.get("afk")).booleanValue()) {
         if(!afk) {
            if(user.properties.get("language").equals("BR")) {
               world.send(new String[]{"server", "Voce nao esta mais longe do teclado (AFK)."}, user);
            } else {
               world.send(new String[]{"server", "You are no longer Away From Keyboard (AFK)."}, user);
            }
         } else if(user.properties.get("language").equals("BR")) {
            world.send(new String[]{"server", "Agora voce esta longe do teclado (AFK)."}, user);
         } else {
            world.send(new String[]{"server", "You are now Away From Keyboard (AFK)."}, user);
         }

         user.properties.put("afk", Boolean.valueOf(afk));
         world.send(new String[]{"uotls", user.getName(), "afk:" + Boolean.parseBoolean(params[0])}, room.getChannellList());
      }

   }
}
