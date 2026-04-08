package server;

import common.model.Product;
import common.model.Location;
import java.util.Comparator;

public class ProductSorter {
    public static Comparator<Product> byLocation() {
        return (p1, p2) -> {
            Location loc1 = getTown(p1);
            Location loc2 = getTown(p2);
            if (loc1 == null && loc2 == null) return 0;
            if (loc1 == null) return -1;
            if (loc2 == null) return 1;
            int cmp = Double.compare(loc1.getX(), loc2.getX());
            if (cmp != 0) return cmp;
            cmp = Float.compare(loc1.getY(), loc2.getY());
            if (cmp != 0) return cmp;
            return Float.compare(loc1.getZ(), loc2.getZ());
        };
    }

    private static Location getTown(Product p) {
        if (p.getManufacturer() == null) return null;
        if (p.getManufacturer().getPostalAddress() == null) return null;
        return p.getManufacturer().getPostalAddress().getTown();
    }
}