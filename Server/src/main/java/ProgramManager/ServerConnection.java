package ProgramManager;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnection {
    private CollectionManager manager = new CollectionManager();
    private Scanner scan = new Scanner(System.in);
    private Database database = new Database();
    private ExecutorService sendPool = Executors.newFixedThreadPool(2);
    /**
     * Метод обеспечивает соединение сервера с клиентом
     *
     * @param file
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public void connect(String file) throws IOException, ClassNotFoundException, InterruptedException {
        while (true) {
            try {
                System.out.println("Введите порт");
                int port = Integer.parseInt(scan.nextLine());
                Selector selector = Selector.open();
                try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {
                    socketChannel.bind(new InetSocketAddress(port));
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_ACCEPT);
                    manager.getCommands(manager, database);
                    String loadMessage = manager.load(file, database);
                    System.out.println("Сервер запущен и ожидает подключения");
                    while (selector.isOpen()) {
                        int count = selector.select();
                        if (count == 0) {
                            continue;
                        }
                        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                        while (iter.hasNext()) {
                            SelectionKey key = iter.next();
                            try {
                                if (key.isAcceptable()) {
                                    SocketChannel channel = socketChannel.accept();
                                    System.out.println("Клиент успешно подключился к серверу");
                                    channel.configureBlocking(false);
                                    channel.register(selector, SelectionKey.OP_WRITE);
                                }
                                if (key.isWritable()) {
                                    if (loadMessage == null){
                                        sendPool.execute(new Sender(key, "Из базы данных загружена коллекция, содержащая "+manager.collection.size()+" элементов"));
                                        key.interestOps(SelectionKey.OP_READ);
                                    } else {
                                        sendPool.execute(new Sender(key, loadMessage));
                                        Thread.sleep(1000);
                                        System.exit(1);
                                    }
                                }
                                if (key.isReadable()) {
                                    Thread thread = new Thread(new Receiver(key, manager, database, sendPool));
                                    thread.start();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                                iter.remove();
                            } catch (CancelledKeyException e) {
                                System.out.println("Клиент отключился от сервера");
                            }
                        }
                    }
                }
            } catch (BindException e) {
                System.out.println("Данный порт уже используется");
            } catch (NumberFormatException e) {
                System.out.println("Введенный порт не является числом");
            } catch (IllegalArgumentException e) {
                System.out.println("Порт должен принимать значения от 1 до 65535");
            } catch (SocketException e) {
                System.out.println("Данный порт недопустим к использованию");
            }
        }
    }
}
