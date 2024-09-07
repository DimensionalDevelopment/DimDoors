package org.dimdev.dimdoors.api.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.ReportedException;
import net.minecraft.server.Bootstrap;

/**
 * Utility class for processing data in parallel.
 */
public class StreamUtils {

    private static final Logger LOGGER = LogManager.getLogger(StreamUtils.class);
    private static final AtomicInteger POOL_THREAD_COUNTER = new AtomicInteger();
    private static ForkJoinPool POOL = null;

    /**
     * Sets up the fork join pool to execute parallel Streams on.
     *
     * @param api The api to run from. Uses the given instances class loader as the class loader for the pool.
     */
    public static void setup(Object classCtx) {
        final ClassLoader classLoader = classCtx.getClass().getClassLoader();
        POOL = new ForkJoinPool(Math.max(4, Runtime.getRuntime().availableProcessors() - 4), forkJoinPool -> {
            final ForkJoinWorkerThread thread = new ForkJoinWorkerThread(forkJoinPool){};
            thread.setContextClassLoader(classLoader);
            thread.setName(String.format("FastSuite Recipe Lookup Thread: %s", POOL_THREAD_COUNTER.incrementAndGet()));
            return thread;
        }, StreamUtils::onThreadException, true);
    }



    /**
     * Handles an exception on the worker thread.
     *
     * @param thread The thread.
     * @param cause  The exception.
     */
    private static void onThreadException(Thread thread, Throwable cause) {
        if (cause instanceof CompletionException) {
            cause = cause.getCause();
        }

        if (cause instanceof ReportedException) {
            Bootstrap.realStdoutPrintln(((ReportedException) cause).getReport().getFriendlyReport());
            System.exit(-1);
        }

        LOGGER.error(String.format("Caught exception in thread %s", thread), cause);
    }

    /**
     * Executes a task on the pool.
     * This needs to be used if a parallel stream is involved, since else class loading can break.
     *
     * @param runnable The task.
     */
    public static void execute(final Runnable runnable) {
        if (POOL == null) throw new IllegalStateException("Tried to run a task in parallel before FastSuite has been initialized!");
        POOL.invoke(new RunnableExecuteAction(runnable));
    }

    /**
     * Executes a task on the pool.
     * This needs to be used if a parallel stream is involved, since else class loading can break.
     *
     * @param callable The task.
     */
    public static <T> T execute(final Callable<T> callable) {
        if (POOL == null) throw new IllegalStateException("Tried to run a task in parallel before FastSuite has been initialized!");
        return POOL.invoke(new CallableExecuteAction<>(callable));
    }

    /**
     * Executes a task on the pool with a specified max time limit.
     * This needs to be used if a parallel stream is involved, since else class loading can break.
     *
     * @param callable   The task.
     * @param maxTime    The max time the task may take.
     * @param unit       The unit of the max time.
     * @param fallback   The object that will be returned in case of a time-based failure.
     * @param timeoutMsg An error message that will be sent on timeout.
     */
    public static <T> T executeUntil(final Callable<T> callable, long maxTime, TimeUnit unit, T fallback, Supplier<String> timeoutMsg) {
        if (POOL == null) throw new IllegalStateException("Tried to run a task in parallel before FastSuite has been initialized!");
        var task = POOL.submit(new CallableExecuteAction<>(callable));
        try {
            return task.get(maxTime, unit);
        }
        catch (InterruptedException | TimeoutException ex) {
            /*FastSuite.LOGGER.error(*/System.out.println(timeoutMsg.get());
            ex.printStackTrace();
            dumpFSThreads();
            return fallback;
        }
        catch (ExecutionException e) {
            /*FastSuite.LOGGER.error(*/System.out.println("Exception during multithreaded recipe lookup");
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
    }

    public static void dumpFSThreads() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] infos = bean.dumpAllThreads(true, true);
        /*DimensionalDoors.LOGGER.debug(*/System.out.println(Arrays.stream(infos).filter(info -> info.getThreadName().startsWith("FastSuite")).map(Object::toString).collect(Collectors.joining()));
    }

    public static <M> CompletableFuture<M> supplyAsync(Supplier<M> supplier) {
        return CompletableFuture.supplyAsync(supplier, POOL);
    }

    private static final class RunnableExecuteAction extends ForkJoinTask<Void> {
        final Runnable runnable;

        private RunnableExecuteAction(Runnable runnable) {
            Validate.notNull(runnable);
            this.runnable = runnable;
        }

        @Override
        public Void getRawResult() {
            return null;
        }

        @Override
        public void setRawResult(Void v) {}

        @Override
        public boolean exec() {
            this.runnable.run();
            return true;
        }
    }

    private static final class CallableExecuteAction<T> extends ForkJoinTask<T> {
        final Callable<T> callable;
        T rawResult;

        private CallableExecuteAction(Callable<T> callable) {
            Validate.notNull(callable);
            this.callable = callable;
        }

        @Override
        public T getRawResult() {
            return this.rawResult;
        }

        @Override
        public void setRawResult(T v) {
            this.rawResult = v;
        }

        @Override
        public boolean exec() {
            try {
                this.setRawResult(this.callable.call());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }
}