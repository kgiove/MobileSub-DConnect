/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.menu;

import mobile.sdc.task.TaskUtility;
import mobile.sdc.settings.LangXml;
import mobile.sdc.actions.ActionDecodeSmsSkus;
import mobile.sdc.rms.MessageRs;
import mobile.sdc.sku.MessageSms;
import mobile.sdc.task.TaskReceiveSms;
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.table.DefaultTableModel;
import com.sun.lwuit.table.Table;
import java.util.Vector;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author ietservizi
 */
public class IncomingMenu implements ActionListener {

    static Form ReceivedMessageMenu = null;
    Form listMessage;
    Form form;
    Container container = null;
    Table table;
    List list;
    List listMessageData;
    Vector vectorMessages = null;
    ActionDecodeSmsSkus decSms = null;
    Label mitt = null;
    Label data = null;
    Label text = null;
    private MessageSms[] messageSku;
    public static DefaultListModel myListD = null;
    public static List myList = null;

    public IncomingMenu() {

        ReceivedMessageMenu = new Form(LangXml.RecData_name);
        ReceivedMessageMenu.setLayout(new BorderLayout());
        String[] Items = {LangXml.SaleSmsName,
            LangXml.Ord_name,
            LangXml.InvSmsName,
            "Free Message"};
        myListD = new DefaultListModel(Items);
        myList = new List(myListD);

        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                switch (myListD.getSelectedIndex()) {
                    case 0:
                        listMessageTipoData(0);
                        break;
                    case 1:
                        listMessageTipoData(1);
                        break;
                    case 2:
                        listMessageTipoData(2);
                        break;
                    case 3:
                        listMessageTipoData(3);
                        break;
                }
            }
        };

        myList.addActionListener(al);
        ReceivedMessageMenu.addComponent(BorderLayout.CENTER, myList);
        ReceivedMessageMenu.addCommand(LangXml.Cmd_back1);
        ReceivedMessageMenu.setCommandListener((ActionListener) this);
    }

    public void start() {

        ReceivedMessageMenu.show();

    }

    public void listMessageTipoData(final int typeSku) {

        listMessage = new Form("");
        listMessage.setLayout(new BorderLayout());
        decSms = new ActionDecodeSmsSkus();
        try {
            vectorMessages = decSms.decodeMessage(typeSku);
        } catch (RecordStoreNotOpenException ex) {
        } catch (RecordStoreException ex) {
        }

        listMessageData = new List();
        if (vectorMessages.size() > 0) {
            for (int i = 0; i < vectorMessages.size(); i++) {
                listMessageData.addItem(vectorMessages.elementAt(i));
            }
        } else {
            listMessageData.addItem(LangXml.Neg + " " + LangXml.Msg);
        }

        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (vectorMessages.size() > 0) {
                    if (typeSku == 3) {
                        showSms(decSms.getMessage(listMessageData.getSelectedItem().toString()));
                    } else {
                        listMessagethrowTable(decSms.getMessage(listMessageData.getSelectedItem().toString()));
                    }
                }
            }
        };

        listMessageData.addActionListener(al);
        listMessage.addComponent(BorderLayout.CENTER, listMessageData);
        listMessage.addCommand(LangXml.Cmd_back2);
        listMessage.setCommandListener((ActionListener) this);
        listMessage.show();


    }

    public void listMessagethrowTable(MessageSms message) {


        if (check_messageReaded() == true) {
            StartMenu.myListD.setItem(5, LangXml.RecData_name);
            myListD.setItem(0, LangXml.SaleSmsName);
            myListD.setItem(1, LangXml.Ord_name);
            myListD.setItem(2, LangXml.InvSmsName);
        }

        if (check_messageReadedSales() == true) {
            myListD.setItem(0, LangXml.SaleSmsName);
        }
        if (check_messageReadedOrder() == true) {
            myListD.setItem(1, LangXml.Ord_name);
        }
        if (check_messageReadedInv() == true) {
            myListD.setItem(2, LangXml.InvSmsName);
        }

        Vector temp_pv = new Vector();
        Vector temp_v = new Vector();

        temp_pv = TaskUtility.parserText(message.getMessage(), ';');
        String[][] tableSkus = new String[temp_pv.size()][2];

        for (int j = 0; j < temp_pv.size(); j++) {
            temp_v = TaskUtility.parserText(temp_pv.elementAt(j).toString() + ",", ',');
            tableSkus[j][0] = temp_v.elementAt(0).toString();
            tableSkus[j][1] = temp_v.elementAt(1).toString();
//            tableSkus[j][2] = temp_v.elementAt(2).toString();
        }

        String[] tableHeaders = new String[]{LangXml.Desc/*.substring(0, 4) + "."*/,
            LangXml.Volume/*,
        LangXml.Tot + "."*/

        };
        DefaultTableModel model = new DefaultTableModel(tableHeaders,
                tableSkus, false);
        table = new Table(model);
        table.getComponentAt(0).setEnabled(false);
        table.getComponentAt(1).setEnabled(false);
//        table.getComponentAt(2).setEnabled(false);
//        table.getComponentAt(1).setPreferredW(45);
//        table.getComponentAt(2).setPreferredW(45);
        table.setScrollable(false);
        table.setIncludeHeader(true);
        form = new Form(LangXml.Msg + " " + LangXml.Det);
        int index = message.getAddress().lastIndexOf(':');
        data = new Label("Date: " + message.getTipoData());
        Label ltab = new Label("From: " + message.getAddress().substring(6, index));

        container = new Container();
        container.addComponent(data);
        container.addComponent(ltab);
        container.addComponent(table);
        form.setLayout(new BorderLayout());
        form.addComponent(BorderLayout.CENTER, container);
        form.addCommand(LangXml.Cmd_back3);
        form.setCommandListener((ActionListener) this);
        form.show();
    }

    public void showSms(MessageSms message) {

        if (check_messageReaded() == true) {
            StartMenu.myListD.setItem(5, LangXml.RecData_name);
            myListD.setItem(0, LangXml.SaleSmsName);
            myListD.setItem(1, LangXml.Ord_name);
            myListD.setItem(2, LangXml.InvSmsName);
        }

        if (check_messageReadedFree() == true) {
            myListD.setItem(3, "Free Message");
        }


        form = new Form("");
        container = new Container();
        int index = message.getAddress().lastIndexOf(':');
        mitt = new Label("Mitt: " + message.getAddress().substring(6, index));
        data = new Label("Date: " + message.getTipoData());

        TextArea messageArea = new TextArea(5, 20);
        messageArea.setText(message.getMessage());
        messageArea.setEditable(false);
        container.addComponent(data);
        container.addComponent(mitt);
        container.addComponent(messageArea);
        form.setLayout(new BorderLayout());
        form.addComponent(BorderLayout.CENTER, container);
        form.addCommand(LangXml.Cmd_back3);
        form.setCommandListener(this);
        form.show();
    }

    public boolean check_messageReaded() {
        boolean result = true;
        TaskReceiveSms.quequeMessage = new MessageRs("quequeMessage");
        messageSku = TaskReceiveSms.quequeMessage.getMessageSmsList();
        for (int i = 0; i < messageSku.length; i++) {
            if (messageSku[i].getLetto() == false) {
                result = false;
            }
        }
        return result;


    }

    public boolean check_messageReadedSales() {
        boolean result = true;
        TaskReceiveSms.quequeMessage = new MessageRs("quequeMessage");
        messageSku = TaskReceiveSms.quequeMessage.getMessageSmsList();
        for (int i = 0; i < messageSku.length; i++) {
            if (messageSku[i].getLetto() == false
                    && messageSku[i].getMessage().startsWith("SALE ")) {
                result = false;
            }
        }
        return result;
    }

    public boolean check_messageReadedOrder() {
        boolean result = true;
        TaskReceiveSms.quequeMessage = new MessageRs("quequeMessage");
        messageSku = TaskReceiveSms.quequeMessage.getMessageSmsList();
        for (int i = 0; i < messageSku.length; i++) {
            if (messageSku[i].getLetto() == false
                    && messageSku[i].getMessage().startsWith("ORDER ")) {
                result = false;
            }
        }
        return result;


    }

    public boolean check_messageReadedInv() {
        boolean result = true;
        TaskReceiveSms.quequeMessage = new MessageRs("quequeMessage");
        messageSku = TaskReceiveSms.quequeMessage.getMessageSmsList();
        for (int i = 0; i < messageSku.length; i++) {
            if (messageSku[i].getLetto() == false
                    && messageSku[i].getMessage().startsWith("INV ")) {
                result = false;
            }
        }
        return result;
    }

    public boolean check_messageReadedFree() {
        boolean result = true;
        TaskReceiveSms.quequeMessage = new MessageRs("quequeMessage");
        messageSku = TaskReceiveSms.quequeMessage.getMessageSmsList();
        for (int i = 0; i < messageSku.length; i++) {
            if (messageSku[i].getLetto() == false
                    && messageSku[i].getCode() == 3) {
                result = false;
            }
        }
        return result;


    }

    public void actionPerformed(ActionEvent ae) {
        Command c = ae.getCommand();
        if (c.getId() == 1) {
            StartMenu.form_Main.show();
        } else if (c.getId() == 2) {
            ReceivedMessageMenu.show();
        } else if (c.getId() == 3) {
            ReceivedMessageMenu.show();
        }
    }
}