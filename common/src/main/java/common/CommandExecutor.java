package common;

/**
 * Интерфейс исполнителя команд.
 * Реализации этого интерфейса отвечают за выполнение команды
 * (на клиенте – отправка на сервер, на сервере – выполнение логики).
 */
public interface CommandExecutor {
    Response execute(Command command);
}