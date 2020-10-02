package ProgramManager;

import DataClasses.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CommandManager {
    public boolean script;
    public boolean autho;
    private ArrayList<File> scriptCycle = new ArrayList<>();
    private BufferedReader commandReader;
    Scanner scan = new Scanner(System.in);

    public void enterExchange(Socket socket, String command, String login, String password) throws IOException, ClassNotFoundException {
        if (command.equals("reg")){
            SerCommand send = new SerCommand("reg", login, password);
            sendCom(socket, send);
            getAns(socket);
        } else if(command.equals("sign")){
            SerCommand send = new SerCommand("sign", login, password);
            sendCom(socket, send);
            getAns(socket);
        }
        if (autho){
            while (true){
                command = scan.nextLine();
                exchange(socket, command, login, password);
            }
        }
    }

    /**
     * Метод обеспечивает отправление команд на сервер
     *
     * @param socket
     * @param command
     * @throws IOException
     */
    public void sendCom(Socket socket, SerCommand command) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream toServer = new ObjectOutputStream(output);
        toServer.writeObject(command);
        byte[] out = output.toByteArray();
        socket.getOutputStream().write(out);
    }

    /**
     * Метод обеспечивает получение ответа от сервера
     *
     * @param socket
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void getAns(Socket socket) throws IOException, ClassNotFoundException {
        String answer;
        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
        answer = (String) fromServer.readObject();
        if (answer.equals("exit")) {
            System.out.println("Завершение работы программы...");
            System.exit(0);
        } else if (answer.equals("Успешная авторизация")){
            autho = true;
            System.out.println("Вы успешно авторизовались!");
        } else {
            System.out.println(answer);
        }
    }

    /**
     * Метод обеспечивает обработку команд для отправления на сервер и получения ответа
     *
     * @param socket
     * @param command
     * @param login
     * @param password
     * @throws IOException
     */
    public void exchange(Socket socket, String command, String login, String password) throws IOException{
        String[] ComAndArgs = command.trim().split(" ");
        try{
            if (ComAndArgs.length == 1){
                switch (ComAndArgs[0]){
                    case "":
                        break;
                    case "help":
                    case "info":
                    case "show":
                    case "clear":
                    case "remove_first":
                    case "average_of_price":{
                        SerCommand send = new SerCommand(ComAndArgs[0], login, password);
                        sendCom(socket, send);
                        getAns(socket);
                    }
                    break;
                    case "add":
                    case "add_if_min":{
                        SerCommand send = new SerCommand(ComAndArgs[0], addProduct(), login, password);
                        sendCom(socket, send);
                        getAns(socket);
                    }
                    break;
                    case "exit":
                        System.out.println("Завершение работы программы...");
                        System.exit(0);
                    default:
                        System.out.println("Введена неизвестная команда. Повторите ввод");
                }
            }
            else if (ComAndArgs.length == 2){
                switch (ComAndArgs[0]){
                    case "remove_by_id":
                    case "remove_greater":
                    case "count_less_than_price":
                        try{
                            SerCommand send = new SerCommand(ComAndArgs[0], Integer.parseInt(ComAndArgs[1]), login, password);
                            sendCom(socket, send);
                            getAns(socket);
                        } catch (NumberFormatException e){
                            System.out.println("Введенный аргумент не является или выходит за пределы int. Повторите ввод");
                        }
                        break;
                    case "update":
                        try{
                            SerCommand send = new SerCommand(ComAndArgs[0], Integer.parseInt(ComAndArgs[1]), addProduct(), login, password);
                            sendCom(socket, send);
                            getAns(socket);
                        } catch (NumberFormatException e){
                            System.out.println("Введенный аргумент не является или выходит за пределы int. Повторите ввод");
                        }
                        break;
                    case "execute_script": {
                        script = true;
                        File file = new File(ComAndArgs[1]);
                        if (!file.exists())
                            System.out.println("Файла с данным именем не существует");
                        else if (!file.canRead())
                            System.out.println("Отсутствуют права на чтение данного файла");
                        else if (scriptCycle.contains(file)) {
                            System.out.println("Выполение данного скрипта зациклится:"
                                    + ComAndArgs[1] + "\nКоманда не выполнена");
                        } else {
                            scriptCycle.add(file);
                            try {
                                commandReader = new BufferedReader(new FileReader(file));
                                String line = commandReader.readLine();
                                while (line != null){
                                    exchange(socket, line, login, password);
                                    System.out.println();
                                    line = commandReader.readLine();
                                }
                                System.out.println("Скрипт успешно выполнен");
                            } catch (IOException ex) {
                                System.out.println("Ошибка чтения скрипта");
                            }
                            scriptCycle.remove(scriptCycle.size() - 1);
                        }
                        script = false;
                        break;
                    }
                    default:
                        System.out.println("Введена неизвестная команда или не введен аргумент. Повторите ввод");
                }
            }
            else if (ComAndArgs.length == 5){
                if (ComAndArgs[0].equals("count_by_owner")){
                    String personName;
                    if (ComAndArgs[1].equals("")) {
                        System.out.println("Имя владельца не может быть null");
                        return;
                    } else {
                        personName = ComAndArgs[1];
                    }

                    double weight;
                    try {
                        if ((ComAndArgs[2].equals("")) || (Double.parseDouble(ComAndArgs[2]) <= 0)) {
                            System.out.println("Вес не может быть null или меньше/равен 0");
                            return;
                        } else {
                            weight = Double.parseDouble(ComAndArgs[2]);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Введенное значение не является или выходит за пределы double");
                        return;
                    }

                    Color eyeColor;
                    try {
                        eyeColor = Color.valueOf(ComAndArgs[3].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Введенного цвета не существует");
                        return;
                    } catch (NullPointerException e) {
                        System.out.println("Цвет не может быть null");
                        return;
                    }

                    Country nationality;
                    try {
                        nationality = Country.valueOf(ComAndArgs[4].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Введенной страны не существует");
                        return;
                    } catch (NullPointerException e) {
                        System.out.println("Страна не может быть null");
                        return;
                    }

                    Person owner = new Person(personName, weight, eyeColor, nationality);

                    SerCommand send = new SerCommand(ComAndArgs[0], owner, login, password);
                    sendCom(socket, send);
                    getAns(socket);

                }
            } else {
                System.out.println("Введена неизвестная команда или не введены аргументы. Повторите ввод");
            }
        } catch (ArrayIndexOutOfBoundsException | ClassNotFoundException e){
            System.out.println("Аргумент отсутствует");
        }
    }

    /**
     * Метод создает элемент для коллекции
     *
     * @return Product
     */
    public Product addProduct() {
        Scanner scanner = new Scanner(System.in);
        Product product;
        UnitOfMeasure unitOfMeasure = null;
        Color eyeColor = null;
        Country nationality = null;
        StringParser pars = new StringParser();

        if (script) {
            try {
                String[] addParam = new String[11];
                for (int i = 0; i < addParam.length; i++) {
                    addParam[i] = commandReader.readLine();
                }
                String name = addParam[0];
                int x = Integer.parseInt(addParam[1]);
                Double y = Double.parseDouble(addParam[2]);
                Integer price = Integer.parseInt(addParam[3]);
                String partNumber = addParam[4];
                Long manufactureCost = Long.parseLong(addParam[5]);
                unitOfMeasure = UnitOfMeasure.valueOf(addParam[6]);
                String perName = addParam[7];
                Double weight = Double.parseDouble(addParam[8]);
                eyeColor = Color.valueOf(addParam[9]);
                nationality = Country.valueOf(addParam[10]);
                Integer id = 0;
                product = new Product(id, name, new Coordinates(x, y), price, partNumber, manufactureCost, unitOfMeasure,
                        new Person(perName, weight, eyeColor, nationality), "");
            } catch (IllegalArgumentException | NullPointerException | IOException e) {
                //e.printStackTrace();
                return null;
            }
        } else {

            String name = pars.strParse("название продукта");

            String strX;
            int x = 858;
            do {
                try {
                    System.out.println("Введите значение поля координата x (значение должно быть меньше или равно 857)");
                    strX = scanner.nextLine().trim();
                    x = Integer.parseInt(strX);
                    if (x >= 858) {
                        System.out.println("Значение не может быть больше 857. Повторите ввод");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Введенное значение не является целым числом или выходит за пределы int. Повторите ввод");
                }
            } while (x >= 858);

            Double y = pars.dblParse("координата y");

            Integer price;
            do {
                price = pars.intParse("цена");
                if (price <= 0) {
                    System.out.println("Цена не может быть меньше или равна 0");
                }
            } while (price <= 0);

            int length;
            String partNumber;
            do {
                partNumber = pars.strParse("номер партии");
                length = partNumber.length();
                if ((length > 85) || (length < 15)) {
                    System.out.println("Номер партии не может быть длиннее 85 или короче 15. Повторите ввод");
                }
            } while ((length > 85) || (length < 15));

            String strManufactureCost;
            Long manufactureCost = null;
            do {
                try {
                    System.out.println("Введите значение поля цена изготовления");
                    strManufactureCost = scanner.nextLine().trim();
                    if (strManufactureCost.equals("")) {
                        System.out.println("Цена изготовления не может быть null. Повторите ввод");
                    } else {
                        manufactureCost = Long.parseLong(strManufactureCost);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Введенное значение не является или выходит за пределы long. Повторите ввод");
                }
            } while (manufactureCost == null);

            String strUnitOfMeasure;
            do {
                System.out.println("Введите одну из единиц измерения: " + Arrays.toString(UnitOfMeasure.values()));
                strUnitOfMeasure = scanner.nextLine().trim().toUpperCase();
                try {
                    unitOfMeasure = UnitOfMeasure.valueOf(strUnitOfMeasure);
                } catch (IllegalArgumentException e) {
                    System.out.println("Данной единицы измерения не существует. Повторите ввод");
                } catch (NullPointerException e) {
                    System.out.println("Единица измерения не может быть null. Повторите ввод");
                }
            } while (unitOfMeasure == null);

            String personName = pars.strParse("имя владельца");

            Double weight;

            do {
                weight = pars.dblParse("вес");
            } while (weight <= 0);

            String strEyeColor;
            do {
                System.out.println("Введите один из цветов глаз: " + Arrays.toString(Color.values()));
                strEyeColor = scanner.nextLine().trim().toUpperCase();
                try {
                    eyeColor = Color.valueOf(strEyeColor);
                } catch (IllegalArgumentException e) {
                    System.out.println("Данного цвета не существует. Повторите ввод");
                } catch (NullPointerException e) {
                    System.out.println("Цвет не может быть null. Повторите ввод");
                }
            } while (eyeColor == null);

            String strNationality;
            do {
                System.out.println("Введите одну из стран: " + Arrays.toString(Country.values()));
                strNationality = scanner.nextLine().trim().toUpperCase();
                try {
                    nationality = Country.valueOf(strNationality);
                } catch (IllegalArgumentException e) {
                    System.out.println("Данной страны не существует. Повторите ввод");
                } catch (NullPointerException e) {
                    System.out.println("Страна не может быть null. Повторите ввод");
                }
            } while (nationality == null);

            Integer id = 1;

            product = new Product(id, name, new Coordinates(x, y), price, partNumber, manufactureCost, unitOfMeasure, new Person(personName, weight, eyeColor, nationality), "");
        }
            return product;
    }

}
