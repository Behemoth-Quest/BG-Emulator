package world.behemoth.console;

import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;

public class Console implements Runnable {
   private World world;
   private Thread thread;
   private ExtensionHelper helper;
   private boolean running;

   public Console() {
      super();
      this.thread = new Thread(this);
      this.thread.setDaemon(false);
   }

   public Console(World world, ExtensionHelper helper) {
      super();
      this.world = world;
      this.helper = helper;
   }

   public void start() {
      if(this.world == null && this.helper == null) {
         throw new UnsupportedOperationException("World and Helper must be set first.");
      } else {
         this.running = true;
         this.thread.start();
      }
   }

   public void stop() {
      this.running = false;
   }

   public void run() {
      try {
         BufferedReader ex = new BufferedReader(new InputStreamReader(System.in, Charset.forName("UTF-8")));
         System.out.print("\nconsole > ");

         for(; this.running; System.out.print("console > ")) {
            String cmd = ex.readLine();
            if(!cmd.equals("help") && !cmd.equals("?")) {
               if(cmd.startsWith("log")) {
                  try {
                     cmd = cmd.length() == 3?"log all":cmd;
                     SmartFoxServer.log.setLevel(Level.parse(cmd.substring(4).toUpperCase()));
                     System.out.println("Press ENTER to exit. Now logging for " + cmd.substring(4).toUpperCase() + " messages:\n\n");
                     ex.readLine();
                  } catch (IllegalArgumentException var8) {
                     System.out.println(var8.getMessage());
                  } finally {
                     SmartFoxServer.log.setLevel(Level.SEVERE);
                  }
               } else if(cmd.startsWith("msg")) {
                  System.out.println("Entered broadcast mode. Type \'quit\' to exit.");
                  System.out.print("\nmessage > ");

                  while(!cmd.equalsIgnoreCase("quit")) {
                     cmd = ex.readLine();
                     if(!cmd.equalsIgnoreCase("quit")) {
                        this.world.send(new String[]{"administrator", cmd}, this.world.zone.getChannelList());
                        System.out.print("message > ");
                     } else {
                        System.out.println();
                     }
                  }
               } else if(cmd.equals("restart")) {
                  this.world.send(new String[]{"logoutWarning", "", "60"}, this.world.zone.getChannelList());
                  this.helper.rebootServer();
               } else {
                  System.out.println("Unknown command, type \'help\' or \'?\' for a full list of commands.");
               }
            } else {
               System.out.println("log (all,severe,warning,info,fine,finest) - logs for messages depending on given type");
               System.out.println("msg (message) - broadcast a message throughout the server");
               System.out.println("restart - restarts the server");
            }
         }
      } catch (IOException var10) {
         SmartFoxServer.log.severe("Error in console: " + var10.getMessage());
      }

   }

   public void setWorld(World world) {
      this.world = world;
   }

   public void setHelper(ExtensionHelper helper) {
      this.helper = helper;
   }
}
