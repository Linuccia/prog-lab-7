package Commands;

import ProgramManager.Sender;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;

public class Help extends AbsCommand {

    /**
     * Метод выводит справку о командах
     *
     * @param commandPool
     * @param sendPool
     * @param key
     */
    @Override
    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
        Runnable help = () -> {
            sendPool.submit(new Sender(key, "help : вывести справку по доступным командам\n"+
                    "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n"+
                    "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n"+
                    "add {element} : добавить новый элемент в коллекцию\n"+
                    "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n"+
                    "remove_by_id id : удалить элемент из коллекции по его id\n"+
                    "clear : очистить коллекцию\n"+
                    "execute_script file_name : считать и исполнить скрипт из указанного файла\n"+
                    "exit : завершить программу\n"+
                    "remove_first : удалить первый элемент из коллекции\n"+
                    "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n"+
                    "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n"+
                    "average_of_price : вывести среднее значение поля price для всех элементов коллекции\n"+
                    "count_by_owner owner : вывести количество элементов, значение поля owner которых равно заданному\n"+
                    "count_less_than_price price : вывести количество элементов, значение поля price которых меньше заданного\n"));
        };
        commandPool.execute(help);
    }
}
