package com.payflow.ledger.Controller;

import com.payflow.ledger.repository.AccountBalanceProjection;
import com.payflow.ledger.service.LedgerAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/ledger/accounts")
public class LedgerAccountController {

    private final LedgerAccountService ledgerAccountService;

    public LedgerAccountController(LedgerAccountService ledgerAccountService) {
        this.ledgerAccountService = ledgerAccountService;
    }

    @GetMapping("/balances")
    public List<AccountBalanceProjection> getAllAccountBalances()
    {
        return ledgerAccountService.getAllAccountBalances();
    }
}
