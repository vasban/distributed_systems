package worker;

import util.Constants;

public class
WorkerFactory {

    private final int workerAmount;

    public WorkerFactory(int workerAmount) {
        this.workerAmount = workerAmount;
    }

    public void createWorkers() {
        for (int i = 0; i < this.workerAmount; i++) {
            Worker worker = new Worker(Constants.DEFAULT_WORKER_PORT + i);
            System.out.println("Initiating worker on port " + worker.getPort());
            worker.start();
        }
    }
}
