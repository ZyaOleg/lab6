package common.commands;

import common.Command;
import common.model.Product;
/**
 * Команда добавления нового продукта в коллекцию.
 */
public class AddCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final Product product;
    public AddCommand(Product product){this.product=product;}
    public Product getProduct() {return product;}
    @Override
    public String getName(){return "add";}
}
