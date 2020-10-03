package Commands;

import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class RemoveById extends AbsCommand {
    private CollectionManager manager;
    private Database database;

    public RemoveById(CollectionManager manager, Database database){
        this.manager = manager;
        this.database = database;
    }

    /**
     * Метод удаляет элемент, id которого равен введенному
     *
     * @param commandPool
     * @param sendPool
     * @param key
     * @param args
     * @param login
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Integer args, String login) {
        Runnable removebyid = () -> {
            if (!(manager.getCollection().size() == 0)) {
                try {
                    database.deleteById(args, login);
                    if (manager.getCollection().removeIf(collection -> collection.getId().equals(args) && collection.getLogin().equals(login))) {
                        sendPool.submit(new Sender(key, "Элемент с данным id удален"));
                    } else
                        sendPool.submit(new Sender(key, "Элемента, созданного вами, с данным id не найдено"));
                } catch (SQLException e) {
                    sendPool.submit(new Sender(key, "Ошибка при работе с базой данных"));
                }
            } else {
                sendPool.submit(new Sender(key, "Коллекция пуста"));
            }
        };
        commandPool.execute(removebyid);
    }
}
