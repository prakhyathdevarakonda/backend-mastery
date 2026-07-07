import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadLocalLeakLab {
    private static final ThreadLocal<String> USER = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        System.out.println("=== Broken version (no cleanup) ===");
        runBrokenScenario();
        System.out.println();
        System.out.println("=== Fixed version (with remove) ===");
        runFixedScenario();
    }

    private static void runBrokenScenario() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        try {
            Future<?> requestA = pool.submit(() -> {
                USER.set("alice");
                System.out.println("Request A set USER=" + USER.get());
                // no cleanup on purpose
            });
            requestA.get();

            Future<?> requestB = pool.submit(() -> {
                String leaked = USER.get();
                System.out.println("Request B observed USER=" + leaked + " (should be null)");
            });
            requestB.get();
        } finally {
            pool.shutdownNow();
            USER.remove();
        }
    }

    private static void runFixedScenario() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        try {
            Future<?> requestA = pool.submit(() -> {
                try {
                    USER.set("alice");
                    System.out.println("Request A set USER=" + USER.get());
                } finally {
                    USER.remove();
                }
            });
            requestA.get();

            Future<?> requestB = pool.submit(() -> {
                String leaked = USER.get();
                System.out.println("Request B observed USER=" + leaked + " (expected null)");
            });
            requestB.get();
        } finally {
            pool.shutdownNow();
            USER.remove();
        }
    }
}
