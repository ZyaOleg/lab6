package common.commands;

import common.Command;
/**
 * Команда очистки коллекции (удаления всех элементов).
 */
public class ClearCommand implements Command {
    private static final long serialVersionUID = 1L;
    @Override
    public String getName() {return "clear";}
}
