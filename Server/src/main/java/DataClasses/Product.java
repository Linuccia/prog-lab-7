package DataClasses;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Product implements Comparable<Product>, Serializable {
    private static final long serialVersionUID = 42L;
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer price; //Поле не может быть null, Значение поля должно быть больше 0
    private String partNumber; //Длина строки не должна быть больше 85, Длина строки должна быть не меньше 15, Строка не может быть пустой, Поле не может быть null
    private Long manufactureCost; //Поле не может быть null
    private UnitOfMeasure unitOfMeasure; //Поле не может быть null
    private Person owner; //Поле не может быть null
    private String login; //Логин пользователя

    public Product(Integer id, String name, Coordinates coordinates, Integer price, String partNumber, Long manufactureCost, UnitOfMeasure unitOfMeasure, Person owner, String login){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.price = price;
        this.partNumber = partNumber;
        this.manufactureCost = manufactureCost;
        this.unitOfMeasure = unitOfMeasure;
        this.owner = owner;
        this.login = login;
    }

    public Integer getId(){return  id;}
    public String getName(){return  name;}
    public Coordinates getCoordinates(){return  coordinates;}
    public LocalDateTime getCreationDate(){return creationDate;}
    public Integer getPrice(){return  price;}
    public String getPartNumber(){return  partNumber;}
    public Long getManufactureCost(){return  manufactureCost;}
    public UnitOfMeasure getUnitOfMeasure(){return  unitOfMeasure;}
    public Person getOwner(){return  owner;}
    public String getLogin(){return login;}

    public void setCreationDate() { this.creationDate = LocalDateTime.now();}
    public void setLogin(String login){
        this.login = login;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int compareTo(Product p) {
        return price - p.getPrice();
    }

    @Override
    public String toString(){
        return "[" + "id = " + id + ", name = '" + name + '\'' + ", coordinates = " + coordinates + ", creationDate = " + creationDate +
                ", price = " + price + ", partNumber = " + partNumber + ", manufactureCost = " + manufactureCost + ", unitOfMeasure = " + unitOfMeasure +
                " owner = " + owner + ", login = " + login + "]" + "\n";
    }
}
