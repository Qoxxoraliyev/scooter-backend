package com.scooter_backend.entity;

import com.scooter_backend.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_requests")
public class OrderRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false,length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_operator_id",nullable = false)
    private User createdByOperator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_by_driver_id")
    private User acceptedByDriver;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();
        if (this.status==null){
            this.status=OrderStatus.NEW;
        }
    }

    public OrderRequest(){}

    public OrderRequest(Long id, String message, OrderStatus status, User createdByOperator, User acceptedByDriver, LocalDateTime createdAt, LocalDateTime acceptedAt, LocalDateTime completedAt) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.createdByOperator = createdByOperator;
        this.acceptedByDriver = acceptedByDriver;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
        this.completedAt = completedAt;
    }



    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public User getCreatedByOperator() {
        return createdByOperator;
    }

    public void setCreatedByOperator(User createdByOperator) {
        this.createdByOperator = createdByOperator;
    }

    public User getAcceptedByDriver() {
        return acceptedByDriver;
    }

    public void setAcceptedByDriver(User acceptedByDriver) {
        this.acceptedByDriver = acceptedByDriver;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

}
