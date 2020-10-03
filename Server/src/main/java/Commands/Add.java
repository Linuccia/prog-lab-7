package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;

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
     * @param product
     * @param login
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Product product, String login) {
        Runnable add = () -> {
            try{
                Integer id = database.getIdSeq();
                product.setId(id);
                product.setLogin(login);
                manager.getCollection().add(product);
                database.add(product, id, login);
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
