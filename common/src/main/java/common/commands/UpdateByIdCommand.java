package common.commands;

import common.Command;
import common.model.Product;
/**
 * Команда обновления продукта по идентификатору.
 * Заменяет существующий продукт новым объектом.
 */
public class UpdateByIdCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final Product product;
    public UpdateByIdCommand(int id, Product product) {this.id=id; this.product=product;}
    public int getId() {return id;}
    public Product getProduct() {
        return product;
    }
    @Override
    public String getName(){return "update_by_id";}

}
