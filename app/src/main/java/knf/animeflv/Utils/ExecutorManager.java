package knf.animeflv.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jordy on 03/05/2016.
 */
public class ExecutorManager {
    private static int corePoolSize = 60;
    private static int maximumPoolSize = 80;
    private static int keepAliveTime = 10;

    public static Executor getExecutor() {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    }
}
