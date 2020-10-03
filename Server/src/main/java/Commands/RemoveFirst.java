package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;

public class RemoveFirst extends AbsCommand {
    private CollectionManager manager;
    private Database database;

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
     */
    @Override
    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable removefirst = () -> {
            try{
                if (!(manager.getCollection().size() == 0)){
                    PriorityQueue<Product> colCopy = new PriorityQueue<>();
                    for (Product p: manager.getCollection()) {
                        if (p.getLogin().equals(command.getLogin())) {
                            colCopy.add(p);
                        }
                    }
                    if (!(colCopy.size() == 0)) {
                        Product del = colCopy.element();
                        database.deleteById(del.getId(), command.getLogin());
                        manager.getCollection().removeIf(collection -> collection.getId().equals(del.getId()));
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
        };
        commandPool.execute(removefirst);
    }
}
