package com.hackathon.demo.order;

/**
 * An order refers to a product that does not exist (HTTP 400: the request is invalid, not the URL).
 */
public class UnknownProductException extends RuntimeException {

    public UnknownProductException(long productId) {
        super("Unknown product id: " + productId);
    }
}
