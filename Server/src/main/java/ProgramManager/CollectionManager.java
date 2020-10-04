package ProgramManager;

import Commands.*;
import DataClasses.Product;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CollectionManager {
    public PriorityQueue<Product> collection= new PriorityQueue<>();
    public Map<String, AbsCommand> commandMap;
    public Lock lock = new ReentrantLock();


    public void getCommands(CollectionManager manager, Database database){
        commandMap = new HashMap<>();
        commandMap.put("clear", new Clear(manager, database));
        commandMap.put("show", new Show(manager));
        commandMap.put("info", new Info(manager));
        commandMap.put("help", new Help());
        commandMap.put("add", new Add(manager, database));
        commandMap.put("count_by_owner", new CountByOwner(manager));
        commandMap.put("add_if_min", new AddIfMin(manager, database));
        commandMap.put("average_of_price", new AverageOfPrice(manager));
        commandMap.put("count_less_than_price", new CountLessThanPrice(manager));
        commandMap.put("remove_greater", new RemoveGreater(manager, database));
        commandMap.put("remove_by_id", new RemoveById(manager, database));
        commandMap.put("update", new UpdateId(manager, database));
        commandMap.put("remove_first", new RemoveFirst(manager, database));
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("Сервер запускается...");
        ServerConnection connection = new ServerConnection();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("Отключение сервера...");
                }
            });
            connection.connect(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Введите имя файла!!!");
        } catch (NoSuchElementException e) {
            System.out.println("Выход из программы...");
        }
    }

    /**
     * Метод загружает коллекцию из базы данных
     *
     * @param file
     * @param database
     * @return
     * @throws ClassNotFoundException
     */
    public String load(String file, Database database) throws ClassNotFoundException {
        try{
            collection = database.load(file);
        } catch (IOException e){
            System.out.println("Ошибка подключения к базе данных");
            return "Файл с properties базы данных не найден";
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Ошибка поключения к базе данных");
            return "Ошибка подключения сервера к базе данных";
        }
        return null;
    }
}
