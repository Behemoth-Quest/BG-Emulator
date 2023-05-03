package world.behemoth.db.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;

import jdbchelper.ResultSetMapper;

public class DiscordCommand {
    private int access;
    private String command, file;

    public static final ResultSetMapper<String, DiscordCommand> resultSetMapper = new ResultSetMapper() {
        public AbstractMap.SimpleEntry<String, DiscordCommand> mapRow(ResultSet rs) throws SQLException {
            DiscordCommand command = new DiscordCommand();
            command.access = rs.getInt("Access");
            command.command = rs.getString("Command");
            command.file = rs.getString("File");

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

    public java.lang.Class getFile() throws ClassNotFoundException {
        java.lang.Class ex = java.lang.Class.forName(this.file);
        return ex;
    }
}