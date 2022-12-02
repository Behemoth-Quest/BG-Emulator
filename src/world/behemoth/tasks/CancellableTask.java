package world.behemoth.tasks;

import java.util.concurrent.ScheduledFuture;

public interface CancellableTask {
   void setRunning(ScheduledFuture<?> var1);

   void cancel();
}
