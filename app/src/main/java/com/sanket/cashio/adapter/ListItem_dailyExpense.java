package com.sanket.cashio.adapter;

public class ListItem_dailyExpense {

    String created;
    int expense;
    String expenseName,detail;
    String catagory;
    int ignored;


    public ListItem_dailyExpense(String created, int expense, String expenseName, String catagory,int ignored,String Detail) {
        this.created=created;
        this.expense=expense;
        this.expenseName=expenseName;
        this.catagory=catagory;
        this.ignored=ignored;
        this.detail=Detail;
    }

    public String getCreated() {
        return created;
    }

    public int getIgnored() {
        return ignored;
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
