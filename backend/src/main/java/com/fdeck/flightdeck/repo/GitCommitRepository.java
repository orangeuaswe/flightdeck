package com.fdeck.flightdeck.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fdeck.flightdeck.model.GitCommitRecord; 
public interface  GitCommitRepository extends JpaRepository<GitCommitRecord, UUID>
{
    List<GitCommitRecord> findBySessionIdOrderByCommitedAtDesc(UUID sessionId);
}
