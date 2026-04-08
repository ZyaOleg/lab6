package client;

import common.Command;
import input.InputHandler;
/**
 * Функциональный интерфейс для создания команды.
 * Используется в {@link ClientCommandFactory} для лямбда-выражений.
 */
@FunctionalInterface
public interface CommandCreator {
    Command create(String argument, InputHandler inputHandler) throws Exception;
}