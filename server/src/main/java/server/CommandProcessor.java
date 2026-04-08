package server;

import collection.ProductManager;
import common.*;
import common.commands.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.handlers.*;
import java.util.HashMap;
import java.util.Map;
/**
 * Процессор команд, реализующий {@link CommandExecutor}.
 * Сопоставляет класс команды с соответствующим обработчиком и делегирует выполнение.
 */
public class CommandProcessor implements CommandExecutor {
    private static final Logger log = LogManager.getLogger(CommandProcessor.class);
    private final Map<Class<? extends Command>, CommandHandler> handlers = new HashMap<>();
    /**
     * Создаёт процессор команд и регистрирует все доступные обработчики.
     *
     * @param productManager менеджер коллекции продуктов, необходимый большинству обработчиков
     */
    public CommandProcessor(ProductManager productManager) {
        handlers.put(AddCommand.class, new AddCommandHandler(productManager));
        handlers.put(AddIfMaxCommand.class, new AddIfMaxCommandHandler(productManager));
        handlers.put(AddIfMinCommand.class, new AddIfMinCommandHandler(productManager));
        handlers.put(ClearCommand.class, new ClearCommandHandler(productManager));
        handlers.put(CountGreaterThanPriceCommand.class, new CountGreaterThanPriceCommandHandler(productManager));
        handlers.put(CountLessThanPartNumberCommand.class, new CountLessThanPartNumberCommandHandler(productManager));
        handlers.put(ExitCommand.class, new ExitCommandHandler());
        handlers.put(HeadCommand.class, new HeadCommandHandler(productManager));
        handlers.put(HelpCommand.class, new HelpCommandHandler());
        handlers.put(InfoCommand.class, new InfoCommandHandler(productManager));
        handlers.put(MinByPriceCommand.class, new MinByPriceCommandHandler(productManager));
        handlers.put(RemoveByIdCommand.class, new RemoveByIdCommandHandler(productManager));
        handlers.put(ShowCommand.class, new ShowCommandHandler(productManager));
        handlers.put(UpdateByIdCommand.class, new UpdateByIdCommandHandler(productManager));
    }
    /**
     * Выполняет команду, выбирая соответствующий обработчик.
     * В случае отсутствия обработчика возвращает ответ с ошибкой.
     * Логирует ошибки валидации и непредвиденные исключения.
     * @param command команда для выполнения
     * @return ответ сервера
     */
    @Override
    public Response execute(Command command) {
        CommandHandler handler = handlers.get(command.getClass());
        if (handler == null) {
            log.warn("Неизвестная команда: {}", command.getClass().getSimpleName());
            return new Response(false, "Неизвестная команда", null);
        }
        log.debug("Выполнение команды: {}", command.getName());
        try {
            Response response = handler.handle(command);
            log.debug("Команда {} выполнена, успех: {}", command.getName(), response.isSuccess());
            return response;
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка при выполнении команды {}: {}", command.getName(), e.getMessage());
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при выполнении команды {}: {}", command.getName(), e.getMessage());
            return new Response(false, "Внутренняя ошибка сервера: " + e.getMessage(), null);
        }
    }

}