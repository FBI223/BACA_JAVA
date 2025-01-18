package uj.wmii.pwj.exec;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ExecServiceTest {

    @Test
    void testExecute() {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        s.execute(r);
        doSleep(50);
        assertTrue(r.wasRun);
    }

    @Test
    void testScheduleRunnable() {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        s.submit(r);
        doSleep(10);
        assertTrue(r.wasRun);
    }

    @Test
    void testScheduleRunnableWithReturn() throws ExecutionException, InterruptedException {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        Future<String> futurama =  s.submit(r,"siema");
        doSleep(10);
        assertTrue(r.wasRun);
        assertSame("siema", futurama.get());
    }

    @Test
    void testScheduleRunnableWithResult() throws Exception {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        Object expected = new Object();
        Future<Object> f = s.submit(r, expected);
        doSleep(10);
        assertTrue(r.wasRun);
        assertTrue(f.isDone());
        assertEquals(expected, f.get());
    }

    @Test
    void testScheduleCallable() throws Exception {
        MyExecService s = MyExecService.newInstance();
        StringCallable c = new StringCallable("X", 10);
        Future<String> f = s.submit(c);
        doSleep(20);
        assertTrue(f.isDone());
        assertEquals("X", f.get());
    }

    @Test
    void testShutdownExecutionAfterShutdown() {
        ExecutorService s = MyExecService.newInstance();
        s.execute(new TestRunnable());
        doSleep(10);
        s.shutdown();
        assertThrows(RejectedExecutionException.class, () -> s.submit(new TestRunnable()));
        assertTrue(s.isShutdown() && s.isTerminated());
    }

    @Test
    void testAutomaticShutdownAfterAllTasksExecuted() {
        ExecutorService s = MyExecService.newInstance();
        s.execute(new TestRunnable());
        s.execute(new TestRunnable());
        s.execute(new TestRunnable());
        doSleep(10);
        s.shutdown();
        doSleep(20);
        assertTrue(s.isShutdown() && s.isTerminated());

    }

    @Test
    void testShutdownWithManyTaskToDoAfterShutdown() {
        ExecutorService s = MyExecService.newInstance();
        doSleep(10);
        for ( int i  = 0 ; i < 10 ; i++)
        {
            s.execute(new TestRunnableWithPause()) ;
        }

        s.shutdown();
        doSleep(70);
        List<Runnable> lista = s.shutdownNow();
        assertTrue(lista.isEmpty());
        assertTrue(s.isShutdown() && s.isTerminated());
    }


    @Test
    void testShutdownNow() {
        ExecutorService s = MyExecService.newInstance();
        s.execute(new TestRunnable());
        doSleep(10);
        s.shutdownNow();
        assertThrows(RejectedExecutionException.class, () -> s.submit(new TestRunnable()));
        assertTrue(s.isShutdown() && s.isTerminated());
    }


    @Test
    void testShutdownNowWithRemainingTasks() {
        ExecutorService s = MyExecService.newInstance();
        doSleep(10);

        for ( int i  = 0 ; i < 1000 ; i++)
        {
            s.execute(new TestRunnableWithPause());
        }

        List<Runnable> list = s.shutdownNow();
        assertThrows(RejectedExecutionException.class, () -> s.submit(new TestRunnable()));
        assertFalse(list.isEmpty());
    }

    @Test
    void testShutdownNowWithReturnedList() {
        ExecutorService s = MyExecService.newInstance();
        doSleep(10);

        TestRunnableInfinite r0 = new TestRunnableInfinite();
        s.execute(r0);

        TestRunnable r1 = new TestRunnable();
        TestRunnable r2 = new TestRunnable();
        TestRunnable r3 = new TestRunnable();
        s.execute(r1);
        s.execute(r2);
        s.execute(r3);
        doSleep(10);

        List<Runnable> list = s.shutdownNow();
        assertThrows(RejectedExecutionException.class, () -> s.submit(new TestRunnable()));
        assertEquals(3, list.size());

    }

    @Test
    void testShutdownNowWithInifniteRunnable() {
        ExecutorService s = MyExecService.newInstance();
        doSleep(10);

        TestRunnableInfiniteInterrupted r0 = new TestRunnableInfiniteInterrupted();
        s.execute(r0);
        doSleep(10);
        List<Runnable> list = s.shutdownNow();
        assertTrue(list.isEmpty());
        assertTrue( s.isTerminated() );

    }




    @Test
    void testIsShutdown() {
        ExecutorService s = MyExecService.newInstance();
        s.shutdown();
        doSleep(5);
        assertTrue(s.isShutdown());
    }


    @Test
    void testIsTerminated() {
        ExecutorService s = MyExecService.newInstance();
        s.shutdown();
        doSleep(5);
        assertTrue(s.isTerminated());
    }


    @Test
    void testNewInstance() {
        ExecutorService s = MyExecService.newInstance();
        assertFalse(s.isShutdown());
    }



    @Test
    void awaitTerminationBasic() throws InterruptedException {
        ExecutorService s = MyExecService.newInstance();
        s.shutdown();
        boolean is_terminated =  s.awaitTermination(10, TimeUnit.MILLISECONDS);
        assertTrue(is_terminated );
    }

    @Test
    void awaitTerminationWithTaskToDo() throws InterruptedException {
        ExecutorService s = MyExecService.newInstance();

        for ( int i  = 0 ; i < 10000 ; i++)
        {
            s.execute(new TestRunnable());
        }
        s.shutdown();
        boolean is_terminated =  s.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertTrue(is_terminated );
    }

    @Test
    void awaitTerminationUnsuccesful() throws InterruptedException {
        ExecutorService s = MyExecService.newInstance();

        for ( int i  = 0 ; i < 50 ; i++)
        {
            s.execute(new TestRunnableWithPause());
        }
        s.shutdown();
        boolean is_terminated =  s.awaitTermination(1, TimeUnit.MILLISECONDS);
        assertFalse(is_terminated );
    }



    @Test
    void awaitTerminationManyTimes() throws InterruptedException {
        ExecutorService s = MyExecService.newInstance();

        for ( int i  = 0 ; i < 20 ; i++)
        {
            s.execute(new TestRunnableWithPause() );
        }
        s.shutdown();
        boolean is_terminated1 =  s.awaitTermination(1, TimeUnit.NANOSECONDS);
        boolean is_terminated2 =  s.awaitTermination(1, TimeUnit.NANOSECONDS);
        boolean is_terminated3 =  s.awaitTermination(150, TimeUnit.MILLISECONDS);


        assertTrue( !is_terminated1 && !is_terminated2 && is_terminated3 );
    }



    @Test
    void InvokeAllCallableList() throws InterruptedException, ExecutionException {
        ExecutorService s = MyExecService.newInstance();
        List<StringCallable> lista_callable = new ArrayList<StringCallable>();

        int n_of_tasks = 50;
        for (int i = 0; i < n_of_tasks; i++)
        {
            lista_callable.add(new StringCallable( "czesc" , 1));
        }

        List<Future<String>> lista_futurama_string = s.invokeAll(lista_callable);

        boolean wynik_testu = true;
        for ( Future<String> futurama : lista_futurama_string   )
        {
            if (!Objects.equals(futurama.get(), "czesc") || futurama.isCancelled()   )
            {
                wynik_testu = false;
            }
        }

        assertTrue(wynik_testu);

    }



    @Test
    void NotInvokeAllCallableListWithTimeLimit() throws InterruptedException {
        ExecutorService s = MyExecService.newInstance();
        List<BoleanCallableTimeout> lista_callable = new ArrayList<BoleanCallableTimeout>();

        int n_of_tasks = 1000;
        for (int i = 0; i < n_of_tasks; i++)
        {
            lista_callable.add(new BoleanCallableTimeout(  10));
        }
        List<Future<Boolean>> lista_futurama_string = s.invokeAll(lista_callable,100,TimeUnit.MILLISECONDS);


        int n_interrupted = 0 ;
        boolean is_task_interrupted = false;
        for ( Future<Boolean> futurama : lista_futurama_string )
        {
            if ( futurama.isCancelled() )
            {
                is_task_interrupted = true;
                n_interrupted++;
            }
        }

        is_task_interrupted = n_interrupted > n_of_tasks/2;
        assertTrue(is_task_interrupted);
    }


    @Test
    void TryToInvokeAllCallableListWithTimeLimit() throws InterruptedException {
        ExecutorService s = MyExecService.newInstance();
        List<BoleanCallableTimeout> lista_callable = new ArrayList<BoleanCallableTimeout>();

        int n_of_tasks = 50;
        for (int i = 0; i < n_of_tasks; i++)
        {
            lista_callable.add(new BoleanCallableTimeout(  1));
        }
        List<Future<Boolean>> lista_futurama_string = s.invokeAll(lista_callable,100,TimeUnit.MILLISECONDS);
        int tasks_done = 0 ;
        boolean are_tasks_done = true;
        for ( Future<Boolean> futurama : lista_futurama_string )
        {
            if ( futurama.isCancelled() )
            {
                are_tasks_done = false;
            }

            if (futurama.isDone() )
            {
                tasks_done++;
            }
        }
        are_tasks_done = tasks_done == n_of_tasks;
        assertTrue(are_tasks_done);

    }



    @Test
    void InvokeAnyCallableList() throws InterruptedException, ExecutionException {
        ExecutorService s = MyExecService.newInstance();
        List<StringCallable> lista_callable = new ArrayList<StringCallable>();

        int n_of_tasks = 50;
        for (int i = 0; i < n_of_tasks; i++)
        {
            lista_callable.add(new StringCallable( "czesc" , 1));
        }

        String futurama_string = s.invokeAny(lista_callable);
        assertSame("czesc", futurama_string);

    }



    @Test
    void NotInvokeAnyCallableListWithTimeLimit() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService s = MyExecService.newInstance();
        List<BoleanCallableTimeout> lista_callable = new ArrayList<BoleanCallableTimeout>();

        int n_of_tasks = 1000;
        for (int i = 0; i < n_of_tasks; i++)
        {
            lista_callable.add(new BoleanCallableTimeout(  1000));
        }
        Boolean futurama_bool = s.invokeAny(lista_callable,1,TimeUnit.MILLISECONDS);
        assertTrue(futurama_bool == null);

    }


    @Test
    void TryToInvokeAnyCallableListWithTimeLimit() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService s = MyExecService.newInstance();
        List<BoleanCallableTimeout> lista_callable = new ArrayList<BoleanCallableTimeout>();

        int n_of_tasks = 50;
        for (int i = 0; i < n_of_tasks; i++)
        {
            lista_callable.add(new BoleanCallableTimeout(  1));
        }
        Boolean futurama_bool = s.invokeAny(lista_callable,100,TimeUnit.MILLISECONDS);

        assertNotNull(futurama_bool);
    }


    @Test
    void AllTestsCombined () throws InterruptedException, ExecutionException
        {

            doSleep(5);

        ExecutorService s = MyExecService.newInstance();

        List<BoleanCallableTimeout> lista_callable1 = new ArrayList<BoleanCallableTimeout>();
        List<BoleanCallable> lista_callable2 = new ArrayList<BoleanCallable>();

        int n_of_tasks = 100;
        for (int i = 0; i < n_of_tasks; i++)
        {
            lista_callable1.add(new BoleanCallableTimeout( 1));
        }

        for (int i = 0; i < n_of_tasks; i++)
        {
            lista_callable2.add(new BoleanCallable( ));
        }

        Boolean futurama1 = s.invokeAny(lista_callable1) ;
        boolean wykonane_jakiekolwiek_1 = futurama1;


        List< Future<Boolean> > futurama2 = s.invokeAll(lista_callable2, 100,TimeUnit.MILLISECONDS);
        boolean wykonane_wszystkie_2 = true ;
        for ( Future<Boolean> future : futurama2 )
        {
            if ( !future.get().booleanValue() )
            {
                wykonane_wszystkie_2 = false;
            }
        }


        List<Runnable> lista_runnable_to_do =  s.shutdownNow();
        boolean awaiting_termination_true = s.awaitTermination(100, TimeUnit.MILLISECONDS);
        boolean is_list_empty = lista_runnable_to_do.isEmpty();

        doSleep(5);
        assertTrue( wykonane_jakiekolwiek_1 && wykonane_wszystkie_2 && is_list_empty && awaiting_termination_true );
    }




    static void doSleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}



class BoleanCallable implements Callable<Boolean> {

    private Boolean result;

    BoleanCallable() {
        this.result = false;
    }

    @Override
    public Boolean call() throws Exception {
        result = true;
        return true;
    }
}


class BoleanCallableTimeout implements Callable<Boolean> {

    private Boolean result;
    private int milis;

    BoleanCallableTimeout(int milis) {
        this.result = false;
        this.milis = milis;
    }

    @Override
    public Boolean call() throws Exception {
        result = true;
        ExecServiceTest.doSleep(milis);
        return true;
    }
}

class StringCallable implements Callable<String> {

    private final String result;
    private final int milis;

    StringCallable(String result, int milis) {
        this.result = result;
        this.milis = milis;
    }

    @Override
    public String call() throws Exception {
        ExecServiceTest.doSleep(milis);
        return result;
    }
}

class TestRunnableWithPause implements Runnable {

    boolean wasRun;
    @Override
    public void run() {
        wasRun = true;
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}



class TestRunnable implements Runnable {

    boolean wasRun;
    @Override
    public void run() {
        wasRun = true;
    }
}

class TestRunnableInfinite implements Runnable {

    boolean wasRun;
    @Override
    public void run() {
        wasRun = true;
        while(true)
        {
            wasRun = true;
        }
    }
}


class TestRunnableInfiniteInterrupted implements Runnable {

    boolean wasRun;
    @Override
    public void run() {
        wasRun = true;
        while(!Thread.currentThread().isInterrupted())
        {
            wasRun = true;
        }

    }
}