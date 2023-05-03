package world.behemoth.discord.events;

import world.behemoth.discord.Bot;
import world.behemoth.world.World;
import jdbchelper.QueryResult;

public class ReloadSettings
{
    public ReloadSettings(World world)
    {
        if (! Bot.settings.isEmpty()) {
            Bot.settings.clear();
        }

        QueryResult rs = world.db.jdbc.query("SELECT * FROM discords_settings");
        while (rs.next()) {
            String Key = rs.getString("Key");
            String Value = rs.getString("Value");
            Bot.settings.put(Key, Value);
        }

        rs.close();
    }
}

