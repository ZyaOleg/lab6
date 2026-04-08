package collection;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Set;
/**
 * Генерирует уникальные id, может переиспользовать освобождённые.
 */
public class IdGenerator {
    private int nextId = 1;
    private final PriorityQueue<Integer> freeIds = new PriorityQueue<>();
    private int maxUsedId = 0;

    /**
     * Определяет все id от 1 до максимального использованного,
     * исключая переданные использованные id.
     * @param usedIds множество использованных идентификаторов.
     */
    public void init(Set<Integer> usedIds) {
        if (usedIds == null || usedIds.isEmpty()) {
            nextId = 1;
            maxUsedId = 0;
            freeIds.clear();
        }
        maxUsedId = Collections.max(usedIds);
        freeIds.clear();
        for (int id = 1; id <= maxUsedId; id++) {
            if (!usedIds.contains(id)) {
                freeIds.offer(id);
            }
        }
        nextId = maxUsedId + 1;
    }

    /**
     * Генерирует следующий уникальный id.
     * @return новый id.
     * @throws RuntimeException если id переполнены.
     */
    public int generateId() {
        if (!freeIds.isEmpty()) {
            return freeIds.poll();
        }
        if (nextId < 0) {
            throw new RuntimeException("Достигнут предел идентификаторов.");
        }
        return nextId++;
    }

    /**
     * Освобождает id для повторного использования.
     * @param id идентификатор, который больше не используется.
     */
    public void freeId(int id) {
        if (id <= maxUsedId || id < nextId) {
            freeIds.offer(id);
        }
    }

    /**
     * Устанавливает следующий id (если переданное значение больше текущего nextId).
     * Используется при загрузке из файла.
     * @param id максимальный встреченный id.
     */
    public void setNextId(int id) {
        if (id >= nextId) {
            nextId = id + 1;
        }
        if (id > maxUsedId) {
            maxUsedId = id;
        }
    }
}