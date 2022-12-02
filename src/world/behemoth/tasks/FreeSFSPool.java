package world.behemoth.tasks;

import it.gotoandplay.smartfoxserver.SmartFoxServer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

public class FreeSFSPool implements Runnable {
   private HashMap<Integer, SocketChannel> DeadChannels = new HashMap();

   public FreeSFSPool() {
      super();
   }

   public void run() {
      try {
         LinkedList ex = SmartFoxServer.getInstance().getChannels();
         Iterator sw1 = ex.iterator();

         while(sw1.hasNext()) {
            SocketChannel exceptionAsString1 = (SocketChannel)sw1.next();
            if(SmartFoxServer.getInstance().getUserByChannel(exceptionAsString1) == null) {
               this.DeadChannels.put(Integer.valueOf(exceptionAsString1.hashCode()), exceptionAsString1);
            }
         }

         sw1 = this.DeadChannels.entrySet().iterator();

         while(sw1.hasNext()) {
            Entry exceptionAsString2 = (Entry)sw1.next();
            Integer hash = (Integer)exceptionAsString2.getKey();
            SocketChannel chan = (SocketChannel)exceptionAsString2.getValue();
            SmartFoxServer.log.info("Closing dead channel: " + hash);
            SmartFoxServer.getInstance().lostConnection(chan);
         }
      } catch (Exception var6) {
         StringWriter sw = new StringWriter();
         var6.printStackTrace(new PrintWriter(sw));
         String exceptionAsString = sw.toString();
         SmartFoxServer.log.warning("Error disposing dead pool: " + exceptionAsString);
      }

   }
}
