package world.behemoth.db.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;

import jdbchelper.ResultSetMapper;

public class DiscordCommand {
    private int access, status;
    private String command, file, desc, param;

    public static final ResultSetMapper<String, DiscordCommand> resultSetMapper = new ResultSetMapper() {
        public AbstractMap.SimpleEntry<String, DiscordCommand> mapRow(ResultSet rs) throws SQLException {
            DiscordCommand command = new DiscordCommand();
            command.access = rs.getInt("Access");
            command.command = rs.getString("Command");
            command.file = rs.getString("File");
            command.desc = rs.getString("Desc");
            command.param = rs.getString("Param");
            command.status = rs.getInt("Status");

            return new AbstractMap.SimpleEntry(String.valueOf(command.getCommand()), command);
        }
    };

    public int getAccess() {
        return this.access;
    }

    public String getCommand() {
        return this.command;
    }

    public String getFileName() {
        return this.file;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getParam() {
        return this.param;
    }

    public int getStatus() {
        return this.status;
    }

    public java.lang.Class getFile() throws ClassNotFoundException {
        java.lang.Class ex = java.lang.Class.forName(this.file);
        return ex;
    }
}