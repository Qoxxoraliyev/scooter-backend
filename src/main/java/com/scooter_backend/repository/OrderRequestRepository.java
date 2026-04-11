package com.scooter_backend.repository;

import com.scooter_backend.entity.OrderRequest;
import com.scooter_backend.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRequestRepository extends JpaRepository<OrderRequest,Long> {

    List<OrderRequest> findAllByStatusOrderByCreatedAtDesc(OrderStatus status);

    List<OrderRequest> findAllByAcceptedByDriver_IdOrderByCreatedAtDesc(Long driverId);

    List<OrderRequest> findAllByCreatedByOperator_IdOrderByCreatedAtDesc(Long operatorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderRequest o where o.id = :id")
    Optional<OrderRequest> findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Query("delete from OrderRequest o where o.createdAt < :threshold")
    void deleteOldOrders(@Param("threshold") LocalDateTime threshold);


}
