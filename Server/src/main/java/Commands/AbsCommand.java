package Commands;
import ProgramManager.SerCommand;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;

/**
 * Абстрактный класс-родитель для команд
 */
public abstract class AbsCommand {

    public void execute(SerCommand command, ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
    }

}
