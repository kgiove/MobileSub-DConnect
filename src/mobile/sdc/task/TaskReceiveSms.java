/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.task;

import mobile.sdc.task.TaskEngine;
import mobile.sdc.rms.MessageRs;
import mobile.sdc.sku.MessageSms;
import mobile.sdc.menu.IncomingMenu;
import mobile.sdc.sku.Product;
import mobile.sdc.actions.ActionProductMaster;
import mobile.sdc.rms.ProductRs;
import mobile.sdc.menu.StartMenu;
import com.sun.lwuit.Dialog;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.PushRegistry;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;
import mobile.sdc.rms.ComunicationSettingRs;
import mobile.sdc.actions.ActionSettings;
import mobile.sdc.sku.SmsUpdatePM;
import mobile.sdc.rms.SmsUpdatePmRs;
import mobile.sdc.settings.LangXml;

/**
 *
 * @author ietservizi
 */
public class TaskReceiveSms {

    public static Timer timer = null;
    private TaskDownload timerTask = null;
    public static MessageConnection connection = null;
    public static MessageRs quequeMessage = null;
    public static TaskEngine manager;
    private Controller controller;
    final static String codeMP = "UPPM";
    final static String codeDL = "PMDL";
    final static String codeAD = "PMAD";
    final static String codeSA = "SALE";
    final static String codeOR = "ORDE";
    final static String codeIN = "INVE";
    private MessageSms message_Sku = null;
    private Vector process_decoding;
    private int skuSmsCode = 0;
    private String header = "";
    private String smsPort = "2501";
    private String smsConnectionStr = "sms://:" + smsPort;
    private String filter = "*";
    private Hashtable ht_product;
    private Product[] productList;
    private SmsUpdatePM[] SmsUpdatePMList;
    private SmsUpdatePmRs quequeSmsUpdatePm;
    private int currNumSmsPM;
    private int totNumSmsPM;
    private int uniqueKey;
    private int pmVersion;
    private String contentSms;
    private boolean result = false;

    public void SetupConnection() {

        quequeMessage = new MessageRs("quequeMessage");
        quequeSmsUpdatePm = new SmsUpdatePmRs("SmsUpdatePm");
        controller = new Controller();
        manager = new TaskEngine();
        manager.runTask(new TaskSetupConnection());
    }

    private void notifyMessageReceived(Message message) {


        String messageFromServer = "";
        header = "";
        boolean hex;
        if (message instanceof TextMessage) {
            messageFromServer = ((TextMessage) message).getPayloadText();
            hex = false;
        } else {
            messageFromServer = new String(((BinaryMessage) message).getPayloadData());
            hex = true;
        }

        messageFromServer = TaskUtility.replaceAll(messageFromServer.trim(), " ", "");
//        System.out.println("Messaggio arrivato: " + messageFromServer);

        if (messageFromServer.length() > 4) {
            header = messageFromServer.substring(0, 4);

        } else {
            header = "";
        }
        message_Sku = new MessageSms();
        process_decoding = TaskUtility.parserText(messageFromServer, ';');

        if (!(header.equals(""))) {
            if (header.equals(codeMP)) {
                dispacherActionPM(messageFromServer);
            } else if (header.equals(codeSA)
                    || header.equals(codeOR)
                    || header.equals(codeIN)) {
                decodeSkuSms(message);
            } else {
                decodeNormalMessage(message, hex);
            }
        } else {
            decodeNormalMessage(message, hex);
        }
    }

    private void decodeSkuSms(Message message) {

        if (header.equals(codeSA)) {
            StartMenu.myListD.setItem(5, "Received data      New Sms");
            IncomingMenu.myListD.setItem(0, "Sales     New Sms");
            skuSmsCode = 0;

        } else if (header.equals(codeOR)) {
            StartMenu.myListD.setItem(5, "Received data      New Sms");
            IncomingMenu.myListD.setItem(1, "Orders    New Sms");
            skuSmsCode = 1;

        } else if (header.equals(codeIN)) {
            StartMenu.myListD.setItem(5, "Received data      New Sms");
            IncomingMenu.myListD.setItem(2, "Inventory    New Sms");
            skuSmsCode = 2;

        }

        message_Sku.setTipoData(TaskUtility.getDate(message.getTimestamp()));
        message_Sku.setCode(skuSmsCode);
        message_Sku.setAddress(message.getAddress());
        String bodyMessage = "";
        for (int i = 1; i < process_decoding.size(); i++) {
            bodyMessage += process_decoding.elementAt(i).toString() + ";";
        }
        message_Sku.setMessage(bodyMessage);
        message_Sku.setLetto(false);        
        saveSms(skuSmsCode, message.getAddress(),
                header, bodyMessage, message_Sku);

    }

