package client;

import common.Command;
import input.InputHandler;
import common.model.*;
import common.commands.*;
import java.util.HashMap;
import java.util.Map;
/**
 * Фабрика для создания объектов команд на основе строки ввода.
 * Разбирает имя команды и аргументы, при необходимости запрашивает
 * данные объекта Product через {@link InputHandler}.
 */
public class ClientCommandFactory {
    private final Map<String, CommandCreator> creators = new HashMap<>();
    private final Map<String, Boolean> requiresArgument = new HashMap<>();
    private final InputHandler inputHandler;

    public ClientCommandFactory(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
        initCreators();
        initRequiresArgument();
    }
    /**
     * Инициализирует маппинг имён команд на их создатели.
     */
    private void initCreators() {
        creators.put("add", (arg, ih) -> new AddCommand(readProduct(ih)));
        creators.put("add_if_max", (arg, ih) -> new AddIfMaxCommand(readProduct(ih)));
        creators.put("add_if_min", (arg, ih) -> new AddIfMinCommand(readProduct(ih)));
        creators.put("remove_by_id", (arg, ih) -> new RemoveByIdCommand(parseInt(arg, "id")));
        creators.put("update_by_id", (arg, ih) -> new UpdateByIdCommand(parseInt(arg, "id"), readProduct(ih)));
        creators.put("count_greater_than_price", (arg, ih) -> new CountGreaterThanPriceCommand(parseInt(arg, "price")));
        creators.put("count_less_than_part_number", (arg, ih) -> new CountLessThanPartNumberCommand(arg));
        creators.put("execute_script", (arg, ih) -> new ExecuteScriptCommand(arg));
        creators.put("show", (arg, ih) -> new ShowCommand());
        creators.put("head", (arg, ih) -> new HeadCommand());
        creators.put("min_by_price", (arg, ih) -> new MinByPriceCommand());
        creators.put("clear", (arg, ih) -> new ClearCommand());
        creators.put("info", (arg, ih) -> new InfoCommand());
        creators.put("help", (arg, ih) -> new HelpCommand());
        creators.put("exit", (arg, ih) -> new ExitCommand());
    }
    /**
     * Инициализирует карту команд, требующих аргумент.
     */
    private void initRequiresArgument() {
        requiresArgument.put("remove_by_id", true);
        requiresArgument.put("update_by_id", true);
        requiresArgument.put("count_greater_than_price", true);
        requiresArgument.put("count_less_than_part_number", true);
        requiresArgument.put("execute_script", true);
    }
    /**
     * Создаёт команду по строке ввода.
     *
     * @param line строка, содержащая имя команды и, возможно, аргумент
     * @return объект команды
     * @throws Exception если команда неизвестна, аргументы не соответствуют ожиданиям или ввод некорректен
     */
    public Command createCommand(String line) throws Exception {
        String[] parts = line.trim().split("\\s+");
        String cmdName = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : null;

        CommandCreator creator = creators.get(cmdName);
        if (creator == null) {
            throw new IllegalArgumentException("Неизвестная команда: " + cmdName);
        }
        else {
            boolean required = requiresArgument.getOrDefault(cmdName, false);
            if (required && argument == null) {
                throw new IllegalArgumentException("Команда " + cmdName + " требует аргумент");
            }
            if (!required && argument != null) {
                throw new IllegalArgumentException("Команда " + cmdName + " не принимает аргументы");
            }
        }
        return creator.create(argument, inputHandler);
    }
    /**
     * Разбирает целое положительное число из строки.
     *
     * @param arg       строковое представление числа
     * @param fieldName имя поля (для сообщения об ошибке)
     * @return положительное целое число
     * @throws IllegalArgumentException если число не целое или не положительное
     */
    private int parseInt(String arg, String fieldName) {
        try {
            int value = Integer.parseInt(arg);
            if (value <= 0) {
                throw new IllegalArgumentException(fieldName + " должен быть положительным целым числом");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " должен быть целым числом");
        }
    }
    /**
     * Читает объект Product из ввода пользователя.
     *
     * @param inputHandler обработчик ввода
     * @return созданный продукт
     */
    private Product readProduct(InputHandler inputHandler) {
        String productName = inputHandler.readRequiredString("product name");
        float coordinateX = inputHandler.readFloat("coordinate x", null, null);
        float coordinateY = inputHandler.readFloat("coordinate y", null, 557F);
        int price = inputHandler.readInt("price", 1, null);

        // уникальность - ответственность сервера
        String partNumber;
        partNumber = inputHandler.readNullableStringWithLength("part number", 10, 56);

        Long manufactureCost = inputHandler.readLong("manufacture cost", null, null);
        UnitOfMeasure unitOfMeasure = inputHandler.readEnum("unit of measure", UnitOfMeasure.class, true);

        String organizationName = inputHandler.readRequiredString("organization name");
        long annualTurnover = inputHandler.readLong("annual turnover", 1L, null);
        OrganizationType organizationType = inputHandler.readEnum("organization type", OrganizationType.class, true);
        String addressStreet = inputHandler.readNullableString("address street");
        String zipCode = inputHandler.readNullableStringWithLength("zip code", 6, null);
        double locationX = inputHandler.readDouble("location x", null, null);
        float locationY = inputHandler.readFloat("location y", null, null);
        float locationZ = inputHandler.readFloat("location z", null, null);
        String locationName = inputHandler.readNullableString("location name");

        Location town = new Location(locationX, locationY, locationZ, locationName);
        Address postalAddress = new Address(addressStreet, zipCode, town);
        Organization manufacturer = new Organization(organizationName, annualTurnover, organizationType, postalAddress);
        Coordinates coordinates = new Coordinates(coordinateX, coordinateY);

        return new Product(productName, coordinates, price, partNumber, manufactureCost, unitOfMeasure, manufacturer);
    }
}