package server.handlers;

import common.Command;
import common.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
import common.model.Product;
/**
 * Обработчик команды min_by_price: возвращает продукт с минимальной ценой
 * (если несколько — выбирается случайный).
 */
public class MinByPriceCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(MinByPriceCommandHandler.class);
    private final ProductManager productManager;
    public MinByPriceCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        Product min = productManager.getMinByPrice();
        if (min != null) {
            log.debug("Продукт с минимальной ценой: id={}, price={}", min.getId(), min.getPrice());
            return new Response(true, "Продукт с минимальной ценой: ", min);
        } else {
            log.debug("Коллекция пуста, нет продукта с минимальной ценой");
            return new Response(false, "В коллекции нет продуктов", null);
        }
    }
}