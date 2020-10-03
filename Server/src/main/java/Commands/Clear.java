package Commands;

import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;

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
     * @param login
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, String login){
        Runnable clear = () ->{
            try {
                database.clear(login);
                if (manager.getCollection().removeIf(col -> col.getLogin().equals(login))) {
                    sendPool.submit(new Sender(key, "Удалены все созданные вами элементы"));
                } else {
                    sendPool.submit(new Sender(key, "В коллекции отсуствуют созданные вами элементы"));
                }
            } catch (SQLException e) {
                sendPool.submit(new Sender(key, "Ошибка при работе с базой данных"));
            }
        };
        commandPool.execute(clear);
    }
}
