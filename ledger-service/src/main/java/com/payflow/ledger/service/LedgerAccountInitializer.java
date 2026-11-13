package com.payflow.ledger.service;

import com.payflow.ledger.entity.AccountType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LedgerAccountInitializer implements CommandLineRunner {

    private final LedgerAccountService ledgerAccountService;

    public LedgerAccountInitializer(
            LedgerAccountService ledgerAccountService) {
        this.ledgerAccountService = ledgerAccountService;
    }

    @Override
    public void run(String... args) {

        ledgerAccountService.createAccountIfAbsent(
                "CUSTOMER-101",
                AccountType.CUSTOMER,
                "INR",
                new BigDecimal("10000.00")
        );

        ledgerAccountService.createAccountIfAbsent(
                "MERCHANT-201",
                AccountType.MERCHANT,
                "INR",
                new BigDecimal("20000.00")
        );

        System.out.println("Initial ledger accounts created!");
    }
}