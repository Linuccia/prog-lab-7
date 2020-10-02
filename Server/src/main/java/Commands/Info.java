package Commands;

import ProgramManager.CollectionManager;
import ProgramManager.Sender;

import java.nio.channels.SelectionKey;
import java.util.Date;
import java.util.concurrent.ExecutorService;


public class Info extends AbsCommand {
    private CollectionManager manager;
    private Date initDate = new Date();

    public Info(CollectionManager manager){
        this.manager = manager;
    }

    /**
     * Метод выводит информацию о коллекции
     *
     * @param commandPool
     * @param sendPool
     * @param key
     */
    @Override
    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable info = () -> {
            sendPool.submit(new Sender(key, "Тип коллекции: PriorityQueue\n" +
                    "Размер коллекции: " + manager.collection.size() + "\n" +
                    "Дата инициализации: " + initDate));
        };
        commandPool.execute(info);
    }
}
