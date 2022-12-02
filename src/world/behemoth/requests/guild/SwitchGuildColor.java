 /*
 * Aldmor: Emulator
 * Copyright � 2015 Aldmor Team
 */
package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

 /**
 *
 * @author Nazzer23 & TheBaldMonk
 */
public class SwitchGuildColor implements IRequest {
    public SwitchGuildColor() {
        super();
    }
    @Override
    public void process(String[] var1, User var2, World var3, Room var4) throws RequestException {
        JSONObject var27 = (JSONObject)var2.properties.get("guildobj");
        if((Integer)var2.properties.get("guildrank") == 3){
            String guildColor = var1[1];
            String guildStringColor = null;
            Integer colorPrice = null;
            int userGold = var3.db.jdbc.queryForInt("SELECT Gold FROM users WHERE id = ? FOR UPDATE", new Object[]{var2.properties.get("dbId")});
            if(guildColor.equals("1")){
                guildStringColor = "GR";
                colorPrice = 100000000;
            } else if (guildColor.equals("2")){
                guildStringColor = "BL";
                colorPrice = 250000000;
            } else if (guildColor.equals("3")){
                guildStringColor = "PU";
                colorPrice = 500000000;
            } else if (guildColor.equals("4")){
                guildStringColor = "GO";
                colorPrice = 750000000;
            } else if (guildColor.equals("5")){
                guildStringColor = "BR";
                colorPrice = 800000000;
            } else if (guildColor.equals("6")){
                guildStringColor = "DB";
                colorPrice = 3500000;
            } else if (guildColor.equals("7")){
                guildStringColor = "PI";
                colorPrice = 2000000;
            } else if (guildColor.equals("8")){
                guildStringColor = "BG";
                colorPrice = 1000000;
            } else if (guildColor.equals("9")){
                guildStringColor = "CG";
                colorPrice = 1500000;
            } else if (guildColor.equals("10")){
                guildStringColor = "RE";
                colorPrice = 5000000;
            } else {
                throw new RequestException("That guild color does not exist!");
            }
            Integer userLeft = userGold - colorPrice;
            if(userGold < colorPrice){
                throw new RequestException("You do not have enough gold to buy that guild color!");
            } else{
                var3.db.jdbc.beginTransaction();
                var27.put("guildColor", guildStringColor);
                var3.sendGuildUpdate(var27);
                var2.properties.put("gold", userLeft);
                JSONObject guildhall = new JSONObject();
                guildhall.put("cmd", "updateEntities");
                guildhall.put("intGold", userLeft);
                guildhall.put("bitSuccess", Integer.valueOf(1));
                var3.send(guildhall, var2);
                var3.db.jdbc.execute("UPDATE users SET Gold = ? WHERE id = ?", new Object[]{Integer.valueOf(userLeft), var2.properties.get("dbId")});
                var3.db.jdbc.run("UPDATE guilds SET GuildColor='"+guildStringColor+"' WHERE Name = ?", new Object[]{var27.get("Name")});
                var3.send(new String[]{"server", "Congratulations! You have successfully updated your guild color!"}, var2);
                var3.db.jdbc.commitTransaction();
            }
        } else {
                throw new RequestException("You do not have the required permission for this. Please contact the guild leader.");
        }
    }
}