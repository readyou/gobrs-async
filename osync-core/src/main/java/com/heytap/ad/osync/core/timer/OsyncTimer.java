
package com.heytap.ad.osync.core.timer;

import com.heytap.ad.osync.core.common.util.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class OsyncTimer {

    private static final Logger logger = LoggerFactory.getLogger(OsyncTimer.class);

    private static OsyncTimer INSTANCE;

    AtomicReference<ScheduledExecutor> executor = new AtomicReference<ScheduledExecutor>();

    /**
     * The Timer core pool size.
     */
    private Integer timerCorePoolSize;

    private OsyncTimer(Integer timerCorePoolSize) {
        this.timerCorePoolSize = timerCorePoolSize;
    }

    /**
     * Retrieve the global instance.
     *
     * @param timerCorePoolSize the timer core pool size
     * @return the instance
     */
    public static OsyncTimer getInstance(Integer timerCorePoolSize) {
        if (INSTANCE == null) {
            synchronized (OsyncTimer.class) {
                if (INSTANCE != null) {
                    return INSTANCE;
                }

                if (Objects.isNull(timerCorePoolSize)) {
                    timerCorePoolSize = ThreadPoolUtil.calculateCoreNum();
                }
                return INSTANCE = new OsyncTimer(timerCorePoolSize);

            }
        }
        return INSTANCE;
    }

    public static void reset() {
        ScheduledExecutor ex = INSTANCE.executor.getAndSet(null);
        if (ex != null && ex.getThreadPool() != null) {
            ex.getThreadPool().shutdownNow();
        }
    }

    public Reference<TimerListener> addTimerListener(final TimerListener listener) {
        startThreadIfNeeded();

        Runnable r = () -> {
            try {
                listener.tick();
            } catch (Exception e) {
                logger.error("", e);
            }
        };

        ScheduledFuture<?> f = executor.get().getThreadPool().scheduleAtFixedRate(r,
                listener.getIntervalTimeInMilliseconds(), listener.getIntervalTimeInMilliseconds(), TimeUnit.MILLISECONDS);
        return new TimerReference(listener, f);

    }

    /**
     * The type Timer reference.
     */
    public static class TimerReference extends SoftReference<TimerListener> {

        private final ScheduledFuture<?> f;

        /**
         * Instantiates a new Timer reference.
         *
         * @param referent the referent
         * @param f        the f
         */
        TimerReference(TimerListener referent, ScheduledFuture<?> f) {
            super(referent);
            this.f = f;
        }

        @Override
        public void clear() {
            super.clear();
            // stop this ScheduledFuture from any further executions
            f.cancel(false);
        }

    }

    /**
     * Start thread if needed.
     */
    protected void startThreadIfNeeded() {
        // create and start thread if one doesn't exist
        while (executor.get() == null || !executor.get().isInitialized()) {
            if (executor.compareAndSet(null, new ScheduledExecutor())) {
                // initialize the executor that we 'won' setting
                executor.get().initialize();
            }
        }
    }

    /**
     * The type Scheduled executor.
     */
    public class ScheduledExecutor {
        /**
         * The Executor.
         */
        volatile ScheduledThreadPoolExecutor executor;
        private volatile boolean initialized;

        /**
         * We want this only done once when created in compareAndSet so use an initialize method
         */
        public void initialize() {

            ThreadFactory threadFactory = new ThreadFactory() {
                final AtomicInteger counter = new AtomicInteger();

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "OsyncTimer-" + counter.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }

            };
            executor = new ScheduledThreadPoolExecutor(timerCorePoolSize, threadFactory);
            initialized = true;
        }

        /**
         * Gets thread pool.
         *
         * @return the thread pool
         */
        public ScheduledThreadPoolExecutor getThreadPool() {
            return executor;
        }

        /**
         * Is initialized boolean.
         *
         * @return the boolean
         */
        public boolean isInitialized() {
            return initialized;
        }
    }

    /**
     * The interface Timer listener.
     */
    public interface TimerListener {

        /**
         * Tick.
         *
         * @throws ExecutionException   the execution exception
         * @throws InterruptedException the interrupted exception
         * @throws TimeoutException     the timeout exception
         */
        void tick() throws ExecutionException, InterruptedException, TimeoutException;

        /**
         * Gets interval time in milliseconds.
         *
         * @return the interval time in milliseconds
         */
        int getIntervalTimeInMilliseconds();
    }

}
