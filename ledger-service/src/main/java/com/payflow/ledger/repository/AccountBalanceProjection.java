package com.payflow.ledger.repository;

import java.math.BigDecimal;

public interface AccountBalanceProjection {

    String getAccountNumber();

    String getAccountType();

    String getCurrency();

    BigDecimal getCurrentBalance();
}