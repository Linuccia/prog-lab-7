package ProgramManager;

import Commands.AbsCommand;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandManager {
    private ExecutorService commandPool = Executors.newFixedThreadPool(2);

    /**
     * Метод обрабатывает команды
     *
     * @param command
     * @param manager
     * @param database
     * @param sendPool
     * @param key
     */
    public void manager(SerCommand command, CollectionManager manager, Database database, ExecutorService sendPool, SelectionKey key){
        try {
            if (command.getCommand().equals("reg")) {
                sendPool.submit(new Sender(key, database.registration(command)));
            } else if (command.getCommand().equals("sign")) {
                if (database.authorization(command)) {
                    System.out.println("Авторизовался пользователь " + command.getLogin());
                    sendPool.submit(new Sender(key, "Успешная авторизация"));
                } else {
                    System.out.println("Ошибка авторизации пользователя");
                    sendPool.submit(new Sender(key, "Неверный логин или пароль"));
                }
            } else {
                if (database.authorization(command)) {
                    manager.commandMap.get(command.getCommand()).execute(command, commandPool, sendPool, key);
                    /*switch (command.getCommand()) {
                        case "help":
                        case "info":
                        case "show":
                        case "average_of_price":{
                            manager.commandMap.get(command.getCommand()).execute(commandPool,sendPool, key);
                        }
                        break;
                        case "remove_first":
                        case "clear": {
                            manager.commandMap.get(command.getCommand()).execute(commandPool,sendPool, key, command.getLogin());
                        }
                        break;
                        case "count_less_than_price": {
                            manager.commandMap.get(command.getCommand()).execute(commandPool, sendPool, key, command.getArg());
                        }
                        break;
                        case "remove_by_id":
                        case "remove_greater": {
                            manager.commandMap.get(command.getCommand()).execute(commandPool, sendPool, key, command.getArg(), command.getLogin());
                        }
                        break;
                        case "add":
                        case "add_if_min": {
                            manager.commandMap.get(command.getCommand()).execute(commandPool, sendPool, key, command.getProduct(), command.getLogin());
                        }
                        break;
                        case "update": {
                            manager.commandMap.get(command.getCommand()).execute(commandPool, sendPool, key, command.getArg(), command.getProduct(), command.getLogin());
                        }
                        case "count_by_owner": {
                            manager.commandMap.get(command.getCommand()).execute(commandPool, sendPool, key, command.getPerson());
                        }
                        break;
                    }*/
                    System.out.println("Выполнена команда " + command.getCommand());
                } else {
                    sendPool.submit(new Sender(key, "Ты зачем пытаешься меня обхитрить?"));
                }
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
