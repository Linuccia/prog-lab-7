package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class Add extends AbsCommand {
    private CollectionManager manager;
    private Database database;

    public Add(CollectionManager manager, Database database){
        this.manager = manager;
        this.database = database;
    }

    /**
     * Метод для добавления элемента в коллекцию
     *
     * @param commandPool
     * @param sendPool
     * @param key
     */
    @Override
    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable add = () -> {
            try{
                Integer id = database.getIdSeq();
                command.getProduct().setId(id);
                command.getProduct().setLogin(command.getLogin());
                manager.getCollection().add(command.getProduct());
                database.add(command.getProduct(), id, command.getLogin());
                sendPool.submit(new Sender(key, "Элемент коллекции успешно добавлен"));
            } catch (SQLException e){
                sendPool.submit(new Sender(key, "Ошибка при работе с базой данных"));
            } catch (NullPointerException e){
                sendPool.submit(new Sender(key, "Ошибка в аргументах для команды add"));
            }
        };
        commandPool.execute(add);
    }
}
