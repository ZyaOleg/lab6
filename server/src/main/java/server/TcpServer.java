package server;

import collection.ProductFileManager;
import collection.ProductManager;
import common.Command;
import common.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

/**
 * TCP-сервер на блокирующих сокетах и потоках ввода-вывода.
 * Работает в однопоточном режиме: последовательно принимает и обрабатывает клиентов,
 * консольные команды (save, exit) проверяются в основном цикле с таймаутом accept.
 * Формат обмена: сначала 4 байта (int, big-endian) – длина следующего сообщения,
 * затем байты сериализованного объекта Command или Response.
 */
public class TcpServer {
    private static final Logger log = LogManager.getLogger(TcpServer.class);
    private final CommandProcessor commandProcessor;
    private final ProductManager productManager;
    private final ProductFileManager fileManager;
    private final int port;
    private volatile boolean running = true;
    public TcpServer(CommandProcessor commandProcessor, ProductManager productManager,
                     ProductFileManager fileManager, int port) {
        this.commandProcessor = commandProcessor;
        this.productManager = productManager;
        this.fileManager = fileManager;
        this.port = port;
    }

    /**
     * Запускает сервер: открывает ServerSocket, входит в цикл обработки,
     * принимает клиентов, обрабатывает их команды, а также реагирует на консольный ввод.
     */
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(100);
        log.info("Сервер запущен на порту {}", port);
        log.info("Введите 'save' для сохранения, 'exit' для остановки.");

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            if (System.in.available() > 0) {
                String line = consoleReader.readLine();
                if (line != null) {
                    line = line.trim();
                    if (line.equalsIgnoreCase("save")) {
                        try {
                            fileManager.saveToFile(productManager.getAll());
                            log.info("Коллекция сохранена.");
                        } catch (IOException e) {
                            log.error("Ошибка сохранения: {}", e.getMessage());
                        }
                    } else if (line.equalsIgnoreCase("exit")) {
                        try {
                            fileManager.saveToFile(productManager.getAll());
                            log.info("Коллекция сохранена перед выходом.");
                        } catch (IOException e) {
                            log.error("Ошибка сохранения перед выходом: {}", e.getMessage());
                        }
                        running = false;
                        break;
                    } else if (!line.isEmpty()) {
                        log.warn("Неизвестная команда. Доступны: save, exit");
                    }
                }
            }

            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (SocketTimeoutException e) {
                continue;
            }

            if (clientSocket != null) {
                log.info("Подключился клиент: {}", clientSocket.getRemoteSocketAddress());
                handleClient(clientSocket);
            }
        }

        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        log.info("Раброта сервера остановлена.");
    }

    /**
     * Обрабатывает одного клиента: читает длину команды, читает данные,
     * десериализует команду, выполняет её, отправляет ответ.
     * Весь обмен синхронный, сокет закрывается после обработки.
     *
     * @param clientSocket сокет подключённого клиента
     */
    private void handleClient(Socket clientSocket) {
        try {
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();

            byte[] lenBytes = new byte[4];
            int read = 0;
            while (read < 4) {
                int r = in.read(lenBytes, read, 4 - read);
                if (r == -1) throw new EOFException("Клиент закрыл соединение");
                read += r;
            }
            int dataLength = ByteBuffer.wrap(lenBytes).getInt();

            if (dataLength <= 0 || dataLength > 10_485_760) {
                throw new IOException("Некорректная длина: " + dataLength);
            }

            byte[] data = new byte[dataLength];
            read = 0;
            while (read < dataLength) {
                int r = in.read(data, read, dataLength - read);
                if (r == -1) throw new EOFException("Клиент закрыл соединение");
                read += r;
            }

            Command command;
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                command = (Command) ois.readObject();
            }

            log.info("Получена команда {} от {}", command.getName(), clientSocket.getRemoteSocketAddress());
            Response response = commandProcessor.execute(command);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(response);
            }
            byte[] respData = baos.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(4 + respData.length);
            buffer.putInt(respData.length);
            buffer.put(respData);
            out.write(buffer.array());
            out.flush();

            log.info("Отправлен ответ клиенту {}: успех={}", clientSocket.getRemoteSocketAddress(), response.isSuccess());

        } catch (EOFException e) {
            log.error("Клиент {} закрыл соединение преждевременно", clientSocket.getRemoteSocketAddress());
        } catch (ClassNotFoundException e) {
            log.error("Ошибка десериализации команды от {}: {}", clientSocket.getRemoteSocketAddress(), e.getMessage());
            try {
                Response errResp = new Response(false, "Ошибка десериализации: " + e.getMessage(), null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(errResp);
                }
                byte[] errData = baos.toByteArray();
                ByteBuffer buffer = ByteBuffer.allocate(4 + errData.length);
                buffer.putInt(errData.length);
                buffer.put(errData);
                clientSocket.getOutputStream().write(buffer.array());
                clientSocket.getOutputStream().flush();
            } catch (IOException ex) {
                log.error("Не удалось отправить ответ об ошибке: {}", ex.getMessage());
            }
        } catch (IOException e) {
            log.error("Ошибка ввода-вывода при общении с клиентом {}: {}", clientSocket.getRemoteSocketAddress(), e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error("Ошибка при закрытии сокета клиента: {}", e.getMessage());
            }
        }
    }
}