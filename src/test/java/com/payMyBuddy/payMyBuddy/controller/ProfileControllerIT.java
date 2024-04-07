package com.payMyBuddy.payMyBuddy.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "davelog@example.com", password = "0000")
    public void testGetProfilePage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/profile"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentUser"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("buddies"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("bankAccounts"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("profileUpdateDTO"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("selectedBuddy"))
                .andExpect(MockMvcResultMatchers.view().name("profile_page"));
    }

    @Test
    @WithMockUser(username = "davelog@example.com", password = "0000")
    public void testAddBankAccount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/profile/add-bank-account")
                        .param("iban", "FR1234567890123456789012345"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/profile#bank-accounts"));
    }
}
