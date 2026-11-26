package com.hackathon.demo.order;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderQuoteService quoteService;

    public OrderController(OrderQuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping("/quote")
    public OrderQuoteResponse quote(@Valid @RequestBody OrderQuoteRequest request) {
        return quoteService.quote(request);
    }
}
