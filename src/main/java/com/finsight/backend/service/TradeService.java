package com.finsight.backend.service;

import com.finsight.backend.dto.PortfolioDTO;
import com.finsight.backend.dto.TradeDTO;
import com.finsight.backend.model.Trade;
import com.finsight.backend.model.TradeType;
import com.finsight.backend.repository.TradeRepository;
import com.finsight.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.finsight.backend.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;
import com.finsight.backend.dto.PerformanceDTO;


import com.finsight.backend.dto.ExternalTradeDTO;
import java.util.Objects;



@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    public TradeService(TradeRepository tradeRepository, UserRepository userRepository) {
    this.tradeRepository = tradeRepository;
    this.userRepository = userRepository;
}

    
    // 🔁 Create Trade
    public TradeDTO createTrade(TradeDTO dto, String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Trade trade = mapToEntity(dto);
    trade.setUser(user);  // 🔗 associate with logged-in user

    Trade saved = tradeRepository.save(trade);
    return mapToDTO(saved);
}


    // 📥 Get All Trades
    public List<TradeDTO> getTradesByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return tradeRepository.findByUser(user).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
}



    // 🔍 Get by ID
    public TradeDTO getTradeById(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
        return mapToDTO(trade);
    }

    // ❌ Delete by ID
    public void deleteTradeById(Long id) {
        tradeRepository.deleteById(id);
    }

    // 🧠 Mapper: Entity ➡ DTO
    private TradeDTO mapToDTO(Trade trade) {
        TradeDTO dto = new TradeDTO();
        dto.setId(trade.getId());
        dto.setSymbol(trade.getSymbol());
        dto.setType(trade.getType());
        dto.setQuantity(trade.getQuantity());
        dto.setPrice(trade.getPrice());
        dto.setTradeDate(trade.getTradeDate());
        return dto;
    }

    // 🔄 Mapper: DTO ➡ Entity
    private Trade mapToEntity(TradeDTO dto) {
        Trade trade = new Trade();
        trade.setSymbol(dto.getSymbol());
        trade.setType(dto.getType());
        trade.setQuantity(dto.getQuantity());
        trade.setPrice(dto.getPrice());
        trade.setTradeDate(dto.getTradeDate());
        return trade;
    }
    public List<PortfolioDTO> getPortfolioByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<Trade> userTrades = tradeRepository.findAll().stream()
        .filter(t -> t.getUser().getId().equals(user.getId()) && t.getType() == TradeType.BUY)
        .collect(Collectors.toList());

    return calculatePortfolio(userTrades);
}
private List<PortfolioDTO> calculatePortfolio(List<Trade> trades) {
    Map<String, List<Trade>> grouped = trades.stream()
        .collect(Collectors.groupingBy(Trade::getSymbol));

    List<PortfolioDTO> portfolio = new ArrayList<>();

    for (Map.Entry<String, List<Trade>> entry : grouped.entrySet()) {
        String symbol = entry.getKey();
        List<Trade> symbolTrades = entry.getValue();

        int totalQuantity = symbolTrades.stream().mapToInt(Trade::getQuantity).sum();
        double totalCost = symbolTrades.stream().mapToDouble(t -> t.getQuantity() * t.getPrice()).sum();
        double avgBuyPrice = totalQuantity > 0 ? totalCost / totalQuantity : 0;
        double currentValue = totalQuantity * getMockPrice(symbol);

        PortfolioDTO dto = new PortfolioDTO();
        dto.setSymbol(symbol);
        dto.setTotalQuantity(totalQuantity);
        dto.setAverageBuyPrice(avgBuyPrice);
        dto.setCurrentValue(currentValue);

        portfolio.add(dto);
    }

    return portfolio;
}

public List<PerformanceDTO> getPerformanceByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<Trade> userTrades = tradeRepository.findAll().stream()
        .filter(t -> t.getUser().getId().equals(user.getId()) && t.getType() == TradeType.BUY)
        .collect(Collectors.toList());

    return calculatePerformance(userTrades);
}
private List<PerformanceDTO> calculatePerformance(List<Trade> trades) {
    Map<String, List<Trade>> grouped = trades.stream()
        .collect(Collectors.groupingBy(Trade::getSymbol));

    List<PerformanceDTO> performanceList = new ArrayList<>();

    for (Map.Entry<String, List<Trade>> entry : grouped.entrySet()) {
        String symbol = entry.getKey();
        List<Trade> symbolTrades = entry.getValue();

        int totalQuantity = symbolTrades.stream().mapToInt(Trade::getQuantity).sum();
        double totalCost = symbolTrades.stream().mapToDouble(t -> t.getQuantity() * t.getPrice()).sum();
        double avgBuyPrice = totalQuantity > 0 ? totalCost / totalQuantity : 0;
        double currentPrice = getMockPrice(symbol);
        double unrealizedPL = (currentPrice - avgBuyPrice) * totalQuantity;
        double returnPct = avgBuyPrice > 0 ? (currentPrice - avgBuyPrice) / avgBuyPrice * 100 : 0;

        PerformanceDTO dto = new PerformanceDTO();
        dto.setSymbol(symbol);
        dto.setTotalQuantity(totalQuantity);
        dto.setAverageBuyPrice(avgBuyPrice);
        dto.setCurrentPrice(currentPrice);
        dto.setUnrealizedPL(unrealizedPL);
        dto.setReturnPercentage(returnPct);

        performanceList.add(dto);
    }

    return performanceList;
}

public List<String> reconcileTrades(List<ExternalTradeDTO> externalTrades) {
    List<Trade> internalTrades = tradeRepository.findAll();

    List<String> mismatches = new ArrayList<>();

    for (ExternalTradeDTO ext : externalTrades) {
        Trade matched = internalTrades.stream()
                .filter(in -> Objects.equals(in.getId(), ext.getId()))
                .findFirst()
                .orElse(null);

        if (matched == null) {
            mismatches.add("Missing internal trade for ID: " + ext.getId());
        } else {
            if (!Objects.equals(matched.getSymbol(), ext.getSymbol())
                || !Objects.equals(matched.getType(), ext.getType())
                || matched.getQuantity() != ext.getQuantity()
                || Double.compare(matched.getPrice(), ext.getPrice()) != 0
                || !Objects.equals(matched.getTradeDate(), ext.getTradeDate())) {

                mismatches.add("Mismatch on trade ID: " + ext.getId());
            }
        }
    }

    return mismatches;
}



// Mock market price — in real app you'd hit an external API
private double getMockPrice(String symbol) {
    switch (symbol.toUpperCase()) {
        case "AAPL": return 180.0;
        case "MSFT": return 320.0;
        case "GOOGL": return 140.0;
        default: return 100.0; // Default fallback
    }
}

}
