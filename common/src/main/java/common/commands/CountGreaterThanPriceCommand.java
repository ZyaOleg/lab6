package common.commands;

import common.Command;
/**
 * Команда подсчёта количества продуктов с ценой выше заданной.
 */
public class CountGreaterThanPriceCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final int price;
    public CountGreaterThanPriceCommand(int price) {this.price=price;}
    public int getPrice() {return price;}
    @Override
    public String getName(){return "count_greater_than_price";}
}

