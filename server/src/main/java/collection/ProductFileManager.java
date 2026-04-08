package collection;

import common.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
/**
 * Загрузка/сохранение коллекции в CSV. Кодировка UTF-8.
 * При загрузке проверяет дубликаты id и partNumber.
 */
public class ProductFileManager {
    private static final Logger log = LogManager.getLogger(ProductFileManager.class);
    private final String filePath;
    private final IdGenerator productIdGenerator;
    private final IdGenerator organizationIdGenerator;
    private final HashSet<String> partNumbers;
    private final HashSet<Integer> productIds = new HashSet<>();
    private final HashSet<Integer> organizationIds = new HashSet<>();

    public ProductFileManager(String filePath, IdGenerator productIdGenerator,
                              IdGenerator organizationIdGenerator, HashSet<String> partNumbers) {
        this.filePath = filePath;
        this.productIdGenerator = productIdGenerator;
        this.organizationIdGenerator = organizationIdGenerator;
        this.partNumbers = partNumbers;
    }

    public ArrayDeque<Product> loadFromFile() throws IOException {
        ArrayDeque<Product> collection = new ArrayDeque<>();
        StringBuilder errors = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != 19) {
                    errors.append("Строка " + lineNumber + ": нарушена целостность данных. Ожидалось 19 полей, получено " + parts.length + ".\n");
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    if (productIds.contains(id)) {
                        errors.append("Строка " + lineNumber + ": дубликат id продукта " + id + ".\n");
                        continue;
                    }
                    productIds.add(id);
                    String name = parts[1].trim();
                    float coordinateX = Float.parseFloat(parts[2].trim());
                    float coordinateY = Float.parseFloat(parts[3].trim());
                    long creationTime = Long.parseLong(parts[4].trim());
                    int price = Integer.parseInt(parts[5].trim());
                    String partNumber = parts[6].trim();
                    if (partNumber.isEmpty()) partNumber = null;
                    Long manufactureCost = parts[7].trim().isEmpty() ? null : Long.parseLong(parts[7].trim());
                    String unitOfMeasureStr = parts[8].trim();
                    UnitOfMeasure unitOfMeasure = unitOfMeasureStr.isEmpty() ? null : UnitOfMeasure.valueOf(unitOfMeasureStr);

                    int organizationId = Integer.parseInt(parts[9].trim());
                    if (organizationIds.contains(organizationId)) {
                        errors.append("Строка " + lineNumber + ": дубликат id организации " + organizationId + ".\n");
                        continue;
                    }
                    organizationIds.add(organizationId);
                    String organizationName = parts[10].trim();
                    long annualTurnover = Long.parseLong(parts[11].trim());
                    String organizationTypeStr = parts[12].trim();
                    OrganizationType organizationType = organizationTypeStr.isEmpty() ? null : OrganizationType.valueOf(organizationTypeStr);
                    String street = parts[13].trim();
                    if (street.isEmpty()) street = null;
                    String zipCode = parts[14].trim();
                    if (zipCode.isEmpty()) zipCode = null;
                    double townX = Double.parseDouble(parts[15].trim());
                    float townY = Float.parseFloat(parts[16].trim());
                    float townZ = Float.parseFloat(parts[17].trim());
                    String townName = parts[18].trim();
                    if (townName.isEmpty()) townName = null;

                    Location town = new Location(townX, townY, townZ, townName);
                    Address postalAddress = new Address(street, zipCode, town);
                    Organization manufacturer = new Organization(organizationName, annualTurnover, organizationType, postalAddress);
                    manufacturer.setId(organizationId);
                    Coordinates coordinates = new Coordinates(coordinateX, coordinateY);
                    Date creationDate = new Date(creationTime);
                    Product product = new Product(name, coordinates, price, partNumber, manufactureCost, unitOfMeasure, manufacturer);
                    product.setId(id);
                    product.setCreationDate(creationDate);

                    product.validate();
                    if (partNumber != null && partNumbers.contains(partNumber)) {
                        errors.append("Строка " + lineNumber + ": partNumber " + partNumber + " уже существует.\n");
                        continue;
                    }
                    collection.add(product);
                    partNumbers.add(partNumber);
                } catch (Exception e) {
                    errors.append("Строка " + lineNumber + ": ошибка разбора - " + e.getMessage() + ".\n");
                }
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Файл не найден: " + filePath);
        }

        if (!errors.isEmpty()) {
            throw new IOException("Ошибки при загрузке файла:\n" + errors.toString());
        }

        return collection;
    }

    public void saveToFile(Collection<Product> collection) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            for (Product product : collection) {
                writer.write(formatProduct(product) + "\n");
            }
        }
        catch (IOException e) {
            log.error("Не удалось сохранить файл: {}", e.getMessage());
        }
    }

    private String formatProduct(Product product) {
        Organization manufacturer = product.getManufacturer();
        Address postalAddress = manufacturer.getPostalAddress();
        Location town = postalAddress != null ? postalAddress.getTown() : null;
        String street = (postalAddress == null || postalAddress.getStreet() == null) ? "" : escapeCsv(postalAddress.getStreet());
        String zipCode = postalAddress == null || postalAddress.getZipCode() == null ? "" : escapeCsv(postalAddress.getZipCode());
        String townX = (town == null) ? "" : String.valueOf(town.getX());
        String townY = (town == null) ? "" : String.valueOf(town.getY());
        String townZ = (town == null) ? "" : String.valueOf(town.getZ());
        String townName = (town == null || town.getName() == null) ? "" : escapeCsv(town.getName());
        return String.join(",",
                String.valueOf(product.getId()),
                escapeCsv(product.getName()),
                String.valueOf(product.getCoordinates().getX()),
                String.valueOf(product.getCoordinates().getY()),
                String.valueOf(product.getCreationDate().getTime()),
                String.valueOf(product.getPrice()),
                product.getPartNumber() == null ? "" : product.getPartNumber(),
                product.getManufactureCost() == null ? "" : String.valueOf(product.getManufactureCost()),
                product.getUnitOfMeasure() == null ? "" : product.getUnitOfMeasure().name(),
                String.valueOf(manufacturer.getId()),
                escapeCsv(manufacturer.getName()),
                String.valueOf(manufacturer.getAnnualTurnover()),
                manufacturer.getType() == null ? "" : manufacturer.getType().name(),
                street, zipCode,
                townX, townY, townZ, townName
        );
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}