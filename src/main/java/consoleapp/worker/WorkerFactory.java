package worker;

public class WorkerFactory {

    private static final int DEFAULT_PORT = 51000;

    private final int workerAmount;

    public static void main(String[] args) {
        int workerAmount = Integer.parseInt(args[0]);
        WorkerFactory workerFactory = new WorkerFactory(workerAmount);
        workerFactory.createWorkers();
    }

    public WorkerFactory(int workerAmount) {
        this.workerAmount = workerAmount;
    }

    private void createWorkers() {
        for (int i = 0; i < this.workerAmount; i++) {
            Worker worker = new Worker(DEFAULT_PORT + i);
            worker.start();
        }
    }
}
