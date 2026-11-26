package com.hackathon.demo.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hackathon.demo.product.ProductService;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderQuoteServiceTest {

    private final OrderQuoteService service = new OrderQuoteService(new ProductService());

    @Test
    void multipliesPricesByQuantitiesAndAddsThemUp() {
        OrderQuoteResponse quote = service.quote(request(item(1, 2), item(2, 1)));

        assertThat(quote.itemCount()).isEqualTo(3);
        // 2 x 399.99 + 1 x 699.99
        assertThat(quote.totalAmount()).isEqualByComparingTo("1499.97");
        assertThat(quote.totalAmount().scale()).isEqualTo(2);
        assertThat(quote.currency()).isEqualTo("INR");
    }

    @Test
    void sameProductTwiceIsCountedTwice() {
        OrderQuoteResponse quote = service.quote(request(item(3, 1), item(3, 2)));

        assertThat(quote.itemCount()).isEqualTo(3);
        assertThat(quote.totalAmount()).isEqualByComparingTo("3897.00");
    }

    @Test
    void largeQuantitiesStayExact() {
        OrderQuoteResponse quote = service.quote(request(item(1, 1000), item(4, 1000)));

        assertThat(quote.itemCount()).isEqualTo(2000);
        assertThat(quote.totalAmount()).isEqualByComparingTo("16398990.00");
    }

    @Test
    void unknownProductIsRejected() {
        assertThatThrownBy(() -> service.quote(request(item(1, 1), item(99, 1))))
                .isInstanceOf(UnknownProductException.class)
                .hasMessage("Unknown product id: 99");
    }

    private static OrderQuoteRequest request(OrderQuoteRequest.Item... items) {
        return new OrderQuoteRequest(List.of(items));
    }

    private static OrderQuoteRequest.Item item(long productId, int quantity) {
        return new OrderQuoteRequest.Item(productId, quantity);
    }
}
