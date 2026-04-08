package common.commands;

import common.Command;
/**
 * Команда вывода всех элементов коллекции.
 * Сервер сортирует элементы по местоположению перед отправкой.
 */
public class ShowCommand implements Command {
    private static final long serialVersionUID = 1L;
    @Override
    public String getName() {return "show";}
}
