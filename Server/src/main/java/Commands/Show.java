package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Show extends AbsCommand {
    private CollectionManager manager;

    public Show(CollectionManager manager){
        this.manager = manager;
    }

    /**
     * Метод выводит все элементы коллекции
     *
     * @param commandPool
     * @param sendPool
     * @param key
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable show = () -> {
            if (manager.collection.size() != 0) {
                Stream<Product> stream = manager.collection.stream();
                sendPool.submit(new Sender(key, stream.map(Product::toString).collect(Collectors.joining("\n"))));
            } else {
                sendPool.submit(new Sender(key, "Коллекция пуста."));
            }
        };
        commandPool.execute(show);
    }
}
