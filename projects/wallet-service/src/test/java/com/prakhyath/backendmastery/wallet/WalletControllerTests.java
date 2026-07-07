package com.prakhyath.backendmastery.wallet;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDepositAndWithdrawThroughHttp() throws Exception {
        String walletJson = mockMvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ownerName":"learner"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerName").value("learner"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long walletId = objectMapper.readTree(walletJson).get("id").asLong();

        mockMvc.perform(post("/api/wallets/{id}/deposits", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":100.00}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", comparesEqualTo(100.00)));

        mockMvc.perform(post("/api/wallets/{id}/withdrawals", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":30.00}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", comparesEqualTo(70.00)));
    }
}
