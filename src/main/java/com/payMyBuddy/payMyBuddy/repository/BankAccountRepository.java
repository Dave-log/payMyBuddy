package com.payMyBuddy.payMyBuddy.repository;

import com.payMyBuddy.payMyBuddy.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}
