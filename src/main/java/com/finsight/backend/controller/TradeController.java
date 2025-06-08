package com.finsight.backend.controller;

import com.finsight.backend.dto.PortfolioDTO;
import com.finsight.backend.dto.TradeDTO;
import com.finsight.backend.dto.PerformanceDTO;
import com.finsight.backend.dto.ExternalTradeDTO;
import com.finsight.backend.service.TradeService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trades")
public class TradeController {

    private final TradeService tradeService;
    
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping
    public List<TradeDTO> getAllTrades() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return tradeService.getTradesByUsername(username);
    }


    @GetMapping("/portfolio")
    public List<PortfolioDTO> getPortfolio() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return tradeService.getPortfolioByUsername(username);
    }

    @GetMapping("/performance")
    public List<PerformanceDTO> getPerformance() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return tradeService.getPerformanceByUsername(username);
    }


    @PostMapping
    public TradeDTO createTrade(@RequestBody @Valid TradeDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return tradeService.createTrade(dto, username); // updated to take username
    }

    @PostMapping("/reconcile")
    public List<String> reconcileTrades(@RequestBody List<ExternalTradeDTO> externalTrades) {
        return tradeService.reconcileTrades(externalTrades);
    }

    

    @DeleteMapping("/{id}")
    public ResponseEntity<TradeDTO> deleteTrade(@PathVariable Long id) {
        TradeDTO deletedTrade = tradeService.getTradeById(id);
        tradeService.deleteTradeById(id);
        return ResponseEntity.ok(deletedTrade);
    }
}
