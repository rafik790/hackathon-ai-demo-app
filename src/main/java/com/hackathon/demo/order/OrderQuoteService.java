package com.hackathon.demo.order;

import com.hackathon.demo.product.Product;
import com.hackathon.demo.product.ProductService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Prices an order without storing it.
 */
@Service
public class OrderQuoteService {

    private static final Logger log = LoggerFactory.getLogger(OrderQuoteService.class);

    private final ProductService productService;

    public OrderQuoteService(ProductService productService) {
        this.productService = productService;
    }

    /**
     * @param request a validated request: at least one item, quantities between 1 and 1000
     * @throws UnknownProductException if an item refers to a product that does not exist
     */
    public OrderQuoteResponse quote(OrderQuoteRequest request) {
        int itemCount = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (OrderQuoteRequest.Item item : request.items()) {
            Product product = productService.findById(item.productId())
                    .orElseThrow(() -> new UnknownProductException(item.productId()));
            total = total.add(product.price().multiply(BigDecimal.valueOf(item.quantity())));
            itemCount += item.quantity();
        }
        OrderQuoteResponse quote = new OrderQuoteResponse(itemCount, total.setScale(2, RoundingMode.HALF_UP),
                ProductService.CURRENCY);
        log.info("Order quoted: items={} total={} {}", quote.itemCount(), quote.totalAmount(), quote.currency());
        return quote;
    }
}
