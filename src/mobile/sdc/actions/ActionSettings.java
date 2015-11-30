/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.actions;

/**
 *
 * @author ietservizi
 */
import mobile.sdc.settings.LangXml;
import mobile.sdc.rms.ComunicationSettingRs;
import com.sun.lwuit.*;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.list.DefaultListModel;
import java.util.Date;
import java.util.Timer;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import mobile.sdc.menu.StartMenu;
import mobile.sdc.rms.ProductRs;
import mobile.sdc.sku.ComunicationSetting;
import mobile.sdc.task.TaskDownload;
import mobile.sdc.task.TaskReceiveSms;
import mobile.sdc.task.TaskUtility;

public class ActionSettings implements ActionListener {

    private TaskDownload timerTask = null;
    private TextField number, gwy_number, your_number;
    public static Form formSettingMenu = null;
    private Form formSettings = null;
    private Form formTimer = null;
    private ComunicationSetting config = null;
    private Container container = null;
    static public ComunicationSetting[] numberList;
    static public ComunicationSettingRs nb;
    private CheckBox[] systemCommcheck = new CheckBox[2];
    String[] systemComm = {"Send to Mobile", "Send to Gateway"};
    ComboBox conCombo = null;
    private ComboBox conComboTm, hour, min, mr;
    private Dialog date;
    private DefaultListModel simpleListModel;
    private ComboBox conComboDay;
    private String day = "";
    private int numDay = -1;
    private String frequency = "";
    private String time = "";

