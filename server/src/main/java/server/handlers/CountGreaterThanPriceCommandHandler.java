package server.handlers;

import common.Command;
import common.Response;
import common.commands.CountGreaterThanPriceCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
/**
 * Обработчик команды count_greater_than_price: подсчитывает количество продуктов с ценой выше заданной.
 */
public class CountGreaterThanPriceCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(CountGreaterThanPriceCommandHandler.class);
    private final ProductManager productManager;
    public CountGreaterThanPriceCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        CountGreaterThanPriceCommand cmd = (CountGreaterThanPriceCommand) command;
        long count = productManager.countGreaterThanPrice(cmd.getPrice());
        log.debug("Подсчитано продуктов с ценой > {}: {}", cmd.getPrice(), count);
        return new Response(true, "Количество продуктов с ценой больше " + cmd.getPrice() + ": ", count);
    }
}