package com.finsight.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Symbol must not be blank")
    private String symbol;

    @NotNull(message = "Trade type must be specified")
    @Enumerated(EnumType.STRING)
    private TradeType type;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.0")
    private double price;

    @NotNull(message = "Trade date is required")
    private LocalDate tradeDate;

    public Trade() {}

    public Trade(String symbol, TradeType type, int quantity, double price, LocalDate tradeDate) {
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.tradeDate = tradeDate;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public TradeType getType() {
        return type;
    }

    public void setType(TradeType type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }


}
