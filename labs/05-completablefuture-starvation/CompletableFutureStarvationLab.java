import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletableFutureStarvationLab {
    public static void main(String[] args) {
        System.out.println("=== Broken pipeline (thenApply + join) ===");
        runBroken();
        System.out.println();
        System.out.println("=== Fixed pipeline (thenCompose) ===");
        runFixed();
    }

    private static void runBroken() {
        ExecutorService pool = newDaemonFixedPool(2, "broken-pool");
        try {
            CompletableFuture<String> requestA = brokenRequest("A", pool);
            CompletableFuture<String> requestB = brokenRequest("B", pool);
            CompletableFuture<Void> both = CompletableFuture.allOf(requestA, requestB);

            try {
                both.get(2, TimeUnit.SECONDS);
                System.out.println("Broken version unexpectedly completed");
            } catch (TimeoutException timeout) {
                System.out.println("Starvation detected: tasks timed out waiting on same exhausted pool");
                requestA.cancel(true);
                requestB.cancel(true);
            } catch (Exception ex) {
                System.out.println("Broken flow failed: " + ex.getMessage());
            }
        } finally {
            pool.shutdownNow();
        }
    }

    private static void runFixed() {
        ExecutorService pool = newDaemonFixedPool(2, "fixed-pool");
        try {
            CompletableFuture<String> requestA = fixedRequest("A", pool);
            CompletableFuture<String> requestB = fixedRequest("B", pool);
            CompletableFuture<Void> both = CompletableFuture.allOf(requestA, requestB);
            both.join();
            System.out.println("Fixed version completed: " + requestA.join() + ", " + requestB.join());
        } finally {
            pool.shutdownNow();
        }
    }

    private static ExecutorService newDaemonFixedPool(int size, String namePrefix) {
        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(namePrefix + "-" + thread.threadId());
            thread.setDaemon(true);
            return thread;
        };
        return Executors.newFixedThreadPool(size, factory);
    }

    private static CompletableFuture<String> brokenRequest(String name, ExecutorService pool) {
        return CompletableFuture
                .supplyAsync(() -> loadA(name), pool)
                .thenApply(value -> loadBAsync(value, pool).join());
    }

    private static CompletableFuture<String> fixedRequest(String name, ExecutorService pool) {
        return CompletableFuture
                .supplyAsync(() -> loadA(name), pool)
                .thenCompose(value -> loadBAsync(value, pool))
                .orTimeout(Duration.ofSeconds(2).toMillis(), TimeUnit.MILLISECONDS);
    }

    private static String loadA(String name) {
        sleep(200);
        return "loadA-" + name;
    }

    private static CompletableFuture<String> loadBAsync(String value, ExecutorService pool) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return "loadB(" + value + ")";
        }, pool);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
