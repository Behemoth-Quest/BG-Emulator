package world.behemoth;

import world.behemoth.config.ConfigData;
import world.behemoth.console.Console;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.log.SimpleLogFormat;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.events.InternalEventObject;
import it.gotoandplay.smartfoxserver.extensions.AbstractExtension;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import it.gotoandplay.smartfoxserver.lib.ActionscriptObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

public class Anu extends AbstractExtension {
   private final List<String> allowedRequestsForBannedUsers = Arrays.asList(new String[]{"mv", "firstJoin", "afk", "isModerator", "retrieveInventory", "moveToCell", "retrieveUserData", "retrieveUserDatas", "emotea"});
   private final Map<String, String> requests = new HashMap();
   private ExtensionHelper helper;
   private Console console;
   private World world;
   private HashMap<String, Long> IPList = new HashMap();
   private HashMap<String, Integer> IPCounter = new HashMap();

   public Anu() {
      super();
      Handler[] handlers = SmartFoxServer.log.getHandlers();
      Handler[] arr$ = handlers;
      int len$ = handlers.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Handler handler = arr$[i$];
         handler.setFormatter(new SimpleLogFormat());
      }
      
      this.console = new Console();
      new Interface().setVisible(true);
   }

   public void init() {
      this.requests.putAll(ConfigData.REQUESTS);
      this.helper = ExtensionHelper.instance();
      this.world = new World(this, this.helper.getZone(this.getOwnerZone()));
      this.console.setWorld(this.world);
      this.console.setHelper(this.helper);
      this.world.db.jdbc.run("UPDATE servers SET Online = 1 WHERE Name = ?", new Object[]{ConfigData.SERVER_NAME});
   }

   public void handleRequest(String cmd, ActionscriptObject ao, User user, int fromRoom) {
      throw new UnsupportedOperationException("ActionScriptObject requests are not supported.");
   }

   private boolean isRequestFiltered(User user, String request) {
      boolean filtered = false;
      long lastRequestTime = ((Long)user.properties.get("requestlastmili")).longValue() + ConfigData.ANTI_REQUESTFLOOD_MIN_MSG_TIME;
      int requestCounter = ((Integer)user.properties.get("requestcounter")).intValue();
      int requestWarningsCounter = ((Integer)user.properties.get("requestwarncounter")).intValue();
      int repeatedRequestCounter = ((Integer)user.properties.get("requestrepeatedcounter")).intValue();
      String lastRequest = (String)user.properties.get("requestlast");
      if(!user.isBeingKicked) {
         byte var10;
         if(lastRequestTime > System.currentTimeMillis()) {
            if(!ConfigData.ANTI_REQUESTFLOOD_GUARDED.contains(request)) {
               ++requestCounter;
               user.properties.put("requestcounter", Integer.valueOf(requestCounter));
               if(requestCounter >= ConfigData.ANTI_REQUESTFLOOD_TOLERANCE) {
                  if(user.properties.get("language").equals("BR")) {
                     this.world.send(new String[]{"warning", "Medidas tomadas muito rapidamente, tente novamente em um momento."}, user);
                  } else {
                     this.world.send(new String[]{"warning", "Action taken too quickly, try again in a moment."}, user);
                  }

                  ++requestWarningsCounter;
                  var10 = 0;
                  user.properties.put("requestwarncounter", Integer.valueOf(requestWarningsCounter));
                  user.properties.put("requestcounter", Integer.valueOf(var10));
                  filtered = true;
               }
            }
         } else {
            var10 = 0;
            user.properties.put("requestcounter", Integer.valueOf(var10));
         }

         if(ConfigData.ANTI_REQUESTFLOOD_REPEAT_ENABLED) {
            byte var11;
            if(request.equals(lastRequest) && !ConfigData.ANTI_REQUESTFLOOD_GUARDED.contains(request)) {
               ++repeatedRequestCounter;
               user.properties.put("requestrepeatedcounter", Integer.valueOf(repeatedRequestCounter));
               if(repeatedRequestCounter >= ConfigData.ANTI_REQUESTFLOOD_MAX_REPEATED) {
                  ++requestWarningsCounter;
                  var11 = 0;
                  user.properties.put("requestwarncounter", Integer.valueOf(requestWarningsCounter));
                  user.properties.put("requestrepeatedcounter", Integer.valueOf(var11));
                  filtered = true;
               }
            } else {
               var11 = 0;
               user.properties.put("requestrepeatedcounter", Integer.valueOf(var11));
               user.properties.put("requestlast", request);
            }
         }

         if(requestWarningsCounter >= ConfigData.ANTI_REQUESTFLOOD_WARNINGS) {
            filtered = true;
            SmartFoxServer.log.warning("Too many requests for user:  " + user.properties.get("username"));
            user.isBeingKicked = true;
            SmartFoxServer.getInstance().addKickedUser(user, 1);
            this.world.users.kick(user);
         }
      }

      user.properties.put("requestlastmili", Long.valueOf(System.currentTimeMillis()));
      return filtered;
   }

   public void handleRequest(String cmd, String[] params, User user, int fromRoom) {
      SmartFoxServer.log.fine("Recieved request: " + cmd);
      if(user != null) {
         if(!this.isRequestFiltered(user, cmd)) {
            if(this.requests.containsKey(cmd)) {
               SmartFoxServer.log.fine("Processing request: " + cmd);
               int access = ((Integer)user.properties.get("access")).intValue();
               if(access <= 0 && !this.allowedRequestsForBannedUsers.contains(cmd)) {
                  if(user.properties.get("language").equals("BR")) {
                     this.world.send(new String[]{"warning", "Sua conta esta atualmente desativado. Acoes do jogo sao limitadas."}, user);
                  } else {
                     this.world.send(new String[]{"warning", "Your account is currently disabled. Actions in-game are limited."}, user);
                  }

                  return;
               }

               try {
                  Class ex = Class.forName((String)this.requests.get(cmd));
                  IRequest request = (IRequest)ex.newInstance();
                  Room room;
                  if(fromRoom != 1 && fromRoom != 32123 && fromRoom > 0) {
                     room = this.world.zone.getRoom(fromRoom);
                     if(room != null) {
                        request.process(params, user, this.world, room);
                     } else {
                        this.world.users.kick(user);
                     }
                  } else {
                     room = this.world.zone.getRoom(user.getRoom());
                     request.process(params, user, this.world, room);
                  }
               } catch (ClassNotFoundException var9) {
                  SmartFoxServer.log.severe("Class not found:" + var9.getMessage());
               } catch (InstantiationException var10) {
                  SmartFoxServer.log.severe("Instantiation error:" + var10.getMessage());
               } catch (IllegalAccessException var11) {
                  SmartFoxServer.log.severe("Illegal access error:" + var11.getMessage());
               } catch (NullPointerException var12) {
                  SmartFoxServer.log.severe("Null error on " + cmd + " request on line " + var12.getStackTrace()[0].getLineNumber() + ": " + var12.getMessage());
               } catch (RequestException var13) {
                  this.world.send(new String[]{var13.getType(), var13.getMessage()}, user);
               }
            } else {
               if(user.properties.get("language").equals("BR")) {
                  this.world.send(new String[]{"server", "A acao que voce esta tentando executar ainda nao esta implementado. Entre em contato com a equipe de desenvolvimento se voce quiser disponivel."}, user);
               } else {
                  this.world.send(new String[]{"server", "The action you are trying to execute is not yet implemented. Please contact the development staff if you want it available."}, user);
               }

               SmartFoxServer.log.warning("Unknown request: " + cmd);
            }

         }
      }
   }

   public void handleInternalEvent(InternalEventObject ieo) {
      String event = ieo.getEventName();
      SmartFoxServer.log.fine("System event: " + ieo.getEventName());
      if(event.equals("serverReady")) {
        this.console.start();
      } else if(event.equals("loginRequest")) {
         String user = ieo.getParam("nick").split("~")[1];
         String room = ieo.getParam("pass");
         SocketChannel userObj = (SocketChannel)ieo.getObject("chan");
         String uJoin = userObj.socket().getInetAddress().getHostAddress();
         if(this.IPList.containsKey(uJoin)) {
            Long time = (Long)this.IPList.get(uJoin);
            if(time.longValue() + 1500L > System.currentTimeMillis()) {
               this.IPCounter.put(uJoin, Integer.valueOf(this.IPCounter.containsKey(uJoin)?((Integer)this.IPCounter.get(uJoin)).intValue() + 1:1));
               if(this.IPCounter.containsKey(uJoin) && ((Integer)this.IPCounter.get(uJoin)).intValue() > 5) {
                  FileWriter fw = null;

                  try {
                     if(FileUtils.readFileToString(new File(ConfigData.ANTI_REQUESTFLOOD_BANNEDLIST)).contains(uJoin)) {
                        return;
                     }

                     this.kickMaliciousClient(userObj);
                     fw = new FileWriter(ConfigData.ANTI_REQUESTFLOOD_BANNEDLIST, true);
                     fw.write((new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")).format(new Date()) + "|" + uJoin + "\n");
                     fw.close();
                  } catch (IOException var10) {
                     SmartFoxServer.log.warning("System I/O Exception: " + var10);
                  }
               }

               return;
            }

            this.IPList.remove(uJoin);
            this.IPCounter.remove(uJoin);
         }

         this.IPList.put(uJoin, Long.valueOf(System.currentTimeMillis()));
         this.world.users.login(user.toLowerCase(), room, userObj);
      } else {
         Room user1;
         if(event.equals("newRoom")) {
            user1 = (Room)ieo.getObject("room");
            SmartFoxServer.log.fine("New room created: " + user1.getName());
         } else {
            User room1;
            if(event.equals("userJoin")) {
               user1 = (Room)ieo.getObject("room");
               room1 = (User)ieo.getObject("user");
               JSONObject userObj1 = this.world.users.getProperties(room1, user1);
               JSONObject uJoin1 = new JSONObject();
               uJoin1.put("cmd", "uotls");
               uJoin1.put("o", userObj1);
               uJoin1.put("unm", room1.getName());
               this.world.sendToRoomButOne(uJoin1, room1, user1);
            } else if(event.equals("userExit")) {
               user1 = (Room)ieo.getObject("room");
               room1 = (User)ieo.getObject("user");
               this.world.rooms.exit(user1, room1);
               if(user1.getUserCount() <= 0) {
                  this.helper.destroyRoom(this.world.zone, user1.getId());
               }
            } else if(event.equals("userLost") || event.equals("logOut")) {
               User user2 = (User)ieo.getObject("user");
               Room room2 = this.world.zone.getRoom(user2.getRoom());
               if(room2 != null) {
                  this.world.rooms.exit(room2, user2);
                  room2.removeUser(user2, true, true);
                  if(room2.getUserCount() <= 0) {
                     this.helper.destroyRoom(this.world.zone, room2.getId());
                  }
               }

               this.world.users.lost(user2);
               this.world.db.jdbc.run("UPDATE servers SET Count = ? WHERE Name = ?", new Object[]{Integer.valueOf(this.world.zone.getUserCount()), ConfigData.SERVER_NAME});
            }
         }
      }

   }

   public void kickMaliciousClient(SocketChannel chan) throws IOException {
      DataInputStream dataIn = new DataInputStream(chan.socket().getInputStream());
      DataOutputStream dataOut = new DataOutputStream(chan.socket().getOutputStream());
      chan.close();
      chan.socket().close();
      dataIn.close();
      dataOut.flush();
      dataOut.close();
   }

   public void destroy()
   {
    this.console.stop();
      this.world.db.jdbc.run("UPDATE servers SET Online = 0 WHERE Name = ?", new Object[]{ConfigData.SERVER_NAME});
      this.world.destroy();
      SmartFoxServer.log.info("Anu destroyed");
   }
}
