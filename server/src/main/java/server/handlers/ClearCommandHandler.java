package server.handlers;

import common.Command;
import common.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
/**
 * Обработчик команды clear: полностью очищает коллекцию.
 */
public class ClearCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(ClearCommandHandler.class);
    private final ProductManager productManager;
    public ClearCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        if (productManager.getAll().isEmpty()) {
            log.debug("Коллекция уже пуста, clear не требуется");
            return new Response(true, "Коллекция уже пуста", null);
        } else {
            productManager.clear();
            log.info("Коллекция очищена");
            return new Response(true, "Коллекция очищена", null);
        }
    }
}