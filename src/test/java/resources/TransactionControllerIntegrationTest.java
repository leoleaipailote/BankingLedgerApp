package resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.junit.jupiter.api.Test;
import dev.codescreen.Application;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLoadFunds() throws Exception {
        String messageId = "msg123";
        String requestBody = "{\n" +
                "    \"userId\": \"user1\",\n" +
                "    \"messageId\": \"msg123\",\n" +
                "    \"transactionAmount\": {\n" +
                "        \"amount\": \"100.00\",\n" +
                "        \"currency\": \"USD\",\n" +
                "        \"debitOrCredit\": \"CREDIT\"\n" +
                "    }\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/load/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messageId").value(messageId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance.amount").value("100.0"));
    }

    @Test
    void testAuthorizeFundsApproved() throws Exception {
        String messageId = "loadMsg123";
        String authorizationMessageId = "authMsg123";
        String loadRequestBody = "{\n" +
                "    \"userId\": \"user2\",\n" +
                "    \"messageId\": \"" + messageId + "\",\n" +
                "    \"transactionAmount\": {\n" +
                "        \"amount\": \"100.00\",\n" +
                "        \"currency\": \"USD\",\n" +
                "        \"debitOrCredit\": \"CREDIT\"\n" +
                "    }\n" +
                "}";

        String authorizeRequestBody = "{\n" +
                "    \"userId\": \"user2\",\n" +
                "    \"messageId\": \"" + authorizationMessageId + "\",\n" +
                "    \"transactionAmount\": {\n" +
                "        \"amount\": \"20.00\",\n" +
                "        \"currency\": \"USD\",\n" +
                "        \"debitOrCredit\": \"DEBIT\"\n" +
                "    }\n" +
                "}";

        // First, make a PUT request to load funds
        mockMvc.perform(MockMvcRequestBuilders.put("/api/load/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loadRequestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("user2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messageId").value(messageId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance.amount").value("100.0"));

        // Then, make a PUT request to authorize funds
        mockMvc.perform(MockMvcRequestBuilders.put("/api/authorization/{messageId}", authorizationMessageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorizeRequestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("user2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messageId").value(authorizationMessageId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("APPROVED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance.amount").value("80.0")); // Assuming $20 was
                                                                                              // debited
    }

    @Test
    void testAuthorizeFundsDeclined() throws Exception {
        String messageId = "msg123";
        String requestBody = "{\n" +
                "    \"userId\": \"user2\",\n" +
                "    \"messageId\": \"msg123\",\n" +
                "    \"transactionAmount\": {\n" +
                "        \"amount\": \"100.00\",\n" +
                "        \"currency\": \"USD\",\n" +
                "        \"debitOrCredit\": \"DEBIT\"\n" +
                "    }\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/authorization/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("user2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messageId").value(messageId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("DECLINED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance.amount").value("0.0"));
    }

    @Test
    void testLoadFundsFail() throws Exception {
        String messageId = "msg123";
        String requestBody = "{\n" +
                "    \"userId\": \"user1\",\n" +
                "    \"messageId\": \"msg123\",\n" +
                "    \"transactionAmount\": {\n" +
                "        \"amount\": \"100.00a\",\n" +
                "        \"currency\": \"USD\",\n" +
                "        \"debitOrCredit\": \"CREDIT\"\n" +
                "    }\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/load/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Failed to process load request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("501"));
    }

    @Test
    void testAuthorizeFundsFail() throws Exception {
        String messageId = "msg123";
        String requestBody = "{\n" +
                "    \"userId\": \"user1\",\n" +
                "    \"messageId\": \"msg123\",\n" +
                "    \"transactionAmount\": {\n" +
                "        \"amount\": \"100.00a\",\n" +
                "        \"currency\": \"USD\",\n" +
                "        \"debitOrCredit\": \"CREDIT\"\n" +
                "    }\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/authorization/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Failed to process authorization request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("502"));
    }
}
