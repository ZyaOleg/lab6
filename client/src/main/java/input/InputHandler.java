package input;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Управляет вводом в интерактивном и скриптовом режимах.
 * Умеет читать строки, числа, enum с проверками и повторными запросами.
 */
public class InputHandler {
    private static final Logger log = LogManager.getLogger(InputHandler.class);
    private final Scanner scanner;
    private final boolean scriptMode;

    public InputHandler (Scanner scanner, boolean scriptMode) {
        this.scanner = scanner;
        this.scriptMode = scriptMode;
    }

    public String readString(String field, boolean allowNull, Integer minLength, Integer maxLength){
        if (scriptMode) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                if (allowNull) return null;
                throw new InputException(field + " не может быть пустым.");
            } else {
                if (minLength != null && line.length() < minLength) {
                    throw new InputException("Длина " + field + " должна быть не менее " + minLength + (allowNull ? " (чтобы пропустить заполнение, введите пустую строку)." : "."));
                }
                if (maxLength != null && line.length() > maxLength) {
                    throw new InputException("Длина " + field + " должна быть не более " + maxLength + (allowNull ? " (чтобы пропустить заполнение, введите пустую строку)." : "."));
                }
                return line;
            }
        }
        else {
            log.info("Введите {}{}", field, allowNull ? " (чтобы пропустить заполнение, нажмите Enter): " : ": ");
            while (true) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    if (allowNull) return null;
                    log.info("{} не может отсутствовать. Попробуйте ввести ещё раз: ", field);
                    continue;
                }
                if (minLength != null && line.length() < minLength) {
                    log.info("Длина {} должна быть не менее {}. Попробуйте ввести ещё раз{}", field, minLength, allowNull ? " (чтобы пропустить заполнение, нажмите Enter): " : ": ");
                    continue;
                }
                if (maxLength != null && line.length() > maxLength) {
                    log.info("Длина {} должна быть не более {}. Попробуйте ввести ещё раз{}", field, maxLength, allowNull ? " (чтобы пропустить заполнение, нажмите Enter): " : ": ");
                    continue;
                }
                return line;
            }
        }
    }

    public String readNullableString(String field) {
        return readString(field, true, null, null);
    }
    public String readRequiredString(String field) {
        return readString(field, false, null, null);
    }
    public String readNullableStringWithLength(String field, Integer minLength, Integer maxLength) {
        return readString(field, true, minLength, maxLength);
    }
    public String readStringWithLength(String field, Integer minLength, Integer maxLength) {
        return readString(field, false, minLength, maxLength);
    }

    private <T extends Number> T readNumber(String field, Function<String, T> parser,
                                            Number min, Number max, String typeValue) {
        if (scriptMode) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty())
                throw new InputException("Значение " + field + "не может быть пустым.");
            try {
                T value = parser.apply(line);
                if (min != null && value.doubleValue() < min.doubleValue())
                    throw new InputException("Значение " + field + " должно быть не меньше " + min + ".");
                if (max != null && value.doubleValue() > max.doubleValue())
                    throw new InputException("Значение " + field + " должно быть не больше " + max + ".");
                return value;
            } catch (NumberFormatException e) {
                throw new InputException(field + " должно иметь " + typeValue + ".");
            }
        } else {
            log.info("Введите {}: ", field);
            while (true) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    log.info("Значение {} не может быть пустым. Попробуйте ввести ещё раз: ", field);
                    continue;
                }
                try {
                    T value = parser.apply(line);
                    if (min != null && value.doubleValue() < min.doubleValue()) {
                        log.info("Значение {} должно быть не меньше {}. Попробуйте ввести ещё раз: ", field, min);
                        continue;
                    }
                    if (max != null && value.doubleValue() > max.doubleValue()) {
                        log.info("Значение {} должно быть не больше {}. Попробуйте ввести ещё раз: ", field, max);
                        continue;
                    }
                    return value;
                } catch (NumberFormatException e) {
                    log.info("{} должен быть {}. Попробуйте ввести ещё раз: ", field, typeValue);
                }
            }
        }
    }

    // Специализированные методы, использующие readNumber
    public int readInt(String field, Integer min, Integer max) {
        return readNumber(field, Integer::parseInt, min, max, "целым числом");
    }

    public long readLong(String field, Long min, Long max) {
        return readNumber(field, Long::parseLong, min, max, "целым числом");
    }

    public double readDouble(String field, Double min, Double max) {
        return readNumber(field, Double::parseDouble, min, max, "дробным числом");
    }

    public float readFloat(String field, Float min, Float max) {
        return readNumber(field, Float::parseFloat, min, max, "дробным числом");
    }

    public <T extends Enum<T>> T readEnum(String field, Class<T> enumClass, boolean allowNull) {
        String enumValues = String.join(", ", Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toArray(String[]::new));
        if (scriptMode) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                if (allowNull) return null;
                throw new InputException("Значение " + field + " не может быть пустым. Допустимы: " + enumValues + ".");
            }
            try {
                return Enum.valueOf(enumClass, line.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InputException("Некорректное значение " + field + ". Допустимы: " + enumValues + (allowNull ? " (чтобы пропустить заполнение, введите пустую строку)." : "."));
            }
        } else {
            log.info("Введите одно из возможных значений: {}{}", enumValues, allowNull ? " (чтобы пропустить заполнение, введите пустую строку)." : ".");
            while (true) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    if (allowNull) return null;
                    log.info("Значение {} не может быть пустым. Попробуйте ввести одно из возможных значений ещё раз: {}.", field, enumValues);
                    continue;
                }
                try {
                    return Enum.valueOf(enumClass, line.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.info("Некорректное значение {}. Попробуйте ввести одно из возможных значений ещё раз: {}{}", field, enumValues, allowNull ? " (чтобы пропустить заполнение, нажмите Enter)." : ".");
                }
            }
        }
    }
}
