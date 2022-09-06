package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.repositories.AccountRepository;

public class AccountUtils {
    public static String generateAccountNumber(AccountRepository accountRepository){
        String number = "VIN " + ((int) ((Math.random() * (99999999 - 0)) + 0));

        while(accountRepository.findByNumber(number) != null){
            number = "VIN " + ((int) ((Math.random() * (99999999 - 0)) + 0));
        }
        return number;
    }

}
