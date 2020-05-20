package com.tui;

import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;
import com.mybank.data.DataSource;
import com.mybank.domain.Bank;
import com.mybank.domain.Account;
import com.mybank.domain.Customer;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.SavingsAccount;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        DataSource dataSource = new DataSource("data/test.dat");
        dataSource.loadData();
        
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 15, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 12);
        custWin.addButton("&Show", 28, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText());
                    Customer customer = Bank.getCustomer(custNum);
                    
                    StringBuilder customerInfo = new StringBuilder();          
                    customerInfo.append("Owner Name: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append(" (id=").append(custNum).append(")");
                    
                    for (int i = 0; i < customer.getNumberOfAccounts(); i++) {
                        customerInfo.append("\nAccount Type:");
                        Account account = customer.getAccount(i);
                        
                        if (account instanceof CheckingAccount) {
                            customerInfo.append("'Checking'\n");
                        } else if (account instanceof SavingsAccount) {
                            customerInfo.append("'Savings'\n");
                        } else {
                            customerInfo.append("Not Found");
                            continue;
                        }
                        customerInfo.append("Account Balance: $").append(account.getBalance());
                    }
                    //details about customer with index==custNum
                    details.setText(customerInfo.toString());
                } catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
            }
        });
    }
}
