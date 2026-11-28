package com.hackathon.demo;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * The REST API through the complete application context, without a server or network.
 */
@SpringBootTest
class ApiControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Nested
    class Hello {

        @Test
        void keepsItsResponse() throws Exception {
            mvc.perform(get("/api/hello"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.application").value("Hackathon Demo App"))
                    .andExpect(jsonPath("$.message").value("Hello! Application is running successfully.v1"))
                    .andExpect(jsonPath("$.version").value("1.0.0"));
        }
    }

    @Nested
    class Products {

        @Test
        void listsTheCatalogue() throws Exception {
            mvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andExpect(jsonPath("$[0].id").value(2))
                    .andExpect(jsonPath("$[0].name").value("Wireless Mouse"))
                    .andExpect(jsonPath("$[0].price").value(399.99))
                    .andExpect(jsonPath("$[0].currency").value("INR"));
        }

        @Test
        void returnsAProduct() throws Exception {
            mvc.perform(get("/api/products/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
                    .andExpect(jsonPath("$.price").value(699.99));
        }

        @Test
        void unknownProductIsNotFound() throws Exception {
            expectError(mvc.perform(get("/api/products/99")), 404, "Product 99 not found")
                    .andExpect(jsonPath("$.path").value("/api/products/99"));
        }

        @Test
        void nonNumericIdIsABadRequest() throws Exception {
            expectError(mvc.perform(get("/api/products/abc")), 400, "Invalid value for 'id'");
        }
    }

    @Nested
    class OrderQuote {

        @Test
        void calculatesTheQuote() throws Exception {
            quote("""
                    {"items": [{"productId": 1, "quantity": 2}, {"productId": 2, "quantity": 1}]}""")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.itemCount").value(3))
                    .andExpect(jsonPath("$.totalAmount").value(1499.97))
                    .andExpect(jsonPath("$.currency").value("INR"));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void quantityMustBeGreaterThanZero(int quantity) throws Exception {
            expectError(quote("""
                    {"items": [{"productId": 1, "quantity": %d}]}""".formatted(quantity)), 400, "Request validation failed")
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("items[0].quantity"))
                    .andExpect(jsonPath("$.fieldErrors[0].message").value("must be greater than 0"));
        }

        @Test
        void unknownProductIsABadRequest() throws Exception {
            expectError(quote("""
                    {"items": [{"productId": 1, "quantity": 1}, {"productId": 99, "quantity": 1}]}"""),
                    400, "Unknown product id: 99");
        }

        @ParameterizedTest
        @ValueSource(strings = {"{\"items\": []}", "{}"})
        void orderMustContainAnItem(String body) throws Exception {
            expectError(quote(body), 400, "Request validation failed")
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("items"))
                    .andExpect(jsonPath("$.fieldErrors[0].message").value("must contain at least one item"));
        }

        @Test
        void itemNeedsAProductAndAQuantity() throws Exception {
            expectError(quote("""
                    {"items": [{}]}"""), 400, "Request validation failed")
                    .andExpect(jsonPath("$.fieldErrors", hasSize(2)))
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("items[0].productId"))
                    .andExpect(jsonPath("$.fieldErrors[1].field").value("items[0].quantity"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "not json", "{\"items\": [{\"productId\": 1, \"quantity\": \"two\"}]}"})
        void unreadableBodyIsABadRequest(String body) throws Exception {
            expectError(quote(body), 400, "Request body is missing or is not valid JSON");
        }

        private ResultActions quote(String body) throws Exception {
            return mvc.perform(post("/api/orders/quote").contentType(MediaType.APPLICATION_JSON).content(body));
        }
    }

    private static ResultActions expectError(ResultActions result, int status, String message) throws Exception {
        return result
                .andExpect(status().is(status))
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }
}
