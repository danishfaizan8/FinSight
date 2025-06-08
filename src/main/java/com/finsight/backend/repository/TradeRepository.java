package com.finsight.backend.repository;

import com.finsight.backend.model.Trade;
import com.finsight.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByUser(User user);  // ✅ Add this line
}
