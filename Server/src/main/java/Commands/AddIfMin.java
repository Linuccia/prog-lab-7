package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class AddIfMin extends AbsCommand {
    private CollectionManager manager;
    private Database database;
    private Lock lock = new ReentrantLock();

    public AddIfMin(CollectionManager manager, Database database){
        this.manager = manager;
        this.database = database;
    }

    /**
     * Метод для добавления элемента в коллекцию, если его price меньше минимального
     *
     * @param commandPool
     * @param sendPool
     * @param key
     * @param product
     * @param login
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Product product, String login) {
        Runnable addifmin = () -> {
            lock.lock();
            try {
                if (!(manager.collection.size() == 0)) {
                    Stream<Product> stream = manager.collection.stream();
                    Integer minPrice = stream.filter(collection -> collection.getPrice() != null).min(Comparator.comparingInt(p -> p.getPrice())).get().getPrice();
                    try {
                        if (product.getPrice() >= minPrice) {
                            sendPool.submit(new Sender(key, "Price данного элемента больше или равен минимальному. Элемент не сохранен"));
                        } else {
                            Integer id = database.getIdSeq();
                            product.setId(id);
                            product.setLogin(login);
                            manager.collection.add(product);
                            database.add(product, id, login);
                            sendPool.submit(new Sender(key, "Элемент коллекции успешно добавлен"));
                        }
                    } catch (NullPointerException e) {
                        sendPool.submit(new Sender(key, "Ошибка в аргументах для команды add_if_min"));
                    } catch (SQLException e){
                        sendPool.submit(new Sender(key, "Ошибка при работе с базой данных"));
                    }
                } else {
                    sendPool.submit(new Sender(key, "Коллекция пуста"));
                }
            } finally {
                lock.unlock();
            }
        };
        commandPool.execute(addifmin);
    }
}
