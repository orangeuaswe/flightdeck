package com.fdeck.flightdeck.model;

import jakarta.persistence.*; 
import lombok.Getter; 
import lombok.NoArgsConstructor; 
import lombok.Setter; 
import java.time.instant; 
import java.util.UUID; 
@Entity
@Table(name = "execution_event", indexes = {@Index(name = "index_event_session", columnList="session_id")})
@Getter
@Setter
@NoArgsConstructor
public class ExecutionEvent 
{
    @Id
    @GeneratedValue
    private UUID id; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="session_id", nullable = false)
    private AgentSession session; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType event; 
    //edit, bash, write for tool events
    private String toolName; 
    
    //summary of action done
    @Column(length = 500)
    private String summary; 

    private Boolean success; 

    //stdout for bash tool calls, read from the hook tool_response
    @Lob 
    private String terminalOutput; 

    @Lob
    private String payloadRaw; 

    @Column(nullable = false)
    private Instant createdAt = Instant.now(); 

    public enum EventType{
        SESSION_START,
        PROMPT_SUBMIT,
        TOOL_USE,
        TOOL_RESULT,
        TEST_RUN,
        GIT_COMMIT,
        NOTIFICATION,
        ASSISTANT_TURN_COMPLETE,
        SESSION_END
    }
    

    
}
