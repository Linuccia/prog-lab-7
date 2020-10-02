package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RemoveFirst extends AbsCommand {
    private CollectionManager manager;
    private Database database;
    private Lock lock = new ReentrantLock();

    public RemoveFirst(CollectionManager manager, Database database){
        this.manager = manager;
        this.database = database;
    }

    /**
     * Метод удаляет первый элемент коллекции
     *
     * @param commandPool
     * @param sendPool
     * @param key
     * @param login
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, String login) {
        Runnable removefirst = () -> {
            lock.lock();
            try {
                try{
                    if (!(manager.collection.size() == 0)){
                        PriorityQueue<Product> colCopy = new PriorityQueue<>();
                        for (Product p: manager.collection) {
                            if (p.getLogin().equals(login)) {
                                colCopy.add(p);
                            }
                        }
                        if (!(colCopy.size() == 0)) {
                            Product del = colCopy.element();
                            database.deleteById(del.getId(), login);
                            manager.collection.removeIf(collection -> collection.getId().equals(del.getId()));
                            sendPool.submit(new Sender(key, "Первый элемент коллекции из созданных вами удален"));
                        } else {
                            sendPool.submit(new Sender(key, "Пока что нет ни одного созданного вами элемента"));
                        }
                    } else {
                        sendPool.submit(new Sender(key, "Коллекция пуста"));
                    }
                } catch (SQLException e){
                    sendPool.submit(new Sender(key, "Ошибка при работе с базой данных"));
                }
            } finally {
                lock.unlock();
            }
        };
        commandPool.execute(removefirst);
    }
}
