package com.sanket.cashio.adapter;

public class ListItem_dailyExpense {

    String created;
    int expense;
    String expenseName,detail;
    String catagory;
    int ignored;
    int lend,investment,loan;


    public ListItem_dailyExpense(String created, int expense, String expenseName, String catagory,int ignored,String Detail,int investment,int lend,int loan) {
        this.created=created;
        this.expense=expense;
        this.expenseName=expenseName;
        this.catagory=catagory;
        this.ignored=ignored;
        this.detail=Detail;
        this.investment=investment;
        this.lend=lend;
        this.loan=loan;
    }

    public String getCreated() {
        return created;
    }

    public int getIgnored() {
        return ignored;
    }
    public int getInvestment() {
        return investment;
    }
    public int getLend() {
        return lend;
    }
    public int getLoan() {
        return loan;
    }

    public int getExpense() {
        return expense;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public String getCatagory() {
        return catagory;
    }

    public String getDetails() {
        return detail;
    }

}
