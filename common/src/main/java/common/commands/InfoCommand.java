package common.commands;

import common.Command;
/**
 * Команда вывода информации о коллекции (тип, дата инициализации, количество элементов, статистика).
 */
public class InfoCommand implements Command {
    private static final long serialVersionUID = 1L;
    @Override
    public String getName() {return "info";}
}
