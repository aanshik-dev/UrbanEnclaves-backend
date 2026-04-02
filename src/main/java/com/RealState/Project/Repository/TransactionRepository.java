package com.RealState.Project.Repository;

import com.RealState.Project.Entity.Agent;
import com.RealState.Project.Entity.Transaction;
import com.RealState.Project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findByBuyer_id(User user);
    List<Transaction> findByAgent_id(Agent agent);

}
