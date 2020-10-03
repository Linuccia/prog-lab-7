package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Sender;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

public class CountLessThanPrice extends AbsCommand {
    private CollectionManager manager;

    public CountLessThanPrice(CollectionManager manager){
        this.manager = manager;
    }

    /**
     * Метод считает количество элементов, значение price которых меньше введенного
     *
     * @param commandPool
     * @param sendPool
     * @param key
     */
    @Override
    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable countlessthanprice = () -> {
            if (!(manager.collection.size() == 0)) {
                //int count = 0;
                //for (Product p: manager.getCollection()){ if (p.getPrice() < args) { count ++; } }
                Stream<Product> stream = manager.collection.stream();
                long count = stream.filter(collection -> collection.getPrice() < command.getArg()).count();
                sendPool.submit(new Sender(key, "Найдено " + count + " элемент(а/ов), значение цены котор(ых/ого) меньше " + command.getArg()));
            } else {
                sendPool.submit(new Sender(key, "Коллекция пуста"));
            }
        };
        commandPool.execute(countlessthanprice);
    }
}
