package com.hackathon.demo.product;

/**
 * The requested product does not exist (HTTP 404).
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(long id) {
        super("Product " + id + " not found");
    }
}
