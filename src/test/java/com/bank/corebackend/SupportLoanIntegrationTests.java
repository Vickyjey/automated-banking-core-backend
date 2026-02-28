package com.bank.corebackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SupportLoanIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private String login(String username, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void supportUnreadCountStaysUntilAdminMarksRead() throws Exception {
        String username = "support-user-" + System.nanoTime();
        String userToken = registerAndLoginUser(username, "pass");
        String adminToken = login("admin", "admin123");

        mockMvc.perform(post("/api/support/messages")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Need help with account\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/support/admin/conversations")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username=='" + username + "')].unreadCount")
                        .value(hasItem(1)));

        mockMvc.perform(get("/api/support/admin/messages/{username}", username)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Need help with account"));

        mockMvc.perform(get("/api/support/admin/conversations")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username=='" + username + "')].unreadCount")
                        .value(hasItem(1)));

        mockMvc.perform(put("/api/support/admin/messages/{username}/read", username)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated").value(1));

        mockMvc.perform(get("/api/support/admin/conversations")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username=='" + username + "')].unreadCount")
                        .value(hasItem(0)));
    }

    @Test
    void loanFlowIsUserScopedAndDefaultsToPending() throws Exception {
        String userOne = "loan-user1-" + System.nanoTime();
        String userTwo = "loan-user2-" + System.nanoTime();
        String userTokenOne = registerAndLoginUser(userOne, "pass");
        String userTokenTwo = registerAndLoginUser(userTwo, "pass");
        String adminToken = login("admin", "admin123");

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userTokenOne)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC-LOAN-1\",\"loanType\":\"Home Loan\",\"loanAmount\":10000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.loanType").value("Home Loan"))
                .andExpect(jsonPath("$.requestedBy").value(userOne));

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userTokenTwo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC-LOAN-2\",\"loanType\":\"Car Loan\",\"loanAmount\":20000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.loanType").value("Car Loan"))
                .andExpect(jsonPath("$.requestedBy").value(userTwo));

        mockMvc.perform(get("/api/loans")
                        .header("Authorization", "Bearer " + userTokenOne))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-LOAN-1"))
                .andExpect(jsonPath("$[0].requestedBy").value(userOne));

        mockMvc.perform(get("/api/loans/account/ACC-LOAN-1")
                        .header("Authorization", "Bearer " + userTokenOne))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-LOAN-1"))
                .andExpect(jsonPath("$[0].requestedBy").value(userOne));

        mockMvc.perform(get("/api/loans")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].accountNumber")
                        .value(hasItems("ACC-LOAN-1", "ACC-LOAN-2")));
    }

    @Test
    void loanValidationRejectsInvalidAmount() throws Exception {
        String user = "loan-user-invalid-" + System.nanoTime();
        String userToken = registerAndLoginUser(user, "pass");

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC-LOAN-X\",\"loanType\":\"Personal Loan\",\"loanAmount\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Loan amount must be greater than zero"));
    }

    @Test
    void adminCanApproveLoanAndUsersCannotApprove() throws Exception {
        String user = "loan-user-approve-" + System.nanoTime();
        String userToken = registerAndLoginUser(user, "pass");
        String adminToken = login("admin", "admin123");

        String createResponse = mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC-LOAN-APPROVE\",\"loanType\":\"Education Loan\",\"loanAmount\":15000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loan = objectMapper.readTree(createResponse);
        long loanId = loan.get("id").asLong();

        mockMvc.perform(put("/api/loans/{loanId}/approve", loanId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/loans/{loanId}/approve", loanId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(get("/api/loans")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }
}
