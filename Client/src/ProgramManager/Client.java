package ProgramManager;

import java.util.NoSuchElementException;

/**
 * @author Stephanskaya P.A.
 */
public class Client {
    public static void main(String[] args) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("Клиент отключен");
                }
            });
            System.out.println("Запускаю клиент...");
            Connection client = new Connection();
            client.Connect();
        } catch (NoSuchElementException e) {
            System.out.println("Выход из программы...");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
