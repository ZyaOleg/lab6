package server.handlers;

import common.Command;
import common.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
import collection.ProductManager;
import common.model.Product;
import java.util.Collection;
import java.util.IntSummaryStatistics;
/**
 * Обработчик команды info: выводит информацию о коллекции (тип, дата инициализации,
 * количество элементов, диапазон id, статистику цен).
 */
public class InfoCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(InfoCommandHandler.class);
    private final ProductManager productManager;
    public InfoCommandHandler(ProductManager productManager) {
        this.productManager = productManager;
    }
    @Override
    public Response handle(Command command) {
        StringBuilder info = new StringBuilder();
        info.append("Тип коллекции: ArrayDeque<Product>\n");
        info.append("Дата инициализации: ").append(productManager.getInitDate()).append("\n");
        info.append("Количество элементов: ").append(productManager.getSize()).append("\n");
        Collection<Product> products = productManager.getAll();
        if (!products.isEmpty()) {
            IntSummaryStatistics idStats = products.stream().mapToInt(Product::getId).summaryStatistics();
            info.append("Диапазон id: от ").append(idStats.getMin()).append(" до ").append(idStats.getMax()).append("\n");
            IntSummaryStatistics priceStats = products.stream().mapToInt(Product::getPrice).summaryStatistics();
            info.append("Цены: мин=").append(priceStats.getMin())
                    .append(", макс=").append(priceStats.getMax())
                    .append(", средняя=").append(priceStats.getAverage());
        }
        log.debug("Выведена информация о коллекции");
        return new Response(true, info.toString(), null);
    }
}