package com.payflow.transaction.provider;

import org.springframework.stereotype.Component;

@Component
public class PaymentProviderSimulator {

    public boolean processPayment()
    {
       return Math.random()<0.8;
       // throw new RuntimeException("Payment provider is unavailable");
        //throw new RuntimeException(" Temporary Payment provider failure");

    }
}
