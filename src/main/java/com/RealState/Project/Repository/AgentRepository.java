package com.RealState.Project.Repository;

import com.RealState.Project.Entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent,Long> {
}
