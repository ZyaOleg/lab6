package common.commands;

import common.Command;
import common.model.Product;
/**
 * Команда добавления продукта, если он больше (дороже) всех существующих.
 */
public class AddIfMaxCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final Product product;
    public AddIfMaxCommand(Product product){this.product=product;}
    public Product getProduct() {return product;}
    @Override
    public String getName(){return "add_if_max";}
}
