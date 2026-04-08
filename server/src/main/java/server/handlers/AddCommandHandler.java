package server.handlers;

import common.Command;
import common.Response;
import common.commands.AddCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
/**
 * Обработчик команды add: добавляет новый продукт в коллекцию.
 */
public class AddCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(AddCommandHandler.class);
    private final ProductManager productManager;
    public AddCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        AddCommand addCmd = (AddCommand) command;
        productManager.addProduct(addCmd.getProduct());
        log.info("Продукт успешно добавлен");
        return new Response(true, "Продукт успешно добавлен", null);
    }
}