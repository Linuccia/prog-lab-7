package Commands;

import DataClasses.Product;
import ProgramManager.CollectionManager;
import ProgramManager.Database;
import ProgramManager.Sender;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

public class AddIfMin extends AbsCommand {
    private CollectionManager manager;
    private Database database;

    public AddIfMin(CollectionManager manager, Database database){
        this.manager = manager;
        this.database = database;
    }

    /**
     * Метод для добавления элемента в коллекцию, если его price меньше минимального
     *
     * @param commandPool
     * @param sendPool
     * @param key
     */
    @Override
    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable addifmin = () -> {
            if (!(manager.getCollection().size() == 0)) {
                Stream<Product> stream = manager.getCollection().stream();
                Integer minPrice = stream.filter(collection -> collection.getPrice() != null).min(Comparator.comparingInt(p -> p.getPrice())).get().getPrice();
                try {
                    if (command.getProduct().getPrice() >= minPrice) {
                        sendPool.submit(new Sender(key, "Price данного элемента больше или равен минимальному. Элемент не сохранен"));
                    } else {
                        Integer id = database.getIdSeq();
                        command.getProduct().setId(id);
                        command.getProduct().setLogin(command.getLogin());
                        manager.getCollection().add(command.getProduct());
                        database.add(command.getProduct(), id, command.getLogin());
                        sendPool.submit(new Sender(key, "Элемент коллекции успешно добавлен"));
                    }
                } catch (NullPointerException e) {
                    sendPool.submit(new Sender(key, "Ошибка в аргументах для команды add_if_min"));
                } catch (SQLException e){
                    sendPool.submit(new Sender(key, "Ошибка при работе с базой данных"));
                }
            } else {
                sendPool.submit(new Sender(key, "Коллекция пуста"));
            }
        };
        commandPool.execute(addifmin);
    }
}
