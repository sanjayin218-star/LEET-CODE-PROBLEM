import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

class FizzBuzz {
    private int n;
    private int current = 1;

    private Semaphore fizz = new Semaphore(0);
    private Semaphore buzz = new Semaphore(0);
    private Semaphore fizzbuzz = new Semaphore(0);
    private Semaphore number = new Semaphore(1);

    public FizzBuzz(int n) {
        this.n = n;
    }

    public void fizz(Runnable printFizz) throws InterruptedException {
        while (true) {
            fizz.acquire();

            if (current > n)
                break;

            printFizz.run();
            current++;
            releaseNext();
        }
    }

    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (true) {
            buzz.acquire();

            if (current > n)
                break;

            printBuzz.run();
            current++;
            releaseNext();
        }
    }

    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (true) {
            fizzbuzz.acquire();

            if (current > n)
                break;

            printFizzBuzz.run();
            current++;
            releaseNext();
        }
    }

    public void number(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            number.acquire();

            if (current > n) {
                fizz.release();
                buzz.release();
                fizzbuzz.release();
                break;
            }

            if (current % 3 != 0 && current % 5 != 0) {
                printNumber.accept(current);
                current++;
            }

            releaseNext();
        }
    }

    private void releaseNext() {
        if (current > n) {
            fizz.release();
            buzz.release();
            fizzbuzz.release();
            number.release();
        } else if (current % 15 == 0) {
            fizzbuzz.release();
        } else if (current % 3 == 0) {
            fizz.release();
        } else if (current % 5 == 0) {
            buzz.release();
        } else {
            number.release();
        }
    }
}
