package server.handlers;

import common.Command;
import common.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandHandler;
/**
 * Обработчик команды help: возвращает справочную информацию о доступных командах.
 */
public class HelpCommandHandler implements CommandHandler {
    private static final Logger log = LogManager.getLogger(HelpCommandHandler.class);
    @Override
    public Response handle(Command command) {
        String help = """
            help: вывести справку по доступным командам
            info: вывести информацию о коллекции
            show: вывести информацию о всех продуктах
            add: добавить информацию о новом продукте
            update_by_id id: изменить информацию о продукте по его id
            remove_by_id id: удалить информацию о продукте по его id
            clear: удалить информацию о всех продуктах
            execute_script file_name: выполнить скрипт из указанного файла
            head: вывести информацию о первом в коллекции продукте
            add_if_max: добавить информацию о продукте, который дороже, чем любой другой продукт в коллекции
            add_if_min: добавить информацию о продукте, который дешевле, чем любой другой продукт в коллекции
            min_by_price: вывести информацию о любом самом дешёвом продукте в коллекции
            count_less_than_part_number partNumber: вывести число продуктов, у которых партийный номер меньше заданного
            count_greater_than_price price: вывести число продуктов, цена которых выше заданной
            exit: завершить работу программы
            """;
        log.debug("Выведена справка по командам");
        return new Response(true, help, null);
    }
}