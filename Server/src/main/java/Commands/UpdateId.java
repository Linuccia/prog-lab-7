package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class UpdateId extends AbsCommand {
    private CollectionManager manager;
    private Database database;

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
     */
    @Override
    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable update = () -> {
            if (!(manager.getCollection().size() == 0)) {
                try {
                    database.updateById(command.getArg(), command.getLogin());
                    if (manager.getCollection().removeIf(collection -> collection.getId().equals(command.getArg()) && collection.getLogin().equals(command.getLogin()))) {
                        command.getProduct().setId(command.getArg());
                        command.getProduct().setLogin(command.getLogin());
                        manager.getCollection().add(command.getProduct());
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
        };
        commandPool.execute(update);
    }
}
