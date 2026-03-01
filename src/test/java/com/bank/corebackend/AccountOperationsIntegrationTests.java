package com.bank.corebackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountOperationsIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private String registerAndLoginUser(String username, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk());

        return mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void withdrawUpdatesBalanceAndReturnsSuccessMessage() throws Exception {
        String userToken = registerAndLoginUser("withdraw-user-" + System.nanoTime(), "pass");
        String account = "ACC-WD-" + System.nanoTime();

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"" + account + "\",\"accountHolderName\":\"Tester\",\"balance\":1000}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/accounts/withdraw")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"" + account + "\",\"amount\":250}"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Withdrawn Successfully")));

        mockMvc.perform(get("/api/accounts/{accountNumber}", account)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(750.0));
    }

    @Test
    void withdrawFailsForInsufficientBalance() throws Exception {
        String userToken = registerAndLoginUser("withdraw-low-" + System.nanoTime(), "pass");
        String account = "ACC-WD-LOW-" + System.nanoTime();

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"" + account + "\",\"accountHolderName\":\"Tester\",\"balance\":100}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/accounts/withdraw")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"" + account + "\",\"amount\":500}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance"));
    }
}
