package com.fdeck.flightdeck.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID; 

import org.springframework.data.jpa.repository.JpaRepository;

import com.fdeck.flightdeck.model.AgentSession; 
public interface AgentSessionRepository extends JpaRepository<AgentSession, UUID>
{
    Optional<AgentSession> finfByProviderAndExternalSessionId(String provider, String externalSessionId);
    List<AgentSession> findAllByOrderStartedAtDesc();
}
