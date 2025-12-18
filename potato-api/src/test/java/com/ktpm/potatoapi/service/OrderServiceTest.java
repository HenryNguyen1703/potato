package com.ktpm.potatoapi.service;

import com.ktpm.potatoapi.cart.dto.CartItemRequest;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.common.utils.SecurityUtils;
import com.ktpm.potatoapi.drone.repo.DroneRepository;
import com.ktpm.potatoapi.drone.repo.DroneStationRepository;
import com.ktpm.potatoapi.menu.entity.MenuItem;
import com.ktpm.potatoapi.menu.repo.MenuItemRepository;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.merchant.repo.MerchantRepository;
import com.ktpm.potatoapi.option.entity.Option;
import com.ktpm.potatoapi.option.entity.OptionValue;
import com.ktpm.potatoapi.option.repo.OptionValueRepository;
import com.ktpm.potatoapi.order.dto.OrderItemResponse;
import com.ktpm.potatoapi.order.dto.OrderRequest;
import com.ktpm.potatoapi.order.dto.OrderResponse;
import com.ktpm.potatoapi.order.entity.Order;
import com.ktpm.potatoapi.order.entity.OrderItem;
import com.ktpm.potatoapi.order.mapper.OrderItemMapper;
import com.ktpm.potatoapi.order.mapper.OrderItemOptionValueMapper;
import com.ktpm.potatoapi.order.mapper.OrderMapper;
import com.ktpm.potatoapi.order.repo.OrderItemOptionValueRepository;
import com.ktpm.potatoapi.order.repo.OrderItemRepository;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @MockitoBean
    private OptionValueRepository optionValueRepository;

    @MockitoBean
    private OrderItemOptionValueRepository orderItemOptionValueRepository;

    @MockitoBean
    private OrderMapper orderMapper;

    @MockitoBean
    private OrderItemMapper orderItemMapper;

    @MockitoBean
    private OrderItemOptionValueMapper orderItemOptionValueMapper;

    @MockitoBean
    private DroneRepository droneRepository;

    @MockitoBean
    private DroneStationRepository droneStationRepository;


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
        // ===== GIVEN =====
        when(securityUtils.getCurrentUserEmail())
                .thenReturn("customer@gmail.com");

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(menuItemRepository.findByIdAndIsVisibleTrue(100L))
                .thenReturn(Optional.of(menuItem));

        // orderMapper
        when(orderMapper.toEntity(any(OrderRequest.class)))
                .thenAnswer(invocation -> new Order());

        when(orderMapper.toResponse(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    OrderResponse response = new OrderResponse();
                    response.setTotalAmount(order.getTotalAmount());
                    response.setDeliveryFee(order.getDeliveryFee());
                    return response;
                });

        // save order → trả lại chính object
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemOptionValueRepository.findAllByOrderItem(any()))
                .thenReturn(List.of());

        when(orderItemMapper.toResponse(any()))
                .thenReturn(new OrderItemResponse());

        // ===== WHEN =====
        OrderResponse response = orderService.createOrder(orderRequest);

        // ===== THEN =====
        // basePrice = 50_000, quantity = 2 → subtotal = 100_000
        // deliveryFee = 15_000 → total = 115_000
        assertThat(response).isNotNull();
        assertThat(response.getDeliveryFee()).isEqualTo(15_000L);
        assertThat(response.getTotalAmount()).isEqualTo(115_000L);

        verify(userRepository).findByEmail("customer@gmail.com");
        verify(menuItemRepository, atLeastOnce())
                .findByIdAndIsVisibleTrue(100L);
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(orderItemRepository, atLeastOnce()).save(any(OrderItem.class));
    }

    @Test
    void createOrder_userNotFound_throwException() {
        when(securityUtils.getCurrentUserEmail())
                .thenReturn("customer@gmail.com");

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> orderService.createOrder(orderRequest));

        assertThat(ex.getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void createOrder_menuItemNotFound_throwException() {
        when(securityUtils.getCurrentUserEmail())
                .thenReturn("customer@gmail.com");

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(customer));

        when(menuItemRepository.findByIdAndIsVisibleTrue(100L))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> orderService.createOrder(orderRequest));

        assertThat(ex.getErrorCode())
                .isEqualTo(ErrorCode.MENU_ITEM_NOT_FOUND);
    }
}
