package com.fdeck.flightdeck.ws;

import com.fdeck.flightdeck.*;
import com.fdeck.flightdeck.model.AgentSession;
import com.fdeck.flightdeck.out.ApiPayload; 
import com.fdeck.flightdeck.model.ExecutionEvent; 
import lombok.RequiredArgsConstructor; 
import org.springframework.messaging.simp.SimpMessagingTemplate; 
import org.springframework.stereotype.Component; 

@Component
@RequiredArgsConstructor
public class EventBroadcaster
{
    /**
     * every webhook taken in goes over two topics: 
     * 1. /topic/sessions: updated session summary, which affects manifest board 
     * 2. /topic/sessions/[id]/events: new timeline entry which affects open detail panel
     */

    private final SimpMessagingTemplate messagingTemplate; 
    public void sessionUpdated(AgentSession session)
    {
        messagingTemplate.convertAndSend("/topic/sessions", ApiPayload.SessionSummary.from(session));
    }
    public void eventAdded(ExecutionEvent event)
    {
        messagingTemplate.convertAndSend("/topic/sessions"+event.getSession().getId()+"/events", ApiPayload.EventEntry.from(event));
    }
}
