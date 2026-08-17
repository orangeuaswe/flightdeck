package com.fdeck.flightdeck.intake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service; 

import com.fdeck.flightdeck.out.HookEventPayload;
import com.fdeck.flightdeck.repo.AgentSessionRepository;
import com.fdeck.flightdeck.repo.ExecutionEventRepository;
import com.fdeck.flightdeck.repo.GitCommitRepository;
import com.fdeck.flightdeck.ws.EventBroadcaster;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class HookEventProcessor
{
    /**
     * translates claude code hook firing into fdeck domain mode, finds and/or creates agentsession for the external session_id, and adds itself to executionevent. updates session level status and/or currenttask, sends changes over websockets. 
     */

    private static final Logger logger = LoggerFactory.getLogger(HookEventProcessor.class);
    private static final int TASK_SUMMARY_MAX_LENGTH = 160; 
    private final AgentSessionRepository sessionRepository; 
    private final ExecutionEventRepository eventRepository;
    private final GitCommitRepository commitRepository;
    private final EventBroadcaster broadcaster; 
    private final ObjectMapper objMapper; 

    @Transactional
    public void process(HookEventPayload payload)
    {
        if(payload.getSession_id() == null || payload.getHook_event_name() == null)
        {
            logger.warn("dropping hook payload, missing session_id and/or hook_event_name: {}", payload);
            return;
        }
        AgentSession session = sessionRepository.findByExternalSessionId(payload.getSession_id()).orElseGet(()->newSession(payload));
        
    }
    
}
