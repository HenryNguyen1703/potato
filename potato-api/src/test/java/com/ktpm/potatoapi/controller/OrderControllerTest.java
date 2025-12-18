package com.ktpm.potatoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktpm.potatoapi.cart.dto.CartItemRequest;
import com.ktpm.potatoapi.order.dto.OrderRequest;
import com.ktpm.potatoapi.order.dto.OrderResponse;
import com.ktpm.potatoapi.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean private OrderService orderService;
    private OrderRequest orderRequest;

    @BeforeEach
    public void init() {
        CartItemRequest cartItem = new CartItemRequest();
        cartItem.setMenuItemId(1L);
        cartItem.setQuantity(2);
        cartItem.setNote("không cay");
        cartItem.setOptionValueIds(List.of(10L, 11L));

        orderRequest = new OrderRequest();
        orderRequest.setFullName("Nguyễn Văn A");
        orderRequest.setPhone("0912345678");
        orderRequest.setDeliveryAddress("123 Lê Lợi, Q1");
        orderRequest.setLatitude(10.776889);
        orderRequest.setLongitude(106.700806);
        orderRequest.setNote("Giao gấp");
        orderRequest.setCartItems(List.of(cartItem));
    }

    @Test
    void checkout_success() throws Exception {
        OrderResponse response = new OrderResponse();
        response.setId(1L);

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void checkout_fail_fullName_blank() throws Exception {
        orderRequest.setFullName("");

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Customer's full name is required"));
    }

    @Test
    void checkout_fail_phone_blank() throws Exception {
        orderRequest.setPhone("");

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Customer's phone number is required"));
    }

    @Test
    void checkout_fail_address_blank() throws Exception {
        orderRequest.setDeliveryAddress("");

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Address is required"));
    }

    @Test
    void checkout_fail_latitude_null() throws Exception {
        orderRequest.setLatitude(null);

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Latitude is required"));
    }

    @Test
    void checkout_fail_longitude_null() throws Exception {
        orderRequest.setLongitude(null);

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Longitude is required"));
    }

    @Test
    void checkout_fail_cartItems_empty() throws Exception {
        orderRequest.setCartItems(List.of());

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("List of cart items is required"));
    }

    @Test
    void checkout_fail_menuItemId_null() throws Exception {
        orderRequest.getCartItems().get(0).setMenuItemId(null);

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Menu item in cart is required"));
    }

    @Test
    void checkout_fail_quantity_null() throws Exception {
        orderRequest.getCartItems().get(0).setQuantity(null);

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Menu item's quantity in cart is required"));
    }

    @Test
    void checkout_fail_quantity_zero() throws Exception {
        orderRequest.getCartItems().get(0).setQuantity(0);

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Menu item's quantity in cart must be greater than 0"));
    }

    @Test
    void checkout_fail_quantity_negative() throws Exception {
        orderRequest.getCartItems().get(0).setQuantity(-1);

        mockMvc.perform(post("/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Menu item's quantity in cart must be greater than 0"));
    }
}
