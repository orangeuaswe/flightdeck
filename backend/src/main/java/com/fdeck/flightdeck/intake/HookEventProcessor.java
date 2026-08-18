package com.fdeck.flightdeck.intake;

import com.fdeck.flightdeck.model.AgentSession;
import com.fdeck.flightdeck.model.ExecutionEvent;
import com.fdeck.flightdeck.model.GitCommitRecord;
import com.fdeck.flightdeck.model.SessionStatus;
import com.fdeck.flightdeck.out.HookEventPayload;
import com.fdeck.flightdeck.repo.AgentSessionRepository;
import com.fdeck.flightdeck.repo.ExecutionEventRepository;
import com.fdeck.flightdeck.repo.GitCommitRepository;
import com.fdeck.flightdeck.ws.EventBroadcaster;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.Instant;
import java.util.Map;

import tools.jackson.databind.ObjectMapper;
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
        String provider = payload.getFdeck_provider() != null ? payload.getFdeck_provider() : DEFAULT_PROVIDER;
        AgentSession session = sessionRepository.finfByProviderAndExternalSessionId(provider, payload.getSession_id()).orElseGet(()->newSession(payload));
        applyCommonMetadata(session, payload);
        
        switch(payload.getHook_event_name())
        {
            case "SessionStart" -> handleSessionStart(session, payload);
            case "UserPromptSubmit" -> handlePromptSubmit(session, payload);
            case "PreToolUse" -> handlePreToolUse(session, payload);
            case "PostToolUse", "PostToolUseFailure" -> handlePostToolUse(session, payload);
            case "Notification" -> handleNotification(session, payload);
            case "Stop", "SubagentStop" -> handleStop(session, payload);
            case "SessionEnd" -> handleSessionEnd(session, payload);
            default -> handleGeneric(payload);
        }
        session = sessionRepository.save(session); 
        broadcaster.sessionUpdated(session);
    }
    private AgentSession newSession(HookEventPayload payload)
    {
        AgentSession session = new AgentSession();
        session.setExtSessionId(payload.getSession_id());
        session.setProvider(payload.getFdeck_provider() != null ? payload.getFdeck_provider() : DEFAULT_PROVIDER); 
        session.setStartedAt(Instant.now()); 
        session.setStatus(SessionStatus.RUNNING); 
        return session;
    }
    private void handleSessionStart(AgentSession session, HookEventPayload payload)
    {
        session.setStatus(SessionStatus.RUNNING); 
        if(session.getStartedAt() == null)
        {
            session.setStartedAt(Instant.now());
        }
        appendEvent(session, ExecutionEvent.EventType.SESSION_START, null, 
            "Session started" + (payload.getSource() != null ? "(" + payload.getSource() +")" : ""), null, payload);
    }
    private void handlePromptSubmit(AgentSession session, HookEventPayload payload)
    {
        session.setStatus(SessionStatus.RUNNING); 
        if (payload.getPrompt != null)
        {
            session.setCurrentTask(truncate(payload.getPrompt()));
        }
        appendEvent(session, ExecutionEvent.EventType.PROMPT_SUBMIT, null, "Prompt: " + truncate(payload.getPrompt()), null, payload);

    }
    private void handlePreToolUse(AgentSession session, HookEventPayload payload)
    {
        session.setStatus(SessionStatus.RUNNING); 
        String toolName = payload.getTool_name(); 
        appendEvent(session, ExecutionEvent.EventType.TOOL_USE, toolName, describeTool(toolName, payload.getTool_input()), null, payload); 
    }
    private void handlePostToolUse(AgentSession session, HookEventPayload payload)
    {
        boolean isFail = "PostToolUseFailure".equals(payload.getHook_event_name()); 
        String toolName = payload.getTool_name(); 
        session.setToolCallCount(nz(session.getToolCallCount())+1);
        if(isFail)
        {
            session.setFailedToolCallCOunt(nz(session.getFailedToolCallCount())+1);
        }
        boolean isTest = toolName != null && toolName.equalsIgnoreCase("bash") && commandLooksLikeTest(payload.getTool_input()); 
        ExecutionEvent.EventType type = isTest ? ExecutionEvent.EventType.TEST_RUN : ExecutionEvent.EventType.TOOL_RESULT; 
        String terminalOut = "bash".equalsIgnoreCase(toolName) ? extractTerminalOutput(payload.getTool_response()) : null; 
        appendEvent(session,type,toolName, (isFail ? "Failed: ":"") + describeTool(toolName, payload.getTool_input()),isFail, terminalOut, payload); 
        if (payload.getFdeck_git_commit_hash() != null)
        {
            String hash = payload.getFdeck_git_commit_hash(); 
            GitCommitRecord commit = new GitCommitRecord(); 
            commit.setSession(session); 
            commit.setCommitHash(hash); 
            commit.setMessage(payload.getFdeck_git_commit_message()); 
            commit.setBranch(session.getBranch()); 
            commitRepository.save(commit); 
            appendEvent(session, ExecutionEvent.EventType.GIT_COMMIT, "git", "Committed " +hash.substring(0, Math.min(7, hash.length()))+"-"+truncate(payload.getFdeck_git_commit_message()), true, payload); 
        }
    }
    private void handleNotification(AgentSession session)
    
}
