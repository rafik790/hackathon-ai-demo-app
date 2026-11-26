package com.hackathon.demo.order;

import java.math.BigDecimal;

/**
 * @param itemCount   sum of all quantities
 * @param totalAmount sum of unit price times quantity, rounded to two decimals
 */
public record OrderQuoteResponse(int itemCount, BigDecimal totalAmount, String currency) {
}
