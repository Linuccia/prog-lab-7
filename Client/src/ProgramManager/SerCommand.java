package ProgramManager;

import DataClasses.Person;
import DataClasses.Product;

import java.io.Serializable;

/**
 * Класс для отправления команд в виде объекта
 */
public class SerCommand implements Serializable {
    private static final long serialVersionUID = 17L;
    Product product;
    Person person;
    String command;
    Integer arg;
    String login;
    String password;

    public SerCommand(String command, String login, String password){
        this.command=command;
        this.login = login;
        this.password = password;
    }

    public SerCommand(String command, Product product, String login, String password){
        this.command=command;
        this.product=product;
        this.login = login;
        this.password = password;
    }

    public SerCommand(String command, Person person, String login, String password){
        this.command=command;
        this.person=person;
        this.login = login;
        this.password = password;
    }

    public SerCommand(String command, Integer arg, String login, String password){
        this.command=command;
        this.arg=arg;
        this.login = login;
        this.password = password;
    }

    public SerCommand(String command, Integer arg, Product product, String login, String password){
        this.command=command;
        this.arg=arg;
        this.product=product;
        this.login = login;
        this.password = password;
    }

    public String getCommand(){return command;}
    public Product getProduct(){return product;}
    public Person getPerson(){return person;}
    public Integer getArg(){return arg;}
    public String getLogin(){return login;}
    public String getPassword(){return password;}
}
