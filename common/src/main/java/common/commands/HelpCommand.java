package common.commands;

import common.Command;
/**
 * Команда вывода справки по доступным командам.
 */
public class HelpCommand implements Command {
    private static final long serialVersionUID = 1L;
    @Override
    public String getName() {return "help";}
}
