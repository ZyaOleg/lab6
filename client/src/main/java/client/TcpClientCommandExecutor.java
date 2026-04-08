package client;

import common.Command;
import common.CommandExecutor;
import common.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
/**
 * Отправляет команды на сервер по TCP.
 * На каждую команду открывается новое соединение.
 * При ошибках делает до 5 попыток с паузой 2 секунды.
 */
public class TcpClientCommandExecutor implements CommandExecutor {
    private final String host;
    private final int port;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 2000;
    public TcpClientCommandExecutor(String host, int port){
        this.host=host;
        this.port=port;
    }
    /**
     * Отправляет команду, получает ответ.
     * @param command команда для выполнения
     * @return ответ сервера
     * @throws RuntimeException если сервер не отвечает после всех попыток
     */
    @Override
    public Response execute(Command command) {
        IOException lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try (SocketChannel channel = SocketChannel.open()) {
                channel.configureBlocking(false);
                channel.connect(new InetSocketAddress(host, port));
                while (!channel.finishConnect()) {
                    Thread.sleep(100);
                }
                // Отправка с префиксом длины
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(command);
                }
                byte[] data = baos.toByteArray();
                ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
                buffer.putInt(data.length);
                buffer.put(data);
                buffer.flip();
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                // Чтение ответа
                ByteBuffer lenBuf = ByteBuffer.allocate(4);
                while (lenBuf.hasRemaining()) {
                    if (channel.read(lenBuf) == -1) throw new IOException("Соединение разорвано");
                }
                lenBuf.flip();
                int respLen = lenBuf.getInt();
                ByteBuffer respBuf = ByteBuffer.allocate(respLen);
                while (respBuf.hasRemaining()) {
                    if (channel.read(respBuf) == -1) throw new IOException("Соединение разорвано");
                }
                respBuf.flip();
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(respBuf.array()))) {
                    return (Response) ois.readObject();
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                lastException = new IOException("Попытка " + attempt + " не удалась", e);
                if (attempt < MAX_RETRIES) {
                    try { Thread.sleep(RETRY_DELAY_MS); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }
        }
        throw new RuntimeException("Сервер недоступен", lastException);
    }
}
