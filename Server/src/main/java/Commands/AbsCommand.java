package Commands;

import DataClasses.Person;
import DataClasses.Product;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;

/**
 * Абстрактный класс-родитель для команд
 */
public abstract class AbsCommand {

    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key) {
    } //info, show, average of price,

    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, String login) {
    } //clear, remove first

    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Integer args) {
    } //count less than price

    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Integer args, String login) {
    } //remove greater, remove by id

    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Product product, String login) {
    } //add, add if min

    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Integer args, Product product, String login) {
    } //update by id

    public void execute(ExecutorService commandPool, ExecutorService sendPool, SelectionKey key, Person person){
    } //count by owner

}
