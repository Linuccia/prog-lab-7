package DataClasses;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private int x; //Поле не может быть null, Максимальное значение поля: 857
    private Double y; //Поле не может быть null

    public Coordinates(int x, Double y){
        this.x = x;
        this.y = y;
    }

    public int getX(){return x;}
    public Double getY(){return y;}

    @Override
    public String toString(){
        return "(x = " + x + ", y = " + y + ")";
    }
}
