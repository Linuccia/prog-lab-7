package ProgramManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

public class Receiver implements Runnable {
    private SelectionKey key;
    private CollectionManager colManager;
    private Database database;
    private ExecutorService sendPool;
    private CommandManager comManager = new CommandManager();

    public Receiver(SelectionKey key, CollectionManager colManager, Database database, ExecutorService sendPool) {
        this.key = key;
        this.colManager = colManager;
        this.database = database;
        this.sendPool = sendPool;
    }

    /**
     * Метод получает команду от клиента
     */
    @Override
    public void run() {
        try {
            SerCommand command;
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            SocketChannel channel = (SocketChannel) key.channel();
            int available = channel.read(buffer);
            if (available > 0) {
                while (available > 0) {
                    available = channel.read(buffer);
                }
                byte[] buf = buffer.array();
                ObjectInputStream fromClient = new ObjectInputStream(new ByteArrayInputStream(buf));
                command = (SerCommand) fromClient.readObject();
                fromClient.close();
                buffer.clear();
                if (command.getCommand().equals("reg") || command.getCommand().equals("sign")) {
                    System.out.println("На сервер поступили логин и пароль от пользователя");
                } else {
                    System.out.println("На сервер поступила команда " + command.getCommand());
                }
                comManager.manager(command, colManager, database, sendPool, key);
            }
            if (available == -1) {
                key.cancel();
            }
        } catch (IOException | ClassNotFoundException e) {
            key.cancel();
        }
    }
}
