package server.handlers;

import common.Command;
import common.Response;
import common.commands.UpdateByIdCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
/**
 * Обработчик команды update_by_id: заменяет продукт с заданным id новым объектом.
 */
public class UpdateByIdCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(UpdateByIdCommandHandler.class);
    private final ProductManager productManager;
    public UpdateByIdCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        UpdateByIdCommand cmd = (UpdateByIdCommand) command;
        boolean updated = productManager.updateById(cmd.getProduct(), cmd.getId());
        if (updated) {
            log.info("Продукт с id {} обновлён", cmd.getId());
            return new Response(true, "Продукт успешно обновлён", null);
        } else {
            log.warn("Попытка обновить несуществующий продукт с id {}", cmd.getId());
            return new Response(false, "Продукт с таким id не найден", null);
        }
    }
}