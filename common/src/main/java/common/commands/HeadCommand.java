package common.commands;

import common.Command;
/**
 * Команда вывода первого элемента коллекции.
 * Порядок сортировки определяется сервером (по заданию – по местоположению).
 */
public class HeadCommand implements Command {
    private static final long serialVersionUID = 1L;
    @Override
    public String getName() {return "head";}
}