    private void dispacherActionPM(String messageToString) {

        int index = messageToString.indexOf(":");
        String temp = messageToString.substring(5, index);

        Vector codeskey = TaskUtility.parserText(temp + ";", ';');
        contentSms = messageToString.substring(index + 1);
        String assembledSmsOperationContent = "";

        if (codeskey.size() == 4) {
            currNumSmsPM = Integer.parseInt(codeskey.elementAt(0).toString());
            totNumSmsPM = Integer.parseInt(codeskey.elementAt(1).toString());
            uniqueKey = Integer.parseInt(codeskey.elementAt(2).toString());
            pmVersion = Integer.parseInt(codeskey.elementAt(3).toString());
        } else if (codeskey.size() == 3) {
            currNumSmsPM = Integer.parseInt(codeskey.elementAt(0).toString());
            totNumSmsPM = Integer.parseInt(codeskey.elementAt(1).toString());
            uniqueKey = Integer.parseInt(codeskey.elementAt(2).toString());
        }


        if (totNumSmsPM == 1) {
            if (quequeSmsUpdatePm.size() > 0) {
                quequeSmsUpdatePm.deleteAllRecords();
            }
            updatePM_LocalBD(pmVersion, TaskUtility.replaceAll(contentSms, " ", ""));

        } else if (codeskey.size() == 4 && totNumSmsPM != 1) {

            SmsUpdatePM incomingSmsUpdate = new SmsUpdatePM();

            if (quequeSmsUpdatePm.size() > 0) {
                quequeSmsUpdatePm.deleteAllRecords();
            }

            incomingSmsUpdate.setCurrNumSmsPM(currNumSmsPM);
            incomingSmsUpdate.setTotNumSmsPM(totNumSmsPM);
            incomingSmsUpdate.setUniqueKey(uniqueKey);
            incomingSmsUpdate.setPmVersion(pmVersion);
            incomingSmsUpdate.setContentSms(contentSms);
            quequeSmsUpdatePm.addRecord(incomingSmsUpdate);


        } else if (codeskey.size() == 3) {
            result = false;
            if (quequeSmsUpdatePm.size() > 0) {
                SmsUpdatePM incomingSmsUpdate = new SmsUpdatePM();
                SmsUpdatePMList = quequeSmsUpdatePm.getMessageSmsList();
                if (SmsUpdatePMList[0].getTotNumSmsPM() == SmsUpdatePMList.length + 1) {
                    int unqueCode = SmsUpdatePMList[0].getUniqueKey();
                    if (uniqueKey == unqueCode) {
                        for (int i = 0; i < SmsUpdatePMList.length; i++) {
                            if (SmsUpdatePMList[i].getCurrNumSmsPM() == i + 1
                                    && SmsUpdatePMList[i].getUniqueKey() == unqueCode) {
                                result = true;
                                assembledSmsOperationContent += SmsUpdatePMList[i].getContentSms();
                            } else {
                                result = false;
                            }
                        }
                    }
                    if (result) {
                        int newVersionPM = SmsUpdatePMList[0].getPmVersion();
                        assembledSmsOperationContent += contentSms;
                        quequeSmsUpdatePm.deleteAllRecords();                        
                        updatePM_LocalBD(newVersionPM, assembledSmsOperationContent);
                    }

                } else {
                    result = false;
                    incomingSmsUpdate.setCurrNumSmsPM(currNumSmsPM);
                    incomingSmsUpdate.setTotNumSmsPM(totNumSmsPM);
                    incomingSmsUpdate.setUniqueKey(uniqueKey);
                    incomingSmsUpdate.setContentSms(messageToString.substring(index + 1));
                    quequeSmsUpdatePm.addRecord(incomingSmsUpdate);
                }
            } else {
                result = false;
            }
        }

    }

