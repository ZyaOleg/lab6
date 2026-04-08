package common.commands;

import common.Command;
/**
 * Команда удаления продукта по идентификатору.
 */
public class RemoveByIdCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final int id;
    public RemoveByIdCommand(int id) {this.id=id;}
    public int getId() {return id;}
    @Override
    public String getName(){return "remove_by_id";}
}
