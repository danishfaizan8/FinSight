package com.finsight.backend.controller;

import com.finsight.backend.model.Trade;
import com.finsight.backend.service.TradeService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
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
    public List<Trade> getAllTrades() {
        return tradeService.getAllTrades();
    }

    @PostMapping
    public Trade createTrade(@RequestBody @Valid Trade trade) {
        return tradeService.createTrade(trade);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Trade> deleteTrade(@PathVariable Long id) {
        Trade deletedTrade = tradeService.getTradeById(id); // fetch first
        tradeService.deleteTradeById(id);                   // then delete
        return ResponseEntity.ok(deletedTrade);             // return the deleted item
    }


}