    private void updatePM_LocalBD(int newVersionPM, String bodyMessage) {
        try {

            String headerOperationContent = "";
            load_HashTables_product();
            Vector v = new Vector();

            headerOperationContent = bodyMessage.substring(0, 4);

            if (headerOperationContent.startsWith("PMAD")
                    || headerOperationContent.startsWith("PMUP")
                    || headerOperationContent.startsWith("PMDL")) {
                bodyMessage = TaskUtility.replaceAll(bodyMessage, "\n", "");
                v = TaskUtility.parserText(bodyMessage.trim(), '*');
                for (int i = 0; i < v.size(); i++) {
                    if (!v.elementAt(i).toString().startsWith("PMDL")) {
                        updateSingleProductMaster(v.elementAt(i).toString().substring(5), v.elementAt(i + 1).toString());
                        i++;
                    } else {
                        deleteSingleProductMaster(v.elementAt(i).toString().substring(5));
                    }
                }
            }
            updateVersionPM(newVersionPM);

        } catch (Exception ex) {
        }
    }

    private void updateSingleProductMaster(String fpcCode, String newRecord) {

        Product old_p = null;
        Product p = new Product();
        String[][] temp = null;
        ActionProductMaster.db = new ProductRs("RecordsProduct");
        try {
            old_p = (Product) ht_product.get(fpcCode);
            temp = TaskUtility.decodeSmsPM(newRecord);
            for (int j = 0; j < temp.length; j++) {
                p.setFpc_code(Integer.parseInt(temp[j][0]));
                p.setDescription(temp[j][1]);
                p.setBrand(temp[j][2]);
                p.setCost(Double.parseDouble(temp[j][3]));
                p.setSelling_price(Double.parseDouble(temp[j][4]));
                p.setTargInv(Integer.parseInt(temp[j][5]));
            }

            if (ht_product.containsKey(fpcCode)) {
                ActionProductMaster.db.updateRecord(p, old_p.getId());
            } else {
                ActionProductMaster.db.addRecord(p);
            }
            ActionProductMaster.db.close();
        } catch (Exception ex) {
            System.out.println("Errore in update: " + ex.getMessage());
        }
    }

    private void deleteSingleProductMaster(String fpcCode) {

        Product prod = null;
        if (ht_product.containsKey(fpcCode)) {
            prod = (Product) ht_product.get(fpcCode);
            ActionProductMaster.db.deleteRecord(prod.getId());
        }

    }

    private void updateVersionPM(int newVersion) {

        productList = ActionProductMaster.db.getProductList();

        for (int i = 0; i < productList.length; i++) {

            productList[i].setVersionCode(newVersion);
            productList[i].setFpc_code(productList[i].getFpc_code());
            productList[i].setDescription(productList[i].getDescription());
            productList[i].setVolume(productList[i].getVolume());
            productList[i].setSelling_price(productList[i].getSelling_price());
            productList[i].setBrand(productList[i].getBrand());
            productList[i].setYear(productList[i].getYear());
            productList[i].setMonth(productList[i].getMonth());
            productList[i].setDay(productList[i].getDay());
            productList[i].setHour(productList[i].getHour());
            productList[i].setMin(productList[i].getMin());
            productList[i].setSec(productList[i].getSec());
            ActionProductMaster.db.updateRecord(productList[i], productList[i].getId());

        }

        Dialog.show(LangXml.Msg, " New Product Master Uploaded", "OK", null);

    }

    private void decodeNormalMessage(Message message, boolean hex) {

        skuSmsCode = 3;
        message_Sku.setTipoData(TaskUtility.getDate(message.getTimestamp()));
        message_Sku.setCode(skuSmsCode);
        message_Sku.setAddress(message.getAddress());
        if (hex) {
            message_Sku.setMessage(new String(((BinaryMessage) message).getPayloadData()));
        } else {
            message_Sku.setMessage(((TextMessage) message).getPayloadText());
        }
        message_Sku.setLetto(false);
        quequeMessage.addRecord(message_Sku);
        StartMenu.myListD.setItem(5, "Received data      New Sms");
        IncomingMenu.myListD.setItem(3, "Free Message    New Sms");
    }

