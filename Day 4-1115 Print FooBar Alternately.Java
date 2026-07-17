import java.util.concurrent.Semaphore;

class FooBar {
    private int n;
    private Semaphore fooSem = new Semaphore(1); // Start with foo permitted
    private Semaphore barSem = new Semaphore(0); // bar must wait initially

    public FooBar(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            fooSem.acquire(); // Wait for permission to print "foo"
            // printFoo.run() outputs "foo". Do not change or remove this line.
            printFoo.run();
            barSem.release(); // Signal the bar thread to proceed
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            barSem.acquire(); // Wait for permission to print "bar"
            // printBar.run() outputs "bar". Do not change or remove this line.
            printBar.run();
            fooSem.release(); // Signal the foo thread to proceed next
        }
    }
}
