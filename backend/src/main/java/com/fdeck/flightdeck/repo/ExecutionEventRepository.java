package com.fdeck.flightdeck.repo;

import com.fdeck.fligtdeck.model.ExecutionEvent;

import org.springframework.data.jpa.JpaRepository;

import java.util.List; 

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface  ExecutionEventRepository extends JpaRepository<ExecutionEvent, UUID>
{
    List<ExecutionEvent> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);
}
