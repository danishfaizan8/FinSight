package com.finsight.backend.service;

import com.finsight.backend.model.Trade;
import com.finsight.backend.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;

    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    public Trade createTrade(Trade trade) {
        return tradeRepository.save(trade);
    }
    public Trade getTradeById(Long id) {
    return tradeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
}

public void deleteTradeById(Long id) {
    tradeRepository.deleteById(id);
}
    

}
