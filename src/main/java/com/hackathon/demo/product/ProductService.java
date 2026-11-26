package com.hackathon.demo.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Fixed catalogue held in memory; there is no database.
 */
@Service
public class ProductService {

    public static final String CURRENCY = "INR";

    private static final List<Product> CATALOGUE = List.of(
            product(1, "Wireless Mouse", "Ergonomic 2.4 GHz wireless mouse", "399.99"),
            product(2, "Mechanical Keyboard", "Compact keyboard with tactile switches", "699.99"),
            product(3, "USB-C Hub", "7-in-1 hub with HDMI and card reader", "1299.00"),
            product(4, "27-inch Monitor", "QHD IPS monitor with adjustable stand", "15999.00"));

    public List<Product> findAll() {
        return CATALOGUE;
    }

    public Optional<Product> findById(long id) {
        return CATALOGUE.stream().filter(product -> product.id() == id).findFirst();
    }

    private static Product product(long id, String name, String description, String price) {
        return new Product(id, name, description, new BigDecimal(price), CURRENCY);
    }
}
