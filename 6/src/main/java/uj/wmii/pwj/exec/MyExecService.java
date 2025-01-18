//Marcin Sztukowski

package uj.wmii.pwj.exec;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class MyExecService implements ExecutorService  {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final Map<Worker, Boolean> workers = new ConcurrentHashMap<>();
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private final AtomicBoolean isTerminated = new AtomicBoolean(false);
    private static final Runnable POISON_PILL = () -> {};
    //private static int num_of_workers = Runtime.getRuntime().availableProcessors();
    private static final int num_of_workers = 1;

    private class Worker extends Thread {
        @Override
        public void run() {
            while (!isTerminated.get()) {
                try {
                    if (isShutdown.get() && taskQueue.isEmpty()) {
                        isTerminated.set(true);
                        break;
                    }
                    Runnable task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public MyExecService() {
        for (int i = 0; i < num_of_workers; i++) {
            Worker worker = new Worker();
            workers.put(worker, Boolean.TRUE);
            worker.start();
        }
    }


    @Override
    public void execute(Runnable command) {
        if ( !isTerminated.get() && !isShutdown.get() ) {
            taskQueue.add(command);
        } else
        {
            throw new RejectedExecutionException("queue is shut down");
        }
    }



    @Override
    public void shutdown() {
        isShutdown.set(true);
        for (Worker worker : workers.keySet()) {
            taskQueue.add(POISON_PILL);
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        isTerminated.set(true);
        isShutdown.set(true);

        List<Runnable> remainingTasks = new ArrayList<>();
        taskQueue.drainTo(remainingTasks);

        for ( Worker worker : workers.keySet() ) {
            worker.interrupt();
        }
        return remainingTasks;
    }


    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long timeoutMS = unit.toMillis(timeout);

        if ( isShutdown.get() )
        {
            Thread.sleep(timeoutMS);
            int active_workers = 0;
            for ( Worker worker : workers.keySet() ) {
                if ( worker.isAlive() )
                {
                    active_workers++;
                }
            }
            if ( active_workers == 0 )
            {
                isTerminated.set(true);
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {

        FutureTask<T> future = new FutureTask<>(task);
        execute(future);
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        FutureTask<T> future = new FutureTask<>(task,result);
        execute(future);
        return future;
    }

    @Override
    public Future<?> submit(Runnable task) {
        FutureTask<?> future = new FutureTask<>(task,null);
        execute(future);
        return future;
    }


    @Override
    public boolean isShutdown() {
        return isShutdown.get();
    }

    @Override
    public boolean isTerminated() {
        return isTerminated.get();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<Future<T>> futures = new ArrayList<>();

        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }

        for (Future<T> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return futures;
    }


    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        List<Future<T>> futures = new ArrayList<>();
        long endTime = System.nanoTime() + unit.toNanos(timeout);

        for (Callable<T> task : tasks) {
            long timeLeft = endTime - System.nanoTime();
            if (timeLeft <= 0) {
                break;
            }
            futures.add(submit(task));
        }

        long timeLeft = endTime - System.nanoTime();
        if (timeLeft > 0) {
            Thread.sleep(TimeUnit.NANOSECONDS.toMillis(timeLeft));
        }

        for (Future<T> future : futures) {
            if (!future.isDone() && !future.isCancelled()) {
                future.cancel(true);
            }
        }

        return futures;
    }


    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        List<Future<T>> futures = new ArrayList<>();
        T result = null;

        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }

        while ( result == null )
        {
            for (Future<T> future : futures) {
                if (future.isDone()) {
                    result = future.get();
                    break;
                }
            }
        }

        for (Future<T> future : futures) {
            if (!future.isDone() && !future.isCancelled()) {
                future.cancel(true);
            }
        }

        return result;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        List<Future<T>> futures = new ArrayList<>();
        T result = null;
        long endTime = System.nanoTime() + unit.toNanos(timeout);

        for (Callable<T> task : tasks) {
            long timeLeft = endTime - System.nanoTime();
            if (timeLeft <= 0) {
                break;
            }
            futures.add(submit(task));
        }

        long timeLeft = endTime - System.nanoTime();
        while ( result == null &&  timeLeft > 0  )
        {
            for (Future<T> future : futures) {
                if (future.isDone()) {
                    result = future.get();
                    break;
                }
            }
            timeLeft = endTime - System.nanoTime();
        }

        for (Future<T> future : futures) {
            if (!future.isDone() && !future.isCancelled()) {
                future.cancel(true);
            }
        }
        return result;
    }

    static MyExecService newInstance() {
        return new MyExecService();
    }
}
