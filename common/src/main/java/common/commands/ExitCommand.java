package common.commands;

import common.Command;
/**
 * Команда завершения работы клиентского приложения.
 * Сервер просто возвращает ответ, клиент после получения завершает свою работу.
 */
public class ExitCommand implements Command {
    private static final long serialVersionUID = 1L;
    @Override
    public String getName() {return "exit";}
}
