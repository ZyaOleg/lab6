package server.handlers;

import common.Command;
import common.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
import common.model.Product;
/**
 * Обработчик команды head: возвращает первый элемент коллекции,
 * отсортированный по местоположению (согласно заданию).
 */
public class HeadCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(HeadCommandHandler.class);
    private final ProductManager productManager;
    public HeadCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        Product first = productManager.getFirstByLocation();
        if (first != null) {
            log.debug("Первый элемент коллекции: id={}", first.getId());
            return new Response(true, "Первый элемент: ", first);
        } else {
            log.debug("Коллекция пуста, head не возвращает элемент");
            return new Response(false, "В коллекции нет продуктов", null);
        }
    }
}