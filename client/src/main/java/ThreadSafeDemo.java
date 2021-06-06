import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeDemo {
    //thread safe

    // private static int anInt = 0;
    private static AtomicInteger anInt = new AtomicInteger();

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<?>[]futures = new Future[10];

        for (int i = 0; i < 10; i++) {
            futures[i] = executorService.submit(() -> doSomeHeavyWork());
        }
        for (var f : futures ) {
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println(anInt.get());
    }

    private static void doSomeHeavyWork() {
        for (int i = 0; i < 10000; i++)
             anInt.incrementAndGet();
        System.out.println(Thread.currentThread().getName() + ":" + anInt.get());
    }
}
