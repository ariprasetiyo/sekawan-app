package com.arprast.sekawan.paymo;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;

public class MoneyHelper {
    public static final CurrencyUnit IDR_CURRENCY = CurrencyUnit.of("IDR");
    public static final Money NOL;

    public MoneyHelper() {
    }

    public static Money IDR(long amount) {
        return Money.ofMajor(IDR_CURRENCY, amount);
    }

    public static Money IDR(double amount) {
        return Money.of(IDR_CURRENCY, amount);
    }

    public static Money parse(BigDecimal amount, String currency, Money defaultIfNull) {
        return amount != null ? Money.of(CurrencyUnit.of(currency), amount) : defaultIfNull;
    }

    public static Money parse(String amount) {
        String[] amounts = amount.split("\s");
        return Money.of(CurrencyUnit.of(amounts[0]), BigDecimal.valueOf(Double.valueOf(amounts[1]))) ;
    }


    static {
        NOL = Money.zero(IDR_CURRENCY);
    }
}

