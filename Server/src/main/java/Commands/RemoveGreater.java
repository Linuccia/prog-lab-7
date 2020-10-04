package Commands;

import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class RemoveGreater extends AbsCommand {
    private CollectionManager manager;
    private Database database;

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
     */
    @Override
    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable removegreater = () -> {
            manager.lock.lock();
            try {
                if (!(manager.collection.size() == 0)) {
                    try {
                        database.deleteGreater(command.getArg(), command.getLogin());
                        int oldSize = manager.collection.size();
                        if (manager.collection.removeIf(collection -> collection.getPrice() > command.getArg() && collection.getLogin().equals(command.getLogin()))) {
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
                manager.lock.unlock();
            }
        };
        commandPool.execute(removegreater);
    }
}
