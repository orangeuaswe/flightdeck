package com.fdeck.flightdeck.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
//import org.springframework.web.bind.support.SessionStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "agentSession", indexes = {
        @Index(name = "index_session_ext_id", columnList = "extSessionId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class AgentSession
{
    @Id
    @GeneratedValue
    private UUID id;

    //session id of the agent
    @Column(nullable = false,unique = true)
    private String extSessionId;
    //provider (claude code, codex,etc.)
    private String provider;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.RUNNING;
    private String repo;
    private String branch;
    private String wDirectory;
    private String machineName;
    private String machineLabel;
    @Column(length = 500)
    private String currentTask;
    private Instant startedAt;
    private Instant finishedAt;

    //cost estimate in USD
    private Double estimatedCostUSD = 0.0;
    private Integer toolCallCount = 0;
    private Integer failedToolCallCount = 0;
    @Version
    private Long version;



}
