package com.sanket.cashio.adapter;

public class ListItem_balance {

    String lastUpdated;
    int balance;
    String bankName,accountNumber;
    String message;


    public ListItem_balance(String lastUpdated, int balance, String bankName, String accountNumber,String message) {
        this.lastUpdated=lastUpdated;
        this.balance=balance;
        this.bankName=bankName;
        this.accountNumber=accountNumber;
        this.message=message;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public int getBalance() {
        return balance;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getMessage() {
        return message;
    }


}

