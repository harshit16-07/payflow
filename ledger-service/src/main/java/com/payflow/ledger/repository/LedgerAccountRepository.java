
package com.payflow.ledger.repository;

import com.payflow.ledger.entity.LedgerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LedgerAccountRepository
        extends JpaRepository<LedgerAccount, Long> {

    Optional<LedgerAccount> findByAccountNumber(String accountNumber);

    @Query(value = """
            SELECT
                a.account_number AS accountNumber,
                a.account_type AS accountType,
                a.currency AS currency,
                a.opening_balance +
                COALESCE(
                    SUM(
                        CASE
                            WHEN e.entry_type = 'CREDIT' THEN e.amount
                            WHEN e.entry_type = 'DEBIT' THEN -e.amount
                        END
                    ),
                    0
                ) AS currentBalance
            FROM ledger_accounts a
            LEFT JOIN ledger_entries e
                ON e.account_id = a.account_number
                AND e.currency = a.currency
            GROUP BY
                a.id,
                a.account_number,
                a.account_type,
                a.currency,
                a.opening_balance
            ORDER BY a.id
            """, nativeQuery = true)
    List<AccountBalanceProjection> findAllAccountBalances();
}
