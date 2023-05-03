package world.behemoth.tasks;

import world.behemoth.config.ConfigData;
import world.behemoth.db.Database;

public class FreeDbPool implements Runnable {
    private Database db;

    public FreeDbPool(Database db) {
        super();
        this.db = db;
    }

    public void run() {
        if (!ConfigData.DB_CONNECTION.equals("hikari"))
            this.db.freeIdleConnections();
    }
}
