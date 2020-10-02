package ProgramManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Sender implements Runnable {
    private SelectionKey key;
    private String answer;

    public Sender(SelectionKey key, String answer){
        this.key = key;
        this.answer = answer;
    }

    /**
     * Метод отправляет ответы клиенту
     */
    public void run() {
        SocketChannel channel = (SocketChannel) key.channel();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ObjectOutputStream toClient = new ObjectOutputStream(out)) {
            toClient.writeObject(answer);
            ByteBuffer buffer = ByteBuffer.wrap(out.toByteArray());
            int available = channel.write(buffer);
            while (available > 0) {
                available = channel.write(buffer);
            }
            buffer.clear();
            buffer.flip();
            System.out.println("Ответ отправлен клиенту");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
