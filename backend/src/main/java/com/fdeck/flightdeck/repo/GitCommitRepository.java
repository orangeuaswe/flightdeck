package com.fdeck.flightdeck.repo;

import com.fdeck.flightdeck.model.GitCommitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
impoet java.util.List; 
import java.util.UUID; 
public interface  GitCommitRepository extends JpaRepository<GitCommitRecord, UUID>
{
    List<GitCommitRecord> findBySessionIdOrderByCommitedAtDesc(UUID sessionId);
}
