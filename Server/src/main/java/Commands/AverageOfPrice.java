package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Sender;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

public class AverageOfPrice extends AbsCommand {
    private CollectionManager manager;

    public AverageOfPrice(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Метод выводит среднее значение price всех элементов коллекции
     *
     * @param command
     * @param commandPool
     * @param sendPool
     * @param key
     */
    @Override
    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable averageofprice = () -> {
            double averagePrice;
            if (!(manager.collection.size() == 0)) {
                Stream<Product> stream = manager.collection.stream();
                //for (Product p: manager.getCollection()){ averagePrice = averagePrice + p.getPrice(); }
                //int priceSum = stream.mapToInt(Product::getPrice).sum();
                averagePrice = (stream.mapToInt(Product::getPrice).sum()) / manager.collection.size();
                sendPool.submit(new Sender(key, "Среднее значение цены всех элементов коллекции - " + averagePrice));
            } else {
                sendPool.submit(new Sender(key, "Коллекция пуста"));
            }
        };
        commandPool.execute(averageofprice);
    }
}
