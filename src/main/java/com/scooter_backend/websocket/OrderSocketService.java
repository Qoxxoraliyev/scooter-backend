package com.scooter_backend.websocket;

import com.scooter_backend.dto.order.OrderAcceptedEventDTO;
import com.scooter_backend.dto.order.OrderResponseDTO;
import com.scooter_backend.dto.order.OrderRemoveEventDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderSocketService {

    private static final String NEW_ORDER_TOPIC="/topic/orders/new";
    private static final String ORDER_ACCEPT_TOPIC="/topic/orders/accepted";
    private static final String ORDER_REMOVED_TOPIC="/topic/orders/removed";


    private final SimpMessagingTemplate messagingTemplate;

    public OrderSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNewOrder(OrderResponseDTO dto){
        messagingTemplate.convertAndSend(NEW_ORDER_TOPIC,dto);
    }

    public void sendOrderAccept(OrderAcceptedEventDTO dto){
        messagingTemplate.convertAndSend(ORDER_ACCEPT_TOPIC,dto);
    }

    public void sendOrderRemoved(OrderRemoveEventDTO dto){
        messagingTemplate.convertAndSend(ORDER_REMOVED_TOPIC,dto);
    }


}
