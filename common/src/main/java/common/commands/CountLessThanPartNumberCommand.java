package common.commands;

import common.Command;
/**
 * Команда подсчёта количества продуктов, у которых partNumber
 * лексикографически меньше заданного.
 */
public class CountLessThanPartNumberCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final String partNumber;
    public CountLessThanPartNumberCommand(String partNumber) {this.partNumber = partNumber;}

    public String getPartNumber() {
        return partNumber;
    }

    @Override
    public String getName() {return "count_less_than_part_number";}
}
