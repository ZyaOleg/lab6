package server;

import common.Command;
import common.Response;
/**
 * Интерфейс обработчика команд на сервере.
 * Реализации этого интерфейса обрабатывают конкретные типы команд и возвращают ответ.
 */
public interface CommandHandler {
    Response handle(Command command);
}
