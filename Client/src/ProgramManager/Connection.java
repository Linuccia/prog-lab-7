package ProgramManager;


import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Connection {
    private CommandManager manager = new CommandManager();
    private String login;
    private String password;

    /**
     * Метод обеспечивает соединение между клиентом и сервером
     */
    public void Connect() throws ClassNotFoundException{
        while (true) {
            try {
                Scanner scan = new Scanner(System.in);
                System.out.println("Введите хост");
                String host = scan.nextLine();
                System.out.println("Введите порт");
                int port = scan.nextInt();
                SocketAddress address = new InetSocketAddress(host, port);
                try (Socket socket = new Socket()) {
                    socket.connect(address, 2000);
                    System.out.println("Соединение с сервером установлено!");
                    manager.getAns(socket);
                    while (true) {
                        regsign(socket);
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Время подключения к серверу вышло. Неверно указан хост или порт");
            } catch (NumberFormatException e) {
                System.out.println("Введенный порт не является числом или выходит за пределы int");
            }catch (ConnectException e){
                System.out.println("Введенный порт недоступен");
            } catch (UnknownHostException e){
                System.out.println("Введен неверный хост");
            } catch (IllegalArgumentException e){
                System.out.println("Порт должен принимать значения от 1 до 65535");
            } catch (IOException e){
                manager.script = false;
                manager.autho = false;
                System.out.println("Не удалось подключиться к серверу. Повторить попытку подключения?(Введите да или нет)");
                String answer;
                Scanner scan = new Scanner(System.in);
                while (!(answer = scan.nextLine()).equals("да")){
                    switch(answer){
                        case "":
                            break;
                        case "нет":
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Введено некорректное значение. Повторите ввод");
                    }
                }
            }
        }
    }

    public void regsign(Socket socket) throws IOException, ClassNotFoundException{
        String command;
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("Введите reg, если хотите зарегистрироваться, или sign, если хотите войти");
            String com = scan.nextLine();
            if (com.equals("reg")) {
                System.out.println("Регистрация нового пользователя");
                loginPassword();
                command = "reg";
                break;
            } else if (com.equals("sign")) {
                System.out.println("Авторизация пользователя");
                loginPassword();
                command = "sign";
                break;
            }
        }
        manager.enterExchange(socket, command, login, password);
    }

    private void loginPassword(){
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("Введите логин");
            login = scan.nextLine();
            if (login.equals("")) {
                System.out.println("Введена пустая строка. Повторите ввод");
            } else {
                break;
            }
        }
        while (true) {
            System.out.println("Введите пароль");
            password = scan.nextLine();
            if (password.equals("")) {
                System.out.println("Введена пустая строка. Повторите ввод");
            } else {
                break;
            }
        }
    }
}
