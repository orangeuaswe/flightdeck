package com.fdeck.flightdeck.repo;

import java.util.List; 
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fdeck.flightdeck.model.ExecutionEvent;

public interface  ExecutionEventRepository extends JpaRepository<ExecutionEvent, UUID>
{
    List<ExecutionEvent> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);
}
