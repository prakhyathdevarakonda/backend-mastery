import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public class RaceConditionLab {
    private static final int THREADS = 100;
    private static final int INCREMENTS_PER_THREAD = 10_000;
    private static final int EXPECTED = THREADS * INCREMENTS_PER_THREAD;

    public static void main(String[] args) throws InterruptedException {
        runScenario("unsafe-int", new UnsafeCounter());
        runScenario("synchronized-int", new SynchronizedCounter());
        runScenario("atomic-integer", new AtomicCounter());
        runScenario("long-adder", new LongAdderCounter());
    }

    private static void runScenario(String name, Counter counter) throws InterruptedException {
        long start = System.nanoTime();
        List<Thread> workers = new ArrayList<>(THREADS);
        for (int i = 0; i < THREADS; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.increment();
                }
            });
            workers.add(t);
        }

        workers.forEach(Thread::start);
        for (Thread worker : workers) {
            worker.join();
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        int actual = counter.get();
        String verdict = actual == EXPECTED ? "OK" : "LOST_UPDATES";
        System.out.printf("%-16s expected=%d actual=%d elapsedMs=%d verdict=%s%n",
                name, EXPECTED, actual, elapsedMs, verdict);
    }

    private interface Counter {
        void increment();

        int get();
    }

    private static final class UnsafeCounter implements Counter {
        private int count;

        @Override
        public void increment() {
            count++;
        }

        @Override
        public int get() {
            return count;
        }
    }

    private static final class SynchronizedCounter implements Counter {
        private int count;

        @Override
        public synchronized void increment() {
            count++;
        }

        @Override
        public synchronized int get() {
            return count;
        }
    }

    private static final class AtomicCounter implements Counter {
        private final AtomicInteger count = new AtomicInteger();

        @Override
        public void increment() {
            count.incrementAndGet();
        }

        @Override
        public int get() {
            return count.get();
        }
    }

    private static final class LongAdderCounter implements Counter {
        private final LongAdder count = new LongAdder();

        @Override
        public void increment() {
            count.increment();
        }

        @Override
        public int get() {
            return count.intValue();
        }
    }
}
