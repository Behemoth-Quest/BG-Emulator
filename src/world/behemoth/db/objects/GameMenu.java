package world.behemoth.db.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import jdbchelper.ResultSetMapper;

public class GameMenu {
    private int id;
    private String Icon, Name, Subtitle, Type, Data;

    public static final ResultSetMapper<Integer, GameMenu> resultSetMapper = new ResultSetMapper<Integer, GameMenu>()
    {
        public AbstractMap.SimpleEntry<Integer, GameMenu> mapRow(ResultSet rs) throws SQLException {
            GameMenu menu = new GameMenu();
            menu.id = rs.getInt("id");
            menu.Icon = rs.getString("Icon");
            menu.Name = rs.getString("Name");
            menu.Subtitle = rs.getString("Subtitle");
            menu.Type = rs.getString("Type");
            menu.Data = rs.getString("Data");

            return new AbstractMap.SimpleEntry<>(Integer.valueOf(menu.id), menu);
        }
    };

    public int getId() {
        return this.id;
    }

    public String getIcon() {
        return this.Icon;
    }

    public String getName() {
        return this.Name;
    }

    public String getSubtitle() {
        return this.Subtitle;
    }

    public String getType() {
        return this.Type;
    }

    public String getData() {
        return this.Data;
    }
}
