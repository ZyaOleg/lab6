package common;

import java.io.Serializable;
/**
 * Ответ сервера на выполнение команды.
 * Содержит флаг успеха, сообщение и дополнительные данные (например, объекты коллекции).
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private final boolean success;
    private final String message;
    private final Object data;
    public Response(boolean success, String message, Object data) {this.success= success; this.message = message; this.data = data;}
    /**
     * Возвращает дополнительные данные ответа.
     *
     * @return объект с данными (например, коллекция продуктов, один продукт, число и т.д.)
     */
    public Object getData() {
        return data;
    }
    /**
     * Возвращает текстовое сообщение ответа.
     *
     * @return сообщение (всегда заканчивается символом новой строки)
     */
    public String getMessage() {
        return message+"\n";
    }

    public boolean isSuccess() {
        return success;
    }
}
