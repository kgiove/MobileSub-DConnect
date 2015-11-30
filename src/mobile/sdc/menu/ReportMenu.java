/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.menu;

import mobile.sdc.actions.ActionReport;
import mobile.sdc.settings.LangXml;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListModel;

/**
 *
 * @author ietservizi
 */
public class ReportMenu implements ActionListener {

    static public Form formReportMenu;
    List list;
    String[][] prova;
    ActionReport r;

    public ReportMenu() {
    }

    public void start() {

        formReportMenu = new Form(LangXml.Report_name);
        formReportMenu.setLayout(new BorderLayout());
        String[] Items = {LangXml.RepSale,
            LangXml.RepOrd,
            LangXml.RepInv
        };
        r = new ActionReport();
        final DefaultListModel myListD = new DefaultListModel(Items);
        List myList = new List(myListD);
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                switch (myListD.getSelectedIndex()) {

                    case 0:
                        r.listSelectedReportSkus(0);
                        break;
                    case 1:
                        r.listSelectedReportSkus(1);
                        break;
                    case 2:
                        r.listSelectedReportSkus(2);
                        break;
                }
            }
        };

        myList.addActionListener(al);

        formReportMenu.addComponent(BorderLayout.CENTER, myList);
        formReportMenu.addCommand(LangXml.Cmd_back1);
        formReportMenu.setCommandListener(this);
        formReportMenu.show();
    }

    public void actionPerformed(ActionEvent ae) {
        Command c = ae.getCommand();
        if (c.getId() == 1) {
            StartMenu.form_Main.show();
        }
    }
}