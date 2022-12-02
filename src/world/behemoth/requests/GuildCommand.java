package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.requests.guild.AddBuilding;
import world.behemoth.requests.guild.AddConnection;
import world.behemoth.requests.guild.AddFrame;
import world.behemoth.requests.guild.BuyPlot;
import world.behemoth.requests.guild.GetInterior;
import world.behemoth.requests.guild.GetInventory;
import world.behemoth.requests.guild.GetShop;
import world.behemoth.requests.guild.GuildAccept;
import world.behemoth.requests.guild.GuildBuyItem;
import world.behemoth.requests.guild.GuildCreate;
import world.behemoth.requests.guild.GuildDeclineInvite;
import world.behemoth.requests.guild.GuildDemote;
import world.behemoth.requests.guild.GuildInvite;
import world.behemoth.requests.guild.GuildMOTD;
import world.behemoth.requests.guild.GuildPromote;
import world.behemoth.requests.guild.GuildRemove;
import world.behemoth.requests.guild.GuildRename;
import world.behemoth.requests.guild.GuildSellItem;
import world.behemoth.requests.guild.GuildSlots;
import world.behemoth.requests.guild.RemoveBuilding;
import world.behemoth.requests.guild.RemoveConnection;
import world.behemoth.requests.guild.SaveInterior;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class GuildCommand implements IRequest {
   public GuildCommand() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      if(params[0].equals("gc")) {
         (new GuildCreate()).process(params, user, world, room);
      } else if(params[0].equals("gi")) {
         (new GuildInvite()).process(params, user, world, room);
      } else if(params[0].equals("ga")) {
         (new GuildAccept()).process(params, user, world, room);
      } else if(params[0].equals("gr")) {
         (new GuildRemove()).process(params, user, world, room);
      } else if(params[0].equals("gdi")) {
         (new GuildDeclineInvite()).process(params, user, world, room);
      } else if(params[0].equals("rename")) {
         (new GuildRename()).process(params, user, world, room);
      } else if(params[0].equals("gp")) {
         (new GuildPromote()).process(params, user, world, room);
      } else if(params[0].equals("gd")) {
         (new GuildDemote()).process(params, user, world, room);
      } else if(params[0].equals("motd")) {
         (new GuildMOTD()).process(params, user, world, room);
      } else if(params[0].equals("slots")) {
         (new GuildSlots()).process(params, user, world, room);
      } else if(params[0].equals("getInterior")) {
         (new GetInterior()).process(params, user, world, room);
      } else if(params[0].equals("buyplot")) {
         (new BuyPlot()).process(params, user, world, room);
      } else if(params[0].equals("getInv")) {
         (new GetInventory()).process(params, user, world, room);
      } else if(params[0].equals("getShop")) {
         (new GetShop()).process(params, user, world, room);
      } else if(params[0].equals("saveInt")) {
         (new SaveInterior()).process(params, user, world, room);
      } else if(params[0].equals("addFrame")) {
         (new AddFrame()).process(params, user, world, room);
      } else if(params[0].equals("addBuilding")) {
         (new AddBuilding()).process(params, user, world, room);
      } else if(params[0].equals("removeBuilding")) {
         (new RemoveBuilding()).process(params, user, world, room);
      } else if(params[0].equals("buyItem")) {
         (new GuildBuyItem()).process(params, user, world, room);
      } else if(params[0].equals("sellItem")) {
         (new GuildSellItem()).process(params, user, world, room);
      } else if(params[0].equals("addConnection")) {
         (new AddConnection()).process(params, user, world, room);
      } else if(params[0].equals("removeConnection")) {
         (new RemoveConnection()).process(params, user, world, room);
      }

   }
}
