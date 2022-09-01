package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;

public final class CardUtils {

    private CardUtils(){
    }

    public static String generateCardNumber(CardRepository cardRepository){
        String number = ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                ((int) ((Math.random() * (9999 - 1000)) + 1000));

        while (cardRepository.findByNumber(number) != null){
            number = ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                    ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                    ((int) ((Math.random() * (9999 - 1000)) + 1000)) + " " +
                    ((int) ((Math.random() * (9999 - 1000)) + 1000));
        }
        return number;
    }

    public static int ganerateCvvNumber(){
        return ((int) ((Math.random() * (999 - 100)) + 100));
    }
}