    private void saveSms(int code, String address, String header,
            String bodyMessage, MessageSms message_Sku) {

        int id = -1;
        MessageSms[] quequeSms = quequeMessage.getMessageSmsList();
        if (quequeSms.length > 0) {
            for (int i = 0; i < quequeSms.length; i++) {

                if (quequeSms[i].getCode() == code
                        && quequeSms[i].getAddress().equals(address)
                        && quequeSms[i].getTipoData().equals(header)) {
                    id = quequeSms[i].getId();
                    bodyMessage = quequeSms[i].getMessage() + bodyMessage;
                    message_Sku.setMessage(bodyMessage);
                    quequeMessage.updateRecord(message_Sku, id);
                    break;

                } else if (quequeSms[i].getCode() == code
                        && quequeSms[i].getAddress().equals(address)
                        && !quequeSms[i].getTipoData().equals(header)) {
                    id = quequeSms[i].getId();
                    quequeMessage.updateRecord(message_Sku, id);
                    break;
                }
            }
            if (id == -1) {
                quequeMessage.addRecord(message_Sku);
            }
        } else {
            quequeMessage.addRecord(message_Sku);
        }

    }

    private void notifyReceiveMessageFailed(Exception reason) {
        Dialog.show("Message Alert", reason.getMessage(), "OK", null);
    }

    private void notifyCreateConnectionFailed(Exception reason) {
        Dialog.show("Message Alert", reason.getMessage(), "OK", null);
    }

    private void load_HashTables_product() {

        try {
            ht_product = new Hashtable();
            ActionProductMaster.db = new ProductRs("RecordsProduct");
            productList = ActionProductMaster.db.getProductList();
            ActionProductMaster.db.close();
            for (int i = 0; i < productList.length; i++) {
                ht_product.put(productList[i].getFpc_code() + "", productList[i]);
            }
        } catch (Exception ex) {
        }
    }

    private boolean checkRegistered() {

        boolean resultReg = false;
        String[] connections = PushRegistry.listConnections(false);
        for (int i = 0; i < connections.length; i++) {
            if (connections[i].equals(smsConnectionStr)) {
                resultReg = true; //Applicazione registra correttamente
            }
        }
        return resultReg; //Registrazione fallita
    }

    public void startTimer() {

        try {
            int fday = 0;
            String ftime = "";
            ActionSettings.nb = new ComunicationSettingRs("Number_Setting");
            ActionSettings.numberList = ActionSettings.nb.getNumber();
            ActionSettings.nb.close();
            if (ActionSettings.numberList.length > 0) {
                if (ActionSettings.numberList[0].getFrequency().equals("Every Day")) {
                    timer = new Timer();
                    ftime = ActionSettings.numberList[0].getTime();
                    timerTask = new TaskDownload(ftime);
                    timer.schedule(timerTask, new Date(), 60000);

                } else if (ActionSettings.numberList[0].getFrequency().equals("Every Week")) {
                    timer = new Timer();
                    fday = ActionSettings.numberList[0].getNumDay();
                    ftime = ActionSettings.numberList[0].getTime();
                    timerTask = new TaskDownload("EW", fday, ftime);
                    timer.schedule(timerTask, new Date(), 60000);
                }
            }
        } catch (Exception ex) {
            System.out.println("Errore:" + ex.getMessage());
        }
    }

    private class Controller implements MessageListener {

        public void notifyIncomingMessage(MessageConnection messageConnection) {
            System.out.println("Message received!");
            manager.runTask(new TaskReceiveMessage());
        }
    }

    private class TaskSetupConnection implements Runnable {

        public void run() {
            try {
                if (connection != null) {
                    connection.close();
                    connection = null;
                }
                connection = (MessageConnection) Connector.open(smsConnectionStr, Connector.READ_WRITE);
                connection.setMessageListener(controller);

                if (!checkRegistered()) {
                    PushRegistry.registerConnection(smsConnectionStr, StartMenu.midletClassName, filter);
                }
                // commento StartTimer()
                startTimer();
            } catch (Exception e) {
                notifyCreateConnectionFailed(e);
            }
        }
    }

    private class TaskReceiveMessage implements Runnable {

        public void run() {
            try {
                Message message = connection.receive();
                notifyMessageReceived(message);
            } catch (Exception e) {
                notifyReceiveMessageFailed(e);
            }
        }
    }
}