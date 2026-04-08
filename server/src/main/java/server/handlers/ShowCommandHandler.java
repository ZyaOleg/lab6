package server.handlers;

import common.Command;
import common.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import server.ProductSorter;
import collection.ProductManager;
import common.model.Product;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Обработчик команды show: возвращает список всех продуктов, отсортированных по местоположению.
 */
public class ShowCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(ShowCommandHandler.class);
    private final ProductManager productManager;
    public ShowCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        List<Product> sorted = productManager.getAll().stream().sorted(ProductSorter.byLocation()).collect(Collectors.toList());
        if (sorted.isEmpty()) {
            log.debug("Коллекция пуста, show не выводит элементы");
            return new Response(true, "В коллекции нет продуктов", sorted);
        } else {
            log.debug("Выведено {} элементов коллекции, отсортированных по местоположению", sorted.size());
            return new Response(true, "Элементы коллекции:\n", sorted);
        }
    }
}