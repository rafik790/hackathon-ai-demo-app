package com.hackathon.demo.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * Products and quantities to quote.
 */
public record OrderQuoteRequest(
        @NotEmpty(message = "must contain at least one item") List<@NotNull @Valid Item> items) {

    /** {@code quantity} is limited so that a quote stays a realistic order. */
    public record Item(
            @NotNull Long productId,
            @NotNull @Positive(message = "must be greater than 0") @Max(1000) Integer quantity) {
    }
}
