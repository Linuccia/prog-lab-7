package Commands;

import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Clear extends AbsCommand {
    private CollectionManager manager;
    private Database database;
    private Lock lock = new ReentrantLock();

    public Clear(CollectionManager manager, Database database){
        this.manager = manager;
        this.database = database;
    }

    /**
     * Метод удаляет все элементы коллекции, созданные данным пользователем
     *
     * @param commandPool
     * @param sendPool
     * @param key
     * @param login
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, String login){
        Runnable clear = () ->{
            lock.lock();
            try {
                try {
                    database.clear(login);
                    if (manager.collection.removeIf(col -> col.getLogin().equals(login))) {
                        sendPool.submit(new Sender(key, "Удалены все созданные вами элементы"));
                    } else {
                        sendPool.submit(new Sender(key, "В коллекции отсуствуют созданные вами элементы"));
                    }
                } catch (SQLException e) {
                    sendPool.submit(new Sender(key, "Ошибка при работе с базой данных"));
                }
            } finally {
                lock.unlock();
            }
        };
        commandPool.execute(clear);
    }
}
