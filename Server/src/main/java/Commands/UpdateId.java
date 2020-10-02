package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UpdateId extends AbsCommand {
    private CollectionManager manager;
    private Database database;
    private Lock lock = new ReentrantLock();

    public UpdateId(CollectionManager manager, Database database){
        this.manager = manager;
        this.database = database;
    }

    /**
     * Метод обновляет элемент с введенным id
     *
     * @param commandPool
     * @param sendPool
     * @param key
     * @param args
     * @param product
     * @param login
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Integer args, Product product, String login) {
        Runnable update = () -> {
            lock.lock();
            try {
                if (!(manager.collection.size() == 0)) {
                    try {
                        database.updateById(args, login);
                        if (manager.collection.removeIf(collection -> collection.getId().equals(args) && collection.getLogin().equals(login))) {
                            product.setId(args);
                            product.setLogin(login);
                            manager.collection.add(product);
                            sendPool.submit(new Sender(key, "Элемент с данным id успешно обновлен"));
                        } else {
                            sendPool.submit(new Sender(key, "Элемента с данным id нет, либо у вас нет доступа к этому элементу"));
                        }
                    } catch (SQLException e) {
                        sendPool.submit(new Sender(key, "Ошибка при работе с базой данных"));
                    }
                } else {
                    sendPool.submit(new Sender(key, "Коллекция пуста"));
                }
            } finally {
                lock.unlock();
            }
        };
        commandPool.execute(update);
    }
}
