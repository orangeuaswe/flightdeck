package com.fdeck.flightdeck.model;

import jakarta.annotation.Generated;
import jakarta.persistence.*; 
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.Instant; 
import java.util.UUID; 

@Entity
@Table(name="git_commit_record")
@Getter
@Setter
@NoArgsConstructor
public class GitCommitRecord 
{
    @Id
    @GeneratedValue
    private UUID id; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="session_id", nullable = false)
    private AgentSession session; 
    @Column(nullable = false)
    private String commitHash; 
    @Column(length = 1000)
    private String message; 
    private String branch; 
    @Column(nullable = false)
    private Instant commitedAt = Instant.now(); 
}
