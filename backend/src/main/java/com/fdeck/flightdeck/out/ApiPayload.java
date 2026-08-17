package com.fdeck.flightdeck.out;

import com.fdeck.flightdeck.model.AgentSession;
import com.fdeck.flightdeck.model.ExecutionEvent;
import com.fdeck.flightdeck.model.GitCommitRecord;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ApiPayload 
{
    public record SessionSummary(UUID id,  String provider, String status, String repoName, String branch, String currentTask,  String machineLabel, Instant startedAt, Instant endedAt, Double estimatedCostUsd, Integer toolCallCount, Integer failedToolCallCount)
    {
        public static SessionSummary from(AgentSession session)
        {
            return new SessionSummary(
                session.getId(),
                session.getProvider(),
                session.getStatus().name(),
                session.getRepo(),
                session.getBranch(),
                session.getCurrentTask(),
                session.getMachineLabel() != null ? session.getMachineLabel(): session.getMachineName(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.getEstimatedCostUSD(),
                session.getToolCallCount(),
                session.getFailedToolCallCount()
            );
        }
    }

    public record EventEntry(UUID id, String eventType, String toolName, String summary, Boolean success,  String terminalOutput,  Instant createdAt)
    {
        public static EventEntry from(ExecutionEvent event)
        {
            return new EventEntry(
                event.getId(),
                event.getEvent().name(),
                event.getToolName(),
                event.getSummary(),
                event.getSuccess(),
                event.getTerminalOutput(),
                event.getCreatedAt()
            );
        }
    }

    public record CommitEntry(UUID id, String commitHash, String message, String branch, Instant createdAt)
    {
        public static CommitEntry from (GitCommitRecord commit)
        {
            return new CommitEntry(
                commit.getId(), 
                commit.getCommitHash(),
                commit.getMessage(), 
                commit.getBranch(), 
                commit.getCommitedAt()
            );
        }
    }

    public record SessionDetail(SessionSummary session, List<EventEntry> timeline, List<CommitEntry> commits)
    {

    }
}