    public void start() {

        formSettingMenu = new Form(LangXml.Settings_name);
        formSettingMenu.setLayout(new BorderLayout());
        String[] Items = {
            LangXml.Ck_PM,
            LangXml.DelProdmaster,
            LangXml.SetComunication,
            LangXml.SetTimer,
            LangXml.LanguageSett,
            "About"
        };

        final DefaultListModel myListD = new DefaultListModel(Items);
        List myList = new List(myListD);
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                switch (myListD.getSelectedIndex()) {

                    case 0:
                        timerTask = new TaskDownload();
                        new Thread(timerTask).start();
                        break;

                    case 1:
                        if (Dialog.show(LangXml.Msg, LangXml.Dlg_AltDelPM, "Ok", "Cancel")) {
                            deleteProductMaster();
                        }
                        break;

                    case 2:
                        setCommunication();
                        break;

                    case 3:
                        setTimer();
                        break;

                    case 4:
                        ActionLanguageSettings ls = new ActionLanguageSettings();
                        ls.start();
                        break;

                    case 5:
                        showInfoApp();
                        break;
                }
            }
        };

        myList.addActionListener(al);
        formSettingMenu.addComponent(BorderLayout.CENTER, myList);
        formSettingMenu.addCommand(LangXml.Cmd_back1);
        formSettingMenu.setCommandListener(this);
        formSettingMenu.show();
    }

    private void setCommunication() {

        nb = new ComunicationSettingRs("Number_Setting");
        numberList = nb.getNumber();
        formSettings = new Form("");
        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        Label lb_TypeProtocol = new Label("Set Communication method:");
        conCombo = new ComboBox();
        conCombo.addItem("AUTOMATIC");
        conCombo.addItem("GPRS");
        conCombo.addItem("SMS");

        Label lb_MobNumber = new Label("Set Destination Mobile Number:");
        Label lb_GatewayNumber = new Label("Set sms Gateway Number:");
        Label lb_YourNumber = new Label("Set Your Number:*");

        number = new TextField(10);
        number.setConstraint(TextField.NUMERIC);
        number.setInputMode("123");

        gwy_number = new TextField(10);
        gwy_number.setConstraint(TextField.PHONENUMBER);
        gwy_number.setInputMode("123");

        your_number = new TextField(10);
        your_number.setConstraint(TextField.PHONENUMBER);
        your_number.setInputMode("123");

        if (numberList.length > 0) {
            if (!(numberList[0].getYourMobileNumber().equals(""))) {
                your_number.setText(numberList[0].getYourMobileNumber());
            }
            if (!(numberList[0].getGatewayNumber().equals(""))) {
                gwy_number.setText(numberList[0].getGatewayNumber());
            }
            if (!(numberList[0].getMobileNumber().equals(""))) {
                number.setText(numberList[0].getMobileNumber());
            }
            conCombo.setSelectedItem(numberList[0].getWichProtocol());
        }

        container.addComponent(lb_TypeProtocol);
        container.addComponent(conCombo);
        container.addComponent(lb_YourNumber);
        container.addComponent(your_number);
        container.addComponent(lb_MobNumber);
        container.addComponent(number);
        container.addComponent(lb_GatewayNumber);
        container.addComponent(gwy_number);

        for (int i = 0; i < systemCommcheck.length; i++) {
            //create a checkbox
            systemCommcheck[i] = new CheckBox(systemComm[i]);
            if (i % 2 == 0) {
                systemCommcheck[i].getStyle().setMargin(Label.RIGHT, 15);
            }
        }
        if (numberList.length > 0) {
            systemCommcheck[0].setSelected(numberList[0].isMobile());
            systemCommcheck[1].setSelected(!numberList[0].isMobile());
        }
        if (conCombo.getSelectedItem().toString().equals("GPRS")) {
            number.setEnabled(false);
            gwy_number.setEnabled(false);
            systemCommcheck[0].setEnabled(false);
            systemCommcheck[1].setEnabled(false);
        }
        container.addComponent(systemCommcheck[0]);
        container.addComponent(systemCommcheck[1]);

        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                if (!conCombo.getSelectedItem().toString().equals("GPRS")) {
                    number.setEnabled(true);
                    gwy_number.setEnabled(true);
                    systemCommcheck[0].setEnabled(true);
                    systemCommcheck[1].setEnabled(true);

                    if (systemCommcheck[0].isSelected() && systemCommcheck[0].hasFocus()) {
                        systemCommcheck[1].setSelected(false);
                    } else if (systemCommcheck[1].isSelected()) {
                        systemCommcheck[0].setSelected(false);
                    }
                } else {
                    number.setEnabled(false);
                    gwy_number.setEnabled(false);
                    systemCommcheck[0].setEnabled(false);
                    systemCommcheck[1].setEnabled(false);
                }
            }
        };

        conCombo.addActionListener(al);
        systemCommcheck[0].addActionListener(al);
        systemCommcheck[1].addActionListener(al);

        formSettings.setLayout(new BorderLayout());
        formSettings.addComponent(BorderLayout.CENTER, container);
        formSettings.addCommand(new Command("Back", 3));
        formSettings.addCommand(LangXml.Cmd_save2);
        formSettings.setCommandListener(this);
        formSettings.show();

    }

    public void saveCommunication() throws RecordStoreNotOpenException, RecordStoreException {

        numberList = nb.getNumber();
        config = new ComunicationSetting();
        config.setYourMobileNumber(your_number.getText());
        config.setGatewayNumber(gwy_number.getText());
        config.setMobileNumber(number.getText());
        if (systemCommcheck[0].isSelected()) {
            config.setIsMobile(true);
        }
        if (systemCommcheck[1].isSelected()) {
            config.setIsMobile(false);
        }
        config.setWichProtocol(conCombo.getSelectedItem().toString());
        if (numberList.length > 0) {
            config.setDay(numberList[0].getDay());
            config.setNumDay(numberList[0].getNumDay());
            config.setFrequency(numberList[0].getFrequency());
            config.setTime(numberList[0].getTime());
        }
        nb.addNumber(config);
        nb.close();
        Dialog.show(LangXml.Msg, LangXml.Protocol_ok, "Ok", null);
        formSettingMenu.show();
    }

    private void setTimer() {

        nb = new ComunicationSettingRs("Number_Setting");
        numberList = nb.getNumber();
        Label lb_sp = new Label("");
        formTimer = new Form("Timer Setting");
        String[] itemsConf = {"Set Frequency",
            "Set Time"};

        final List myList = new List(itemsConf);
        DefaultListCellRenderer conf_ListDF = new DefaultListCellRenderer();
        myList.setListCellRenderer(conf_ListDF);


        String[] items = {
            "Frequency : ",
            "Day : ",
            "Time: "
        };

        simpleListModel = new DefaultListModel(items);

        DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
        dlcr.setShowNumbers(false);
        dlcr.setFocusable(false);
        dlcr.setFocus(false);
        if (numberList.length > 0) {
            if (!numberList[0].getFrequency().equals("")) {

                frequency = numberList[0].getFrequency();
                simpleListModel.setItem(0, items[0] + frequency);

                day = numberList[0].getDay();
                numDay = numberList[0].getNumDay();
                simpleListModel.setItem(1, items[1] + day);

                time = numberList[0].getTime();
                simpleListModel.setItem(2, items[2] + time);

            }
        }
        List viewList = new List(simpleListModel);
        viewList.setEnabled(false);
        viewList.getModel().setSelectedIndex(-1);
        viewList.setListCellRenderer(dlcr);

        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                switch (myList.getSelectedIndex()) {

                    case 0:
                        showConfigurationFrequencyTimer();
                        if (!conComboDay.isEnabled()) {
                            day = "NA";
                            frequency = conComboTm.getSelectedItem().toString();
                            simpleListModel.setItem(0, "Frequency: "
                                    + frequency);
                            simpleListModel.setItem(1, "Day: " + day);
                            numDay = -1;
                        } else {
                            frequency = conComboTm.getSelectedItem().toString();
                            day = conComboDay.getSelectedItem().toString();
                            numDay = conComboDay.getSelectedIndex();
                            simpleListModel.setItem(0, "Frequency: "
                                    + frequency);
                            simpleListModel.setItem(1, "Day: "
                                    + day);
                        }

                        break;

                    case 1:
                        showConfigurationTime();
                        time = hour.getSelectedItem().toString()
                                + ":" + min.getSelectedItem().toString()
                                + ":" + mr.getSelectedItem().toString();
                        simpleListModel.setItem(2, "Time: " + time);
                        break;
                }
            }
        };

        myList.addActionListener(al);
        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        container.addComponent(myList);
        container.addComponent(lb_sp);
        container.addComponent(viewList);
        formTimer.setLayout(new BorderLayout());
        formTimer.addComponent(BorderLayout.CENTER, container);
        formTimer.addCommand(new Command("Back", 3));
        formTimer.setCommandListener(this);
        formTimer.show();
    }

    public void saveTimer() throws RecordStoreNotOpenException, RecordStoreException {

        numberList = nb.getNumber();
        config = new ComunicationSetting();
        config.setDay(day);
        config.setNumDay(numDay);
        config.setFrequency(frequency);
        config.setTime(time);
        if (numberList.length > 0) {
            config.setYourMobileNumber(numberList[0].getYourMobileNumber());
            config.setGatewayNumber(numberList[0].getGatewayNumber());
            config.setMobileNumber(numberList[0].getMobileNumber());
            config.setIsMobile(numberList[0].isMobile());
            config.setWichProtocol(numberList[0].getWichProtocol());
        }

        nb.addNumber(config);
        nb.close();
        formSettingMenu.show();
    }

    public void startTimer() {

        if (TaskReceiveSms.timer != null) {
            TaskReceiveSms.timer.cancel();
            TaskReceiveSms.timer = new Timer();
        } else {
            TaskReceiveSms.timer = new Timer();
        }

        if (frequency.equals("Every Day")) {
            timerTask = new TaskDownload("ED", numDay, time);
        } else {
            timerTask = new TaskDownload("EW", numDay, time);
        }
        TaskReceiveSms.timer.schedule(timerTask, new Date(), 60000);
    }

    private void showInfoApp() {

        Label lb_infoAppVersion = new Label("Application Version:");
        Label lb_currentAppVersion = new Label("Mobile Sub-DConnect 2.3");
        Label lb_infoPM = new Label("Product Master Version :");
        Label lb_infoRealease = new Label("Release Date: 18 June 2012");
        Label lb_currentPM = new Label(TaskUtility.getCurrentPmVersion());
        Dialog info = new Dialog("Info");
        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        container.addComponent(lb_infoAppVersion);
        container.addComponent(lb_currentAppVersion);
        container.addComponent(lb_infoRealease);
        container.addComponent(lb_infoPM);
        container.addComponent(lb_currentPM);
        info.setLayout(new BorderLayout());
        info.addComponent(BorderLayout.CENTER, container);
        info.addCommand(new Command("OK"));
        info.show(60, 10, 10, 20, false);
    }

    private void showConfigurationFrequencyTimer() {

        conComboTm = new ComboBox();
        conComboTm.addItem("Every Day");
        conComboTm.addItem("Every Week");

        conComboDay = new ComboBox();
        conComboDay.addItem("Sunday");
        conComboDay.addItem("Monday");
        conComboDay.addItem("Tuesday");
        conComboDay.addItem("Wednesday");
        conComboDay.addItem("Thursday");
        conComboDay.addItem("Friday");
        conComboDay.addItem("Saturday");


        date = new Dialog("");
        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        container.addComponent(conComboTm);
        container.addComponent(conComboDay);
        if (conComboTm.getSelectedItem().toString().equals("Every Day")) {
            conComboDay.setEnabled(false);
        }
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (!conComboTm.getSelectedItem().toString().equals("Every Day")) {
                    conComboDay.setEnabled(true);
                } else {
                    conComboDay.setEnabled(false);
                }
            }
        };
        conComboTm.addActionListener(al);
        date.setLayout(new BorderLayout());
        date.addComponent(BorderLayout.CENTER, container);
        date.addCommand(new Command("OK"));
        date.show(60, 10, 10, 20, false);


    }

    private void showConfigurationTime() {

        Label lb_hour = new Label("Hour:");
        Label lb_min = new Label("Minute:");
        Label lb_mr = new Label("Meridia:");


        date = new Dialog("Edit");
        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        container.addComponent(lb_hour);
        container.addComponent(getComboBoxHours());
        container.addComponent(lb_min);
        container.addComponent(getComboBoxMinutes());
        container.addComponent(lb_mr);
        container.addComponent(getComboBoxMeridians());
        date.setLayout(new BorderLayout());
        date.addComponent(BorderLayout.CENTER, container);
        date.addCommand(new Command("OK"));
        date.show(60, 10, 10, 20, false);

    }

    private ComboBox getComboBoxHours() {

        hour = new ComboBox();
        hour.addItem("01");
        hour.addItem("02");
        hour.addItem("03");
        hour.addItem("04");
        hour.addItem("05");
        hour.addItem("06");
        hour.addItem("07");
        hour.addItem("08");
        hour.addItem("09");
        hour.addItem("10");
        hour.addItem("11");
        hour.addItem("12");
        return hour;
    }

    private ComboBox getComboBoxMinutes() {

        min = new ComboBox();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                min.addItem("0" + i);
            } else {
                min.addItem(i + "");
            }
        }
        return min;
    }

    private ComboBox getComboBoxMeridians() {

        mr = new ComboBox();
        mr.addItem("AM");
        mr.addItem("PM");
        return mr;
    }

    private void deleteProductMaster() {
        ActionProductMaster.db = new ProductRs("RecordsProduct");
        ActionProductMaster.db.deleteAllRecords();
    }

    public void actionPerformed(ActionEvent ae) {
        Command c = ae.getCommand();

        if (c.getId() == 1) {
            try {
                nb.close();
            } catch (Exception ex) {
            }
            StartMenu.form_Main.show();
        } else if (c.getId() == 2) {
            try {
                if (!your_number.getText().toString().equals("")) {
                    if (conCombo.getSelectedItem().toString().equals("GPRS")) {
                        saveCommunication();
                    } else if (conCombo.getSelectedItem().toString().equals("AUTOMATIC")) {
                        if (systemCommcheck[1].isSelected() && !gwy_number.getText().equals("")) {
                            saveCommunication();

                        } else {
                            Dialog.show(LangXml.Msg, "System Comunication no ok", "Ok", null);
                        }

                    } else {
                        if ((number.getText().equals("")
                                && gwy_number.getText().equals(""))) {
                            Dialog.show(LangXml.Msg, LangXml.Dlg_NumAlert, "Ok", null);
                        } else {
                            if (!(!systemCommcheck[0].isSelected()
                                    && !systemCommcheck[1].isSelected())) {
                                if (systemCommcheck[0].isSelected() && !number.getText().equals("")
                                        || systemCommcheck[1].isSelected() && !gwy_number.getText().equals("")) {
                                    saveCommunication();
                                } else {
                                    Dialog.show(LangXml.Msg, "System Comunication no ok", "Ok", null);
                                }
                            } else {
                                Dialog.show(LangXml.Msg, "System Comunication mast be setted!", "Ok", null);
                            }
                        }
                    }
                } else {
                    Dialog.show(LangXml.Msg, "Your Number have to be setted", "Ok", null);
                }
            } catch (RecordStoreNotOpenException ex) {
                ex.printStackTrace();
            } catch (RecordStoreException ex) {
                ex.printStackTrace();
            }
        } else if (c.getId() == 3) {
            try {
                if (!(day.equals("") || frequency.equals("") || time.equals(""))) {
                    saveTimer();
                    startTimer();
                }
            } catch (Exception ex) {
            }
            formSettingMenu.show();
        }
    }
}