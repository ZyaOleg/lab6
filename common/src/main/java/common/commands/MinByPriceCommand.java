package common.commands;

import common.Command;
/**
 * Команда вывода продукта с минимальной ценой.
 * Если таких несколько, сервер выбирает любой.
 */
public class MinByPriceCommand implements Command {
    private static final long serialVersionUID = 1L;
    @Override
    public String getName(){return "min_by_price";}
}
