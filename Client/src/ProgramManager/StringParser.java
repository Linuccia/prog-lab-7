package ProgramManager;

import java.util.Scanner;

/**
 * Класс, содержащий методы для парсинга вводимых значений с консоли в String, Integer и Double, используемые в команде add
 */
public class StringParser {

    Scanner scan = new Scanner(System.in);
    static String s;

    /**
     * Метод для инициализации полей типа String
     *
     * @param a
     * @return
     */
    public String strParse(String a){
        do {
            System.out.println("Введите значение поля " + a + "");
            s = scan.nextLine().trim();
            if (s.equals("")) {
                System.out.println(a + " не может быть null. Повторите ввод");
            }
        } while (s.equals(""));
        return s;
    }

    /**
     * Метод для инициализации полей типа Integer
     *
     * @param a
     * @return
     */
    public Integer intParse(String a){
        Integer i = null;
        do {
            try {
                System.out.println("Введите значение поля " + a);
                s = scan.nextLine().trim();
                if (s.equals("")) {
                    System.out.println(a + " не может быть null. Повторите ввод");
                } else {
                    i = Integer.parseInt(s);
                }
            } catch (NumberFormatException e) {
                System.out.println("Введенное значение не является целым числом или выходит за пределы int. Повторите ввод");
            }
        } while (i == null);
        return i;
    }

    /**
     * Метод для инициализации полей типа Double
     *
     * @param a
     * @return
     */
    public Double dblParse(String a){
        Double d = null;
        do {
            try {
                System.out.println("Введите значение поля " + a);
                s = scan.nextLine().trim();
                if (s.equals("")) {
                    System.out.println(a + " не может быть null. Повторите ввод");
                } else {
                    d = Double.parseDouble(s);
                }
            } catch (NumberFormatException e) {
                System.out.println("Введенное значение не является или выходит за пределы double. Повторите ввод");
            }
        } while (d == null);
        return d;
    }

}
