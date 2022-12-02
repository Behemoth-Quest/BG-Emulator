package world.behemoth.requests;

import world.behemoth.Interface;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.PartyInfo;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdbchelper.JdbcException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class Message implements IRequest {
   public Message() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      if(((Integer)user.properties.get("permamute")).intValue() > 0) {
         throw new RequestException("You are muted! Chat privileges have been permanently revoked.");
      } else if(world.users.isMute(user)) {
         int var22 = world.users.getMuteTimeInSeconds(user);
         if(user.properties.get("language").equals("BR")) {
            throw new RequestException(world.users.getMuteMessageBR((double)var22));
         } else {
            throw new RequestException(world.users.getMuteMessage((double)var22));
         }
      } else {
         String channel = params[1];
         String message = params[0];
         Iterator pattern = world.chatFilters.keySet().iterator();

         int members;
         while(pattern.hasNext()) {
            String parts = (String)pattern.next();
            String access = message.toLowerCase().replaceAll(" ", "").replaceAll("@", "a").replaceAll("v", "u");
            if(access.contains(parts)) {
               world.users.mute(user, ((Integer)world.chatFilters.get(parts)).intValue(), 13);
               members = world.users.getMuteTimeInSeconds(user);
               if(user.properties.get("language").equals("BR")) {
                  throw new RequestException(world.users.getMuteMessageBR((double)members));
               }

               throw new RequestException(world.users.getMuteMessage((double)members));
            }
         }

         Pattern var23 = Pattern.compile("\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel|[a-z]{2}))(:[\\d]{1,5})?(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");
         String[] var24 = message.split("\\s");
         String[] var25 = var24;
         members = var24.length;

         for(int it = 0; it < members; ++it) {
            String member = var25[it];
            Matcher client = var23.matcher(member.toLowerCase());
            if(client.matches()) {
               byte muteTimeInMinutes = 5;
               world.users.mute(user, muteTimeInMinutes, 12);
               int seconds = world.users.getMuteTimeInSeconds(user);
               String kek = null;
               if(user.properties.get("language").equals("BR")) {
                  kek = world.users.getMuteMessageBR((double)seconds);
               } else {
                  kek = world.users.getMuteMessage((double)seconds);
               }

               world.send(new String[]{"warning", kek}, user);
               if(user.properties.get("language").equals("BR")) {
                  throw new RequestException("Por favor, abster-se de publicar esse tipo de mensagem.", "warning");
               }

               throw new RequestException("Please refrain from posting this kind of message.", "warning");
            }
         }

         if(message.length() > 150) {
            message = message.substring(0, 150);
         }

         message = StringUtils.replaceEach(message, new String[]{"#038:", "&", "\"", "<", ">"}, new String[]{"&", "&amp;", "&quot;", "&lt;", "&gt;"});
         int var26;
         if(channel.equals("world")) {
            world.db.jdbc.beginTransaction();

            try {
               var26 = world.db.jdbc.queryForInt("SELECT Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
               if(var26 < 100) {
                  world.db.jdbc.rollbackTransaction();
                  if(user.properties.get("language").equals("BR")) {
                     throw new RequestException("Voce precisa de pelo menos 100ACs para enviar uma mensagem ao canal mundo.", "server");
                  }

                  throw new RequestException("You need at least 100ACs to send a message to world channel.", "server");
               }

               JSONObject var28 = new JSONObject();
               var28.put("cmd", "sellItem");
               var28.put("intAmount", Integer.valueOf(-100));
               var28.put("CharItemID", Integer.valueOf(user.hashCode()));
               var28.put("bCoins", Integer.valueOf(1));
               world.db.jdbc.run("UPDATE users SET Coins = (Coins - 100) WHERE id = ?", new Object[]{user.properties.get("dbId")});
               world.send(var28, user);
               world.sendToUsers(new String[]{"chatm", "world~" + message, user.getName(), String.valueOf(1)});
            } catch (JdbcException var20) {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.rollbackTransaction();
               }

               SmartFoxServer.log.severe("Error in world message transaction: " + var20.getMessage());
            } finally {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.commitTransaction();
               }

            }
         } else if(channel.equals("party")) {
            var26 = ((Integer)user.properties.get("partyId")).intValue();
            if(var26 < 0) {
               if(user.properties.get("language").equals("BR")) {
                  throw new RequestException("Voce nao esta em uma festa.", "server");
               }

               throw new RequestException("You are not in a party.", "server");
            }

            PartyInfo var29 = world.parties.getPartyInfo(var26);
            world.send(new String[]{"chatm", "party~" + message, user.getName(), String.valueOf(1)}, var29.getChannelList());
         } else if(channel.equals("guild")) {
            if(((Integer)user.properties.get("guildid")).intValue() <= 0) {
               if(user.properties.get("language").equals("BR")) {
                  throw new RequestException("Voce nao esta em uma alianca.", "server");
               }

               throw new RequestException("You are not in a guild.", "server");
            }
            
            if((Integer) user.properties.get("guildrank") == 0){
                JSONObject var27 = (JSONObject)user.properties.get("guildobj");
                String guildName = (String)var27.get("Name");
                throw new RequestException("You may not chat in guild chat until you are promoted to a member of "+guildName+".", "server");
            }

            JSONObject var27 = (JSONObject)user.properties.get("guildobj");
            JSONArray var30 = (JSONArray)var27.get("ul");
            if(var30 != null && var30.size() > 0) {
               Iterator var31 = var30.iterator();

               while(var31.hasNext()) {
                  JSONObject var32 = (JSONObject)var31.next();
                  User var33 = world.zone.getUserByName(var32.get("userName").toString().toLowerCase());
                  if(var33 != null) {
                     world.send(new String[]{"chatm", "guild~" + message, user.getName(), String.valueOf(1)}, var33);
                  }
               }
            }
         } else {
            var26 = ((Integer)user.properties.get("access")).intValue();
            switch(var26) {
            case 40:
               channel = "moderator";
               break;
            case 60:
               channel = "admin";
               break;
            default:
               channel = "zone";
            }

            world.sendToRoom(new String[]{"chatm", channel + "~" + message, user.getName(), String.valueOf(room.getId())}, user, room);
         }
         Interface.jTextArea2.append("message:"+user.getName()+":"+message);

         world.applyFloodFilter(user, message);
      }
   }
}
