package com.payMyBuddy.payMyBuddy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "davelog@example.com", password = "0000")
    public void testTransfer_ValidTransaction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/buddy-transaction")
                        .param("amount", "1")
                        .param("recipientEmail", "batman@example.com")
                        .param("description", "none")
                        .param("feePaidBySender", "true"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/transfer?success"));
    }

    @Test
    @WithMockUser(username = "davelog@example.com", password = "0000")
    public void testTransfer_InvalidTransaction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/buddy-transaction")
                        .param("amount", "1000")
                        .param("recipientEmail", "batman@example.com")
                        .param("description", "none")
                        .param("feePaidBySender", "true"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error"));
    }
}
