package com.hackathon.demo.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductServiceTest {

    private final ProductService service = new ProductService();

    @Test
    void listsTheWholeCatalogueWithUniqueIdsInRupees() {
        assertThat(service.findAll()).hasSize(4)
                .extracting(Product::id).doesNotHaveDuplicates();
        assertThat(service.findAll()).allMatch(product -> product.currency().equals("INR")
                && product.price().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void findsAProductById() {
        assertThat(service.findById(1)).hasValueSatisfying(product -> {
            assertThat(product.name()).isEqualTo("Wireless Mouse");
            assertThat(product.price()).isEqualByComparingTo("399.99");
        });
    }

    @Test
    void unknownIdIsEmpty() {
        assertThat(service.findById(99)).isEmpty();
        assertThat(service.findById(0)).isEmpty();
        assertThat(service.findById(-1)).isEmpty();
    }
}
