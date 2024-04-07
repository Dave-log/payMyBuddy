package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.BuddyDTO;
import com.payMyBuddy.payMyBuddy.dto.TransferDTO;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;
    private final TransactionService transactionService;

    public HomeController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/home")
    public String getHomePage(Model model) {
        User currentUser = userService.getCurrentUser();

        model.addAttribute("parentPages", new String[]{""});
        model.addAttribute("currentPage", "home");
        model.addAttribute("firstName", currentUser.getFirstName());
        model.addAttribute("lastName", currentUser.getLastName());
        model.addAttribute("balance", currentUser.getBalance());

        return "home_page";
    }

    @GetMapping("/transfer")
    public String getTransferPage(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "3") int pageSize
    ) {
        List<BuddyDTO> buddyList = userService.getBuddyDTOs();
        model.addAttribute("parentPages", new String[]{"home"});
        model.addAttribute("currentPage", "transfer");
        model.addAttribute("buddyList", buddyList);

        Page<TransferDTO> transferPage = transactionService.getTransferDTOs(PageRequest.of(page, pageSize));
        model.addAttribute("transferPage", transferPage);
        model.addAttribute("pageSize", pageSize);

        return "transfer_page";
    }

    @GetMapping("/contact")
    public String getContactPage(Model model) {
        model.addAttribute("parentPages", new String[]{"home"});
        model.addAttribute("currentPage", "contact");
        return "contact_page";
    }
}
