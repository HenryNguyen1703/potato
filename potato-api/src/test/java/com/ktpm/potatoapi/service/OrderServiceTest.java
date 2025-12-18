package com.ktpm.potatoapi.service;

import com.ktpm.potatoapi.cart.dto.CartItemRequest;
import com.ktpm.potatoapi.common.utils.SecurityUtils;
import com.ktpm.potatoapi.menu.entity.MenuItem;
import com.ktpm.potatoapi.menu.repo.MenuItemRepository;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.merchant.repo.MerchantRepository;
import com.ktpm.potatoapi.order.dto.OrderRequest;
import com.ktpm.potatoapi.order.entity.Order;
import com.ktpm.potatoapi.order.repo.OrderRepository;
import com.ktpm.potatoapi.order.service.OrderService;
import com.ktpm.potatoapi.user.entity.Role;
import com.ktpm.potatoapi.user.entity.User;
import com.ktpm.potatoapi.user.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private MenuItemRepository menuItemRepository;

    @MockitoBean
    private MerchantRepository merchantRepository;

    private Order order;
    private OrderRequest orderRequest;
    private User customer;
    private Merchant merchant;
    private MenuItem menuItem;

    @BeforeEach
    public void init() {
        customer = User.builder()
                .id(1L)
                .email("customer@gmail.com")
                .role(Role.CUSTOMER)
                .build();

        merchant = new Merchant();
        merchant.setId(10L);
        merchant.setName("Pizza Store");

        menuItem = new MenuItem();
        menuItem.setId(100L);
        menuItem.setBasePrice(50_000L);
        menuItem.setMerchant(merchant);

        CartItemRequest cartItem = new CartItemRequest();
        cartItem.setMenuItemId(100L);
        cartItem.setQuantity(2);

        orderRequest = new OrderRequest();
        orderRequest.setCartItems(List.of(cartItem));
    }

    @Test
    void createOrder_valid_success() {
//        // mock security
//        when(securityUtils.getCurrentUserEmail()).thenReturn("customer@gmail.com");
//
//        // mock user
//        when(userRepository.findByEmail("customer@gmail.com"))
//                .thenReturn(Optional.of(customer));
//
//        // mock menu item & merchant
//        when(menuItemRepository.findById(100L))
//                .thenReturn(Optional.of(menuItem));
//
//        // mock save order (2 lần)
//        when(orderRepository.save(any(Order.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        // when
//        OrderResponse response = orderService.createOrder(orderRequest);
//
//        // then
//        Assertions.assertThat(response).isNotNull();
//        Assertions.assertThat(response.getTotalAmount()).isGreaterThan(0);
    }

}
