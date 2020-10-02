package ProgramManager;

import DataClasses.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.PriorityQueue;
import java.util.Properties;

public class Database {
    private Connection connection;
    private Statement statement;
    private PreparedStatement preState;
    private ResultSet result;
    private Product product;
    private MessageDigest md;

    /**
     * Метод осуществляет поключение к базе данных и загружает оттуда коллекцию
     *
     * @param file
     * @return
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public PriorityQueue<Product> load(String file) throws IOException, SQLException, ClassNotFoundException {
        PriorityQueue<Product> collection = new PriorityQueue<>();
        Properties prop = new Properties();
        prop.load(new FileInputStream(file));
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(prop.getProperty("location"), prop.getProperty("name"), prop.getProperty("password"));
        System.out.println("Сервер подключился в базе данных");
        statement = connection.createStatement();
        result = statement.executeQuery("SELECT * FROM product;");
        while (result.next()){
            Integer id = result.getInt("id");
            String name = result.getString("name");
            int x = result.getInt("x");
            Double y = result.getDouble("y");
            Integer price = result.getInt("price");
            String partNumber = result.getString("partNumber");
            Long manufactureCost = result.getLong("manufactureCost");
            UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(result.getString("unitOfMeasure"));
            String perName = result.getString("perName");
            Double weight = result.getDouble("weight");
            Color eyeColor = Color.valueOf(result.getString("eyeColor"));
            Country nationality = Country.valueOf(result.getString("nationality"));
            String login = result.getString("login");
            product = new Product(id, name, new Coordinates(x, y), price, partNumber, manufactureCost, unitOfMeasure,
                    new Person(perName, weight, eyeColor, nationality), login);
            collection.add(product);
        }
        return collection;
    }

    /**
     * Метод добавляет параметры объекта в базу данных
     *
     * @param product
     * @param id
     * @param login
     * @throws SQLException
     */
    public void add(Product product, Integer id, String login) throws SQLException{
        preState = connection.prepareStatement("INSERT INTO product (id, name, x, y, creationDate, " +
                "price, partNumber, manufactureCost, unitOfMeasure, perName, weight, eyeColor, nationality, login)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        preState.setInt(1, id);
        preState.setString(2, product.getName());
        preState.setInt(3, product.getCoordinates().getX());
        preState.setDouble(4, product.getCoordinates().getY());
        preState.setObject(5, product.getCreationDate());
        preState.setInt(6, product.getPrice());
        preState.setString(7, product.getPartNumber());
        preState.setLong(8, product.getManufactureCost());
        preState.setString(9, String.valueOf(product.getUnitOfMeasure()));
        preState.setString(10, product.getOwner().getName());
        preState.setDouble(11, product.getOwner().getWeight());
        preState.setString(12, String.valueOf(product.getOwner().getEyeColor()));
        preState.setString(13, String.valueOf(product.getOwner().getNationality()));
        preState.setString(14, login);
        preState.execute();
    }

    /**
     * Метод осуществляет регистрацию пользователя
     *
     * @param command
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String registration(SerCommand command) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        try{
            md = MessageDigest.getInstance("SHA-384");
            PreparedStatement preState = connection.prepareStatement("INSERT INTO login_password (login, password) VALUES(?, ?)");
            preState.setString(1, command.getLogin());
            preState.setString(2, Base64.getEncoder().encodeToString(md.digest(command.getPassword().getBytes("UTF-8"))));
            preState.execute();
            System.out.println("Зарегистрирован пользователь " + command.getLogin());
            return "Вы успешно зарегистрировались";
        } catch (SQLException e){
            System.out.println("Ошибка SQL при регистрации нового пользователя");
            return "Ошибка регистрации в базе данных";
        }
    }

    /**
     * Метод авторизует пользователя
     *
     * @param command
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public boolean authorization(SerCommand command) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        try {
            md = MessageDigest.getInstance("SHA-384");
            preState = connection.prepareStatement("SELECT * FROM login_password WHERE (login = ?);");
            preState.setString(1, command.getLogin());
            result = preState.executeQuery();
            result.next();
            return Base64.getEncoder().encodeToString(md.digest(command.getPassword().getBytes("UTF-8"))).equals(result.getString("password"));
        } catch (SQLException e) {
            System.out.println("Ошибка авторизации в базе данных");
            return false;
        }
    }

    /**
     * Метод возвращает сгенерированный id из базы данных
     *
     * @return
     * @throws SQLException
     */
    public Integer getIdSeq() throws SQLException{
        ResultSet result = statement.executeQuery("SELECT nextval('idSequence');");
        result.next();
        return result.getInt(1);
    }

    /**
     * Метод удаляет из базы данных элементы, созданные одним пользователем
     *
     * @param login
     * @throws SQLException
     */
    public void clear(String login) throws SQLException{
        preState = connection.prepareStatement("DELETE FROM product WHERE login = ?;");
        preState.setString(1,login);
        preState.execute();
    }

    /**
     * Метод удаляет из базы данных элементы, price которых больше указанного
     *
     * @param price
     * @param login
     * @throws SQLException
     */
    public void deleteGreater(Integer price, String login) throws SQLException{
        preState = connection.prepareStatement("DELETE FROM product WHERE (price > ?) AND (login = ?);");
        preState.setInt(1, price);
        preState.setString(2, login);
        preState.execute();
    }

    /**
     * Метод удаляет из базы данных элементы с указанным id
     *
     * @param id
     * @param login
     * @throws SQLException
     */
    public void deleteById(Integer id, String login) throws SQLException{
        preState = connection.prepareStatement("DELETE FROM product WHERE (id = ?) AND (login = ?);");
        preState.setInt(1, id);
        preState.setString(2, login);
        preState.execute();
    }

    /**
     * Метод обновляет в базе данных элемент с указанным id
     *
     * @param id
     * @param login
     * @throws SQLException
     */
    public void updateById(Integer id, String login) throws SQLException{
        preState = connection.prepareStatement("UPDATE product SET name = ?, x = ?, y = ?, creationDate = ?, " +
                "price = ?, partNumber = ?, manufactureCost = ?, unitOfMeasure = ?, perName = ?, weight = ?, eyeColor = ?, nationality = ?" +
                "WHERE (id = ?) AND (login = ?);");
        preState.setString(1, product.getName());
        preState.setInt(2, product.getCoordinates().getX());
        preState.setDouble(3, product.getCoordinates().getY());
        preState.setObject(4, product.getCreationDate());
        preState.setInt(5, product.getPrice());
        preState.setString(6, product.getPartNumber());
        preState.setLong(7, product.getManufactureCost());
        preState.setString(8, String.valueOf(product.getUnitOfMeasure()));
        preState.setString(9, product.getOwner().getName());
        preState.setDouble(10, product.getOwner().getWeight());
        preState.setString(11, String.valueOf(product.getOwner().getEyeColor()));
        preState.setString(12, String.valueOf(product.getOwner().getNationality()));
        preState.setInt(13, id);
        preState.setString(14, login);
        preState.execute();
    }
}
