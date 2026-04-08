package server.handlers;

import common.Command;
import common.Response;
import common.commands.RemoveByIdCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
/**
 * Обработчик команды remove_by_id: удаляет продукт по идентификатору.
 */
public class RemoveByIdCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(RemoveByIdCommandHandler.class);
    private final ProductManager productManager;
    public RemoveByIdCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        RemoveByIdCommand cmd = (RemoveByIdCommand) command;
        boolean removed = productManager.removeById(cmd.getId());
        if (removed) {
            log.info("Продукт с id {} удалён", cmd.getId());
            return new Response(true, "Продукт успешно удалён", null);
        } else {
            log.warn("Попытка удалить несуществующий продукт с id {}", cmd.getId());
            return new Response(false, "Продукт с таким id не найден", null);
        }
    }
}