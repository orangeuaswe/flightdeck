package com.fdeck.flightdeck.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HookEventPayload 
{
    //claudecode fields 
    private String session_id;
    private String hook_event_name;
    private String cwd;
    private String transcript_path; 
    private String tool_name; 
    private Map <String, Object> tool_input; 
    private Map<String, Object> tool_response; 
    private String prompt; 
    private String reason; 
    private String source; 
    private String model; 
    private Boolean stop_hook_active; 
    private String message; 
    
    //fields flightdeck adds before forwarding to claudecode
    private String fdeck_provider;
    private String fdeck_machine_hostname; 
    private String fdeck_machine_label; 
    private String fdeck_git_commit_hash; 
    private String fdeck_git_commit_message;
    private String fdeck_git_branch; 
    private String fdeck_repo_name; 
    private String fdeck_current_task_hint; 
}
