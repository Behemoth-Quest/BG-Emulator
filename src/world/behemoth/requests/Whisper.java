package world.behemoth.requests;

import world.behemoth.Interface;
import world.behemoth.aqw.Settings;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class Whisper implements IRequest {
   public Whisper() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      if(((Integer)user.properties.get("permamute")).intValue() > 0) {
         if(user.properties.get("language").equals("BR")) {
            throw new RequestException("Voce esta silenciado! Privilegios de bate-papo foram permanentemente revogada.");
         } else {
            throw new RequestException("You are muted! Chat privileges have been permanently revoked.");
         }
      } else if(world.users.isMute(user)) {
         int message1 = world.users.getMuteTimeInSeconds(user);
         if(user.properties.get("language").equals("BR")) {
            throw new RequestException(world.users.getMuteMessageBR((double)message1));
         } else {
            throw new RequestException(world.users.getMuteMessage((double)message1));
         }
      } else {
         String message = params[0];
         String username = params[1].toLowerCase();
         User client = world.zone.getUserByName(username);
         if(client == null) {
            if(user.properties.get("language").equals("BR")) {
               throw new RequestException("Jogador \"" + username + "\" nao pode ser encontrado.");
            } else {
               throw new RequestException("Player \"" + username + "\" could not be found.");
            }
         } else if(!Settings.isAllowed("bWhisper", user, client)) {
            if(user.properties.get("language").equals("BR")) {
               throw new RequestException("Jogador " + client.getName() + " nao esta aceitando PMs no momento.");
            } else {
               throw new RequestException("Player " + client.getName() + " is not accepting PMs at this time.");
            }
         } else {
            String[] msg = new String[]{"whisper", message, user.getName(), username, "0"};
            Interface.jTextArea2.append("whisper:"+user.getName()+" to "+client.getName()+":"+message);
            world.send(msg, user);
            world.send(msg, client);
            world.applyFloodFilter(user, message);
         }
      }
   }
}
