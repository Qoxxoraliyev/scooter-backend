package com.scooter_backend.service.order;

import com.scooter_backend.dto.order.OrderAcceptedEventDTO;
import com.scooter_backend.dto.order.OrderResponseDTO;
import com.scooter_backend.entity.OrderRequest;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.OrderStatus;
import com.scooter_backend.enums.Role;
import com.scooter_backend.exception.CustomException;
import com.scooter_backend.repository.OrderRequestRepository;
import com.scooter_backend.repository.UserRepository;
import com.scooter_backend.service.OrderRequestService;
import com.scooter_backend.websocket.OrderSocketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scooter_backend.dto.order.OrderRemoveEventDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderRequestServiceImpl implements OrderRequestService {

    private final OrderRequestRepository orderRequestRepository;
    private final UserRepository userRepository;
    private final OrderSocketService orderSocketService;

    public OrderRequestServiceImpl(OrderRequestRepository orderRequestRepository, UserRepository userRepository, OrderSocketService orderSocketService) {
        this.orderRequestRepository = orderRequestRepository;
        this.userRepository = userRepository;
        this.orderSocketService = orderSocketService;
    }

    @Override
    public OrderResponseDTO createOrder(String operatorPhone, String message) {
        User operator = getUserByPhone(operatorPhone);
        ensureRole(operator, Role.OPERATOR, "Only operator can create order");

        OrderRequest order = new OrderRequest();
        order.setMessage(message);
        order.setCreatedByOperator(operator);
        order.setStatus(OrderStatus.NEW);

        OrderRequest saved = orderRequestRepository.save(order);
        OrderResponseDTO dto = toDTO(saved);
        orderSocketService.sendNewOrder(dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAvailableOrders() {
        return orderRequestRepository.findAllByStatusOrderByCreatedAtDesc(OrderStatus.NEW)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getMyAcceptedOrders(String driverPhone) {
        User driver = getUserByPhone(driverPhone);
        ensureRole(driver, Role.DRIVER, "Only driver can view accepted orders");

        return orderRequestRepository.findAllByAcceptedByDriver_IdOrderByCreatedAtDesc(driver.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getMyCreatedOrders(String operatorPhone) {
        User operator = getUserByPhone(operatorPhone);
        ensureRole(operator, Role.OPERATOR, "Only operator can view created orders");

        return orderRequestRepository.findAllByCreatedByOperator_IdOrderByCreatedAtDesc(operator.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public OrderResponseDTO acceptOrder(Long orderId, String driverPhone) {
        User driver = getUserByPhone(driverPhone);
        ensureRole(driver, Role.DRIVER, "Only driver can accept order");

        OrderRequest order = orderRequestRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new CustomException("Order already accepted or unavailable");
        }

        order.setStatus(OrderStatus.ACCEPTED);
        order.setAcceptedByDriver(driver);
        order.setAcceptedAt(LocalDateTime.now());

        OrderRequest saved = orderRequestRepository.save(order);
        OrderResponseDTO dto = toDTO(saved);

        orderSocketService.sendOrderAccept(new OrderAcceptedEventDTO(saved.getId(), driver.getId()));
        orderSocketService.sendOrderRemoved(new OrderRemoveEventDTO(saved.getId()));

        return dto;
    }

    @Override
    public OrderResponseDTO completeOrder(Long orderId, String driverPhone) {
        User driver = getUserByPhone(driverPhone);
        ensureRole(driver, Role.DRIVER, "Only driver can complete order");

        OrderRequest order = orderRequestRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new CustomException("Only accepted order can be completed");
        }

        if (order.getAcceptedByDriver() == null || !order.getAcceptedByDriver().getId().equals(driver.getId())) {
            throw new CustomException("This order belongs to another driver");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());

        return toDTO(orderRequestRepository.save(order));
    }

    @Override
    public OrderResponseDTO cancelOrder(Long orderId, String operatorPhone) {
        User operator = getUserByPhone(operatorPhone);
        if (operator.getRole() != Role.OPERATOR && operator.getRole() != Role.ADMIN) {
            throw new CustomException("Only operator or admin can cancel order");
        }

        OrderRequest order = orderRequestRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new CustomException("Order already closed");
        }

        if (operator.getRole() == Role.OPERATOR && !order.getCreatedByOperator().getId().equals(operator.getId())) {
            throw new CustomException("You can cancel only your own orders");
        }

        order.setStatus(OrderStatus.CANCELED);
        OrderRequest saved = orderRequestRepository.save(order);
        orderSocketService.sendOrderRemoved(new OrderRemoveEventDTO(saved.getId()));
        return toDTO(saved);
    }


    private User getUserByPhone(String phone){
        return userRepository.findByPhone(phone)
                .orElseThrow(()->new CustomException("User not found"));
    }

    private void ensureRole(User user, Role role, String message){
        if (user.getRole()!=role){
            throw new CustomException(message);
        }
    }

    private OrderResponseDTO toDTO(OrderRequest order){
        return new OrderResponseDTO(
                order.getId(),
                order.getMessage(),
                order.getStatus(),
                order.getCreatedByOperator()!=null ? order.getCreatedByOperator().getId():null,
                order.getAcceptedByDriver()!=null?order.getAcceptedByDriver().getId():null,
                order.getCreatedAt(),
                order.getAcceptedAt(),
                order.getCompletedAt()
        );
    }


}
