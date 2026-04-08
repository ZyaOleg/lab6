package server.handlers;

import common.Command;
import common.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
/**
 * Обработчик команды exit: просто возвращает успешный ответ (клиент завершает свою работу сам).
 */
public class ExitCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(ExitCommandHandler.class);
    @Override
    public Response handle(Command command) {
        log.info("Получена команда exit от клиента");
        return new Response(true, "Завершение работы программы.", null);
    }
}