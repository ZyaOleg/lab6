package server.handlers;

import common.Command;
import common.Response;
import common.commands.AddIfMaxCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
import common.model.Product;
/**
 * Обработчик команды add_if_max: добавляет продукт, если он больше (дороже) всех существующих.
 */
public class AddIfMaxCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(AddIfMaxCommandHandler.class);
    private final ProductManager productManager;
    public AddIfMaxCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        AddIfMaxCommand cmd = (AddIfMaxCommand) command;
        Product max = productManager.getMax();
        if (max == null || cmd.getProduct().compareTo(max) > 0) {
            productManager.addProduct(cmd.getProduct());
            log.info("Продукт добавлен (add_if_max)");
            return new Response(true, "Продукт успешно добавлен", null);
        } else {
            log.debug("Условие add_if_max не выполнено, продукт не добавлен");
            return new Response(false, "Условие не выполнено, продукт не добавлен", null);
        }
    }
}