package com.bank.corebackend;

import com.bank.corebackend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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
    void userTokenCannotAccessAdminEndpoints() throws Exception {
        String token = registerAndLoginUser("alice-" + System.nanoTime(), "pass");

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminTokenCanAccessAdminEndpoints() throws Exception {
        String adminToken = login("admin", "admin123");

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void userTokenCannotAccessGlobalTransactions() throws Exception {
        String token = registerAndLoginUser("bob-" + System.nanoTime(), "pass");

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void userTokenCanAccessAccountScopedTransactions() throws Exception {
        String token = registerAndLoginUser("carol-" + System.nanoTime(), "pass");

        mockMvc.perform(get("/api/transactions/account/ACC-001")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void missingOrExpiredTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/accounts/ACC-100"))
                .andExpect(status().isUnauthorized());

        String expiredToken = Jwts.builder()
                .setSubject("expired-user")
                .claim("role", "USER")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3_600_000))
                .setExpiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(
                        Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkey123456"
                                .getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )
                .compact();

        mockMvc.perform(get("/api/accounts/ACC-100")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminSeederCreatesSingleAdminUser() {
        long adminCount = userRepository.countByUsername("admin");
        org.junit.jupiter.api.Assertions.assertEquals(1, adminCount);
    }
}
