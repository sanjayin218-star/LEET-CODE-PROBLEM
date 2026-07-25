import java.util.LinkedList;

class MyHashSet {

    private final int SIZE = 1000;
    private LinkedList<Integer>[] buckets;

    public MyHashSet() {
        buckets = new LinkedList[SIZE];
    }

    private int hash(int key) {
        return key % SIZE;
    }

    public void add(int key) {
        int index = hash(key);

        if (buckets[index] == null) {
            buckets[index] = new LinkedList<>();
        }

        if (!buckets[index].contains(key)) {
            buckets[index].add(key);
        }
    }

    public void remove(int key) {
        int index = hash(key);

        if (buckets[index] != null) {
            buckets[index].remove(Integer.valueOf(key));
        }
    }

    public boolean contains(int key) {
        int index = hash(key);

        return buckets[index] != null && buckets[index].contains(key);
    }
}
