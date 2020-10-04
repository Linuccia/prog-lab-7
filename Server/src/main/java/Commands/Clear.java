package Commands;

import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class Clear extends AbsCommand {
    private CollectionManager manager;
    private Database database;

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
     */
    @Override
    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key){
        Runnable clear = () ->{
            manager.lock.lock();
            try {
                database.clear(command.getLogin());
                if (manager.collection.removeIf(col -> col.getLogin().equals(command.getLogin()))) {
                    sendPool.submit(new Sender(key, "Удалены все созданные вами элементы"));
                } else {
                    sendPool.submit(new Sender(key, "В коллекции отсуствуют созданные вами элементы"));
                }
            } catch (SQLException e) {
                sendPool.submit(new Sender(key, "Ошибка при работе с базой данных"));
            } finally {
                manager.lock.unlock();
            }
        };
        commandPool.execute(clear);
    }
}
