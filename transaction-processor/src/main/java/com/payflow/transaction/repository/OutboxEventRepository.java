package com.payflow.transaction.repository;

import com.payflow.transaction.entity.OutboxEvent;
import com.payflow.transaction.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent,Long> {

    List<OutboxEvent> findByStatus(OutboxStatus status);
}
