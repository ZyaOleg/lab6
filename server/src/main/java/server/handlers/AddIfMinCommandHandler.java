package server.handlers;

import common.Command;
import common.Response;
import common.commands.AddIfMinCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
import common.model.Product;
/**
 * Обработчик команды add_if_min: добавляет продукт, если он меньше (дешевле) всех существующих.
 */
public class AddIfMinCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(AddIfMinCommandHandler.class);
    private final ProductManager productManager;
    public AddIfMinCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        AddIfMinCommand cmd = (AddIfMinCommand) command;
        Product min = productManager.getMin();
        if (min == null || cmd.getProduct().compareTo(min) < 0) {
            productManager.addProduct(cmd.getProduct());
            log.info("Продукт добавлен (add_if_min)");
            return new Response(true, "Продукт успешно добавлен", null);
        } else {
            log.debug("Условие add_if_min не выполнено, продукт не добавлен");
            return new Response(false, "Условие не выполнено, продукт не добавлен", null);
        }
    }
}