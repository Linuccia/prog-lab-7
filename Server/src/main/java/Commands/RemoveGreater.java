package Commands;

import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RemoveGreater extends AbsCommand {
    private CollectionManager manager;
    private Database database;
    private Lock lock = new ReentrantLock();

    public RemoveGreater(CollectionManager manager, Database database){
        this.manager = manager;
        this.database = database;
    }

    /**
     * Метод удаляет элементы коллекции, price которых больше введенного
     *
     * @param commandPool
     * @param sendPool
     * @param key
     * @param args
     * @param login
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Integer args, String login) {
        Runnable removegreater = () -> {
            lock.lock();
            try {
                if (!(manager.collection.size() == 0)) {
                    try {
                        database.deleteGreater(args, login);
                        int oldSize = manager.collection.size();
                        if (manager.collection.removeIf(collection -> collection.getPrice() > args && collection.getLogin().equals(login))) {
                            sendPool.submit(new Sender(key, "Был/о удален/о " + (oldSize - manager.collection.size()) + " элемент/ов коллекции"));
                        } else {
                            sendPool.submit(new Sender(key, "Ни одного элемента не удалено"));
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
        commandPool.execute(removegreater);
    }
}
