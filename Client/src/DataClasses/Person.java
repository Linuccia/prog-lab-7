package DataClasses;

import java.io.Serializable;

public class Person implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Double weight; //Поле не может быть null, Значение поля должно быть больше 0
    private Color eyeColor; //Поле не может быть null
    private Country nationality; //Поле не может быть null

    public Person(String name, Double weight, Color eyeColor, Country nationality){
        this.name = name;
        this.weight = weight;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
    }

    public String getName(){return name;}
    public Double getWeight(){return weight;}
    public Color getEyeColor(){return eyeColor;}
    public Country getNationality(){return nationality;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Person p = (Person) o;
        if (o == null || getClass() != o.getClass()) return false;
        return this.name.equals(p.getName()) && this.weight.equals(p.getWeight()) && this.eyeColor.equals(p.getEyeColor()) && this.nationality.equals(p.getNationality());
    }

    @Override
    public String toString(){
        return "(" + "name = " + name + ", weight = " + weight + ", eyeColor = " + eyeColor + ", nationality = " + nationality + ")";
    }
}
