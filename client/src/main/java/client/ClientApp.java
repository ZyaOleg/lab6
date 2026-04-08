package client;

import common.Command;
import common.CommandExecutor;
import common.Response;
import common.commands.ExecuteScriptCommand;
import common.commands.ExitCommand;
import input.InputHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Главный класс клиентского приложения.
 * Отвечает за интерактивный ввод команд, выполнение скриптов,
 * вывод ответов сервера и управление жизненным циклом клиента.
 */
public class ClientApp {
    private final CommandExecutor executor;
    private final Scanner scanner;
    private final InputHandler inputHandler;
    private boolean exitRequested = false; // флаг для завершения при exit в скрипте

    /**
     * Конструктор клиентского приложения.
     *
     * @param executor исполнитель команд (отправляет команды на сервер и получает ответы)
     */
    public ClientApp(CommandExecutor executor) {
        this.executor = executor;
        this.scanner = new Scanner(System.in);
        this.inputHandler = new InputHandler(scanner, false);
    }

    /**
     * Запускает основной цикл обработки команд.
     */
    public void start() {
        System.out.println("Чтобы вывести полный список команд, введите команду help.");
        while (true) {
            if (exitRequested) break;

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            try {
                Command command = new ClientCommandFactory(inputHandler).createCommand(line);
                if (command instanceof ExecuteScriptCommand) {
                    executeScript((ExecuteScriptCommand) command);
                    if (exitRequested) break;
                    continue;
                }
                Response response = executor.execute(command);
                if (response == null) {
                    System.err.println("Сервер не вернул ответ. Попробуйте позже.");
                    continue;
                }
                printResponse(response, "");
                if (command instanceof ExitCommand) break;
            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }
        scanner.close();
        System.out.println("Работа клиента завершена.");
    }

    private void executeScript(ExecuteScriptCommand cmd) {
        runScript(cmd.getFilename(), new HashSet<>());
    }

    /**
     * Рекурсивно выполняет команды из файла-скрипта.
     *
     * @param filename     имя файла скрипта
     * @param calledFiles  множество уже вызванных файлов (для обнаружения рекурсии)
     * @return true, если скрипт выполнен без ошибок, false – при ошибке или рекурсии
     */
    private void runScript(String filename, Set<String> calledFiles) {
        if (exitRequested) return;

        if (calledFiles.contains(filename)) {
            System.err.println("Обнаружена рекурсия: скрипт " + filename + " уже выполняется в текущей цепочке. Вызов пропущен.");
            return;
        }
        Set<String> newCalled = new HashSet<>(calledFiles);
        newCalled.add(filename);
        try (Scanner fileScanner = new Scanner(new File(filename), "UTF-8")) {
            InputHandler scriptHandler = new InputHandler(fileScanner, true);
            ClientCommandFactory scriptFactory = new ClientCommandFactory(scriptHandler);
            int lineNum = 0;
            while (fileScanner.hasNextLine()) {
                if (exitRequested) break;

                String line = fileScanner.nextLine().trim();
                lineNum++;
                if (line.isEmpty()) continue;
                try {
                    Command scriptCommand = scriptFactory.createCommand(line);
                    if (scriptCommand instanceof ExecuteScriptCommand) {
                        runScript(((ExecuteScriptCommand) scriptCommand).getFilename(), newCalled);
                        continue;
                    }
                    Response resp = executor.execute(scriptCommand);
                    String prefix = "[Скрипт " + filename + " стр." + lineNum + "]\n";
                    printResponse(resp, prefix);
                    if (scriptCommand instanceof ExitCommand) {
                        exitRequested = true;
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("[Скрипт " + filename + " стр." + lineNum + "]\nОшибка: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + filename);
        }
    }

    /**
     * Универсальный метод вывода ответа сервера.
     *
     * @param response ответ сервера
     * @param prefix   строка, добавляемая перед каждым выводом (например, "[Скрипт ...]")
     */
    private void printResponse(Response response, String prefix) {
        if (response.isSuccess()) {
            String msg = response.getMessage();
            if (msg.endsWith("\n")) {
                msg = msg.substring(0, msg.length() - 1);
            }
            System.out.print(prefix + msg);
            if (response.getData() != null) {
                if (response.getData() instanceof Collection) {
                    System.out.println();
                    ((Collection<?>) response.getData()).forEach(item -> System.out.println(prefix + item));
                } else {
                    System.out.println(" " + response.getData());
                }
            } else {
                System.out.println();
            }
        } else {
            System.err.print(prefix + "Ошибка: " + response.getMessage());
            if (response.getData() != null) {
                System.err.println(prefix + response.getData());
            }
        }
    }

    /**
     * Точка входа в клиентское приложение.
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 6527;
        CommandExecutor executor = new TcpClientCommandExecutor(host, port);
        ClientApp client = new ClientApp(executor);
        client.start();
    }
}