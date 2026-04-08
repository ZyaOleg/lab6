package collection;

import common.model.Product;
import server.ProductSorter;

import java.util.*;
import java.util.stream.Collectors;
/**
 * Управляет коллекцией ArrayDeque<Product>.
 * Содержит методы добавления, удаления, обновления, получения статистики.
 */
public class ProductManager {
    private final ArrayDeque<Product> collection = new ArrayDeque<>();
    private final Date initDate = new Date();
    private final IdGenerator productIdGenerator;
    private final IdGenerator organizationIdGenerator;
    private final HashSet<String> partNumbers = new HashSet<>();
    private final Random random = new Random();

    public ProductManager(IdGenerator productIdGenerator, IdGenerator organizationIdGenerator) {
        this.productIdGenerator = productIdGenerator;
        this.organizationIdGenerator = organizationIdGenerator;
    }

    public void loadCollection(ArrayDeque<Product> loaded) {
        collection.clear();
        partNumbers.clear();

        Set<Integer> productIds = new HashSet<>();
        Set<Integer> organizationIds = new HashSet<>();

        for (Product product : loaded) {
            collection.add(product);
            if (product.getPartNumber() != null) {
                partNumbers.add(product.getPartNumber());
            }
            productIds.add(product.getId());
            organizationIds.add(product.getManufacturer().getId());
        }
        if (!collection.isEmpty()) {
            productIdGenerator.init(productIds);
            organizationIdGenerator.init(organizationIds);
        }
    }

    public void addProduct(Product product) {
        int productId = productIdGenerator.generateId();
        product.setId(productId);
        product.setCreationDate(new Date());
        if (product.getManufacturer().getId() == 0) {
            product.getManufacturer().setId(organizationIdGenerator.generateId());
        }
        product.validate();
        if (product.getPartNumber() != null) {
            if (partNumbers.contains(product.getPartNumber())) {
                throw new IllegalArgumentException("Такой серийный номер уже существует.");
            }
            partNumbers.add(product.getPartNumber());
        }
        collection.add(product);
    }

    public boolean removeById(int id) {
        Iterator<Product> it = collection.iterator();
        while (it.hasNext()) {
            Product p = it.next();
            if (p.getId() == id) {
                it.remove();
                if (p.getPartNumber() != null) {
                    partNumbers.remove(p.getPartNumber());
                }
                productIdGenerator.freeId(id);
                return true;
            }
        }
        return false;
    }

    public boolean updateById(Product newProduct, int id) {
        for (Product product : collection) {
            if (product.getId() == id) {
                if (newProduct.getManufacturer().getId() == 0) {
                    newProduct.getManufacturer().setId(organizationIdGenerator.generateId());
                }
                String oldPartNumber = product.getPartNumber();
                String newPartNumber = newProduct.getPartNumber();
                if (newPartNumber != null && !newPartNumber.equals(oldPartNumber) && partNumbers.contains(newPartNumber)) {
                    throw new IllegalArgumentException("Такой серийный номер уже существует.\n");
                }
                product.setName(newProduct.getName());
                product.setCoordinates(newProduct.getCoordinates());
                product.setPrice(newProduct.getPrice());
                product.setPartNumber(newPartNumber);
                product.setManufactureCost(newProduct.getManufactureCost());
                product.setUnitOfMeasure(newProduct.getUnitOfMeasure());
                product.setManufacturer(newProduct.getManufacturer());
                product.setCreationDate(new Date());
                product.validate();
                if (oldPartNumber != null && !oldPartNumber.equals(newPartNumber)) {
                    partNumbers.remove(oldPartNumber);
                }
                if (newPartNumber != null) {
                    partNumbers.add(newPartNumber);
                }
                return true;
            }
        }
        return false;
    }

    public void clear() {
        collection.clear();
        partNumbers.clear();
    }

    public Collection<Product> getAll() {
        return Collections.unmodifiableCollection(collection);
    }

    public List<Product> getSorted() {
        return collection.stream().sorted().collect(Collectors.toList());}

    public boolean productInCollection(int id) {
        return collection.stream().anyMatch(p -> p.getId() == id);
    }

    public Product getProductById(int id) {
        return collection.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public boolean isPartNumberExists(String partNumber) {
        return partNumber != null && partNumbers.contains(partNumber);
    }

    public HashSet<String> getPartNumbers() {
        return partNumbers;
    }

    public Date getInitDate() {
        return initDate;
    }

    public int getSize() {
        return collection.size();
    }

    public Product getFirstByLocation() {
        return collection.stream().min(ProductSorter.byLocation()).orElse(null);
    }

    public Product getMax() {
        return collection.stream().max(Comparator.naturalOrder()).orElse(null);
    }

    public Product getMin() {
        return collection.stream().min(Comparator.naturalOrder()).orElse(null);
    }

    public Product getMinByPrice() {
        return collection.stream().min(Comparator.comparingInt(Product::getPrice)).orElse(null);
    }

    public long countLessThanPartNumber(String partNumber) {
        if (partNumber == null) return 0;
        return collection.stream().filter(p -> p.getPartNumber() != null && p.getPartNumber().compareTo(partNumber) < 0).count();
    }

    public long countGreaterThanPrice(int price) {
        return collection.stream().filter(p -> p.getPrice() > price).count();
    }
}