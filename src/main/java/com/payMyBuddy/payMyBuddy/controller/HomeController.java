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

/**
 * Controller class responsible for handling requests related to home, transfer, and contact pages.
 */
@Controller
public class HomeController {

    private final UserService userService;
    private final TransactionService transactionService;

    /**
     * Constructs a new HomeController instance with the specified dependencies.
     *
     * @param userService        the service responsible for user-related operations
     * @param transactionService the service responsible for transaction-related operations
     */
    public HomeController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    /**
     * Returns the home page, displaying information about the current user.
     *
     * @param model the model to which attributes will be added
     * @return the home page view
     */
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

    /**
     * Returns the transfer page, displaying a list of buddies and transfer transactions.
     *
     * @param model    the model to which attributes will be added
     * @param page     the page number for pagination
     * @param pageSize the size of each page for pagination
     * @return the transfer page view
     */
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

    /**
     * Returns the contact page.
     *
     * @param model the model to which attributes will be added
     * @return the contact page view
     */
    @GetMapping("/contact")
    public String getContactPage(Model model) {
        model.addAttribute("parentPages", new String[]{"home"});
        model.addAttribute("currentPage", "contact");
        return "contact_page";
    }
}
