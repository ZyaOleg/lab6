package server;

import collection.IdGenerator;
import collection.ProductFileManager;
import collection.ProductManager;
import common.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayDeque;
/**
 * Точка входа в серверное приложение.
 * Загружает коллекцию из файла, указанного в переменной окружения PRODUCTS_FILE,
 * настраивает менеджеры, добавляет обработчик завершения и запускает сервер.
 */
public class ServerApp {
    private static final Logger log = LogManager.getLogger(ServerApp.class);
    public static void main(String[] args) {
        String filePath = System.getenv("PRODUCTS_FILE");
        if (filePath == null) {
            log.error("Переменная окружения PRODUCTS_FILE не установлена");
            System.exit(1);
        }

        IdGenerator productIdGen = new IdGenerator();
        IdGenerator orgIdGen = new IdGenerator();
        ProductManager productManager = new ProductManager(productIdGen, orgIdGen);
        ProductFileManager fileManager = new ProductFileManager(filePath, productIdGen, orgIdGen, productManager.getPartNumbers());

        try {
            ArrayDeque<Product> loaded = fileManager.loadFromFile();
            productManager.loadCollection(loaded);
            log.info("Коллекция загружена. Количество элементов: {}", productManager.getSize());
        } catch (IOException e) {
            log.error("Ошибка загрузки: {}", e.getMessage());
            System.exit(1);
        }

        CommandProcessor processor = new CommandProcessor(productManager);
        TcpServer server = new TcpServer(processor, productManager, fileManager, 6527);
        try {
            server.start();
        } catch (IOException e) {
            log.error("Ошибка запуска сервера: {}", e.getMessage());
            System.exit(1);
        }
    }
}