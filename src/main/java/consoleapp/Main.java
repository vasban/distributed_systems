import master.Master;
import reducer.Reducer;
import worker.WorkerFactory;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int workerAmount = Integer.parseInt(args[0]);

        WorkerFactory workerFactory = new WorkerFactory(workerAmount);
        workerFactory.createWorkers();

        Thread.sleep(1000);

        Master master = new Master(workerAmount);
        master.init();

        Thread.sleep(1000);

        Reducer reducer = new Reducer(workerAmount);
        reducer.init();
    }
}
