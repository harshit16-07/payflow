package com.payflow.payment.repository;

import com.payflow.payment.entity.OutboxEvent;
import com.payflow.payment.event.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent,Long> {

    List<OutboxEvent> findByStatus(OutboxStatus status);
}
