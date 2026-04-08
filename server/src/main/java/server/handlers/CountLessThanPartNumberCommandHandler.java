package server.handlers;

import common.Command;
import common.Response;
import common.commands.CountLessThanPartNumberCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
/**
 * Обработчик команды count_less_than_part_number: подсчитывает количество продуктов,
 * у которых partNumber лексикографически меньше заданного.
 */
public class CountLessThanPartNumberCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(CountLessThanPartNumberCommandHandler.class);
    private final ProductManager productManager;
    public CountLessThanPartNumberCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        CountLessThanPartNumberCommand cmd = (CountLessThanPartNumberCommand) command;
        long count = productManager.countLessThanPartNumber(cmd.getPartNumber());
        log.debug("Подсчитано продуктов с partNumber < '{}': {}", cmd.getPartNumber(), count);
        return new Response(true, "Количество продуктов с partNumber меньше '" + cmd.getPartNumber() + "': ", count);
    }
}