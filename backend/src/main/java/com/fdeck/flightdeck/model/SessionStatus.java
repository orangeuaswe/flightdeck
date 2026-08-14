package com.fdeck.flightdeck.model;

public enum SessionStatus 
{
    /*
    lifecycle status of an agent: 
    running - session started, working (between sessionstart and sessionend)
    watiting - session is idle waiting on user and/or approval
    completed - session completed with a normal reason
    failed - session failed with error, or tol errored and no activity proceeded it within timeout windows
    */
    RUNNING,
    WAITING,
    COMPLETED,
    FAILED
    
}
