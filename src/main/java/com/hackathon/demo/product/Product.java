package com.hackathon.demo.product;

import java.math.BigDecimal;

/**
 * A product of the catalogue.
 *
 * @param price    unit price in {@code currency}
 * @param currency ISO 4217 code
 */
public record Product(long id, String name, String description, BigDecimal price, String currency) {
}
