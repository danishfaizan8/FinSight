package com.finsight.backend;

import com.finsight.backend.dto.TradeDTO;
import com.finsight.backend.model.TradeType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TradeIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/trades";
    }

    @Test
    public void testCreateTrade() {
        TradeDTO dto = new TradeDTO();
        dto.setSymbol("AAPL");
        dto.setType(TradeType.BUY);
        dto.setQuantity(10);
        dto.setPrice(150.5);
        dto.setTradeDate(LocalDate.now());

        ResponseEntity<TradeDTO> response = restTemplate.postForEntity(baseUrl(), dto, TradeDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("AAPL", response.getBody().getSymbol());
    }

    @Test
    public void testGetAllTrades() {
        ResponseEntity<TradeDTO[]> response = restTemplate.getForEntity(baseUrl(), TradeDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 0); // Can be empty or contain trades
    }
}
