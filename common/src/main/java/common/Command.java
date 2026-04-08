package common;

import java.io.Serializable;
/**
 * Интерфейс, представляющий команду, которую клиент отправляет серверу.
 * Все конкретные команды должны реализовывать этот интерфейс.
 * <p>
 * Команды сериализуются и передаются по сети.
 * </p>
 */
public interface Command extends Serializable {
    String getName();
}
