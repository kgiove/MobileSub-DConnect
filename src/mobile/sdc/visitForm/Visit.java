/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.visitForm;

import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import javax.microedition.rms.RecordStoreException;
import mobile.sdc.settings.LangXml;
import mobile.sdc.sku.Product;
import mobile.sdc.actions.ActionProductMaster;
import mobile.sdc.actions.ActionSettings;
import mobile.sdc.task.TaskSendMessage;
import mobile.sdc.task.TaskReceiveSms;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.table.DefaultTableModel;
import com.sun.lwuit.table.Table;
import com.sun.lwuit.table.TableModel;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.rms.RecordStoreNotOpenException;
import mobile.sdc.sku.Sku;
import mobile.sdc.task.TaskUtility;
import mobile.sdc.rms.ProductRs;
import mobile.sdc.rms.ComunicationSettingRs;
import mobile.sdc.rms.Rs;
import mobile.sdc.task.TaskSendThrowsGprs;

/**
 *
 * @author ietservizi
 */
public class Visit implements ActionListener {

    protected List chooseBrand, chooseProduct;
    protected Form formBrands, formProducts, formIns, form, formAllSkus, Det_Form;
    protected String BrandProductList = "";
    protected String TypeProduct = "";
    protected TextField num_sales;
    protected TextField vol;
    protected Product[] productList = null;
    protected int addAllControl = 0;
    protected TextField fpc_code;
    protected TextField brand;
    protected TextField description;
    protected TextField amount;
    protected String desc = "";
    protected String FPC = "";
    protected String[][] skuTable = null;
    protected Table table;
    protected TableModel model;
    protected Label totLabel;
    protected Container formContainer = null;
    protected Product rif_product_current = null;
    protected String msg = "";
    public String curr_PMVersion = "";
    protected Hashtable ht_product = null;
    public Rs skus = null;
    public static Rs skuReport = null;
    protected Hashtable ht_skus = null;
    protected Sku[] skusList = null;
    protected Sku currentSku;
    protected int val = 0;
    protected int typeSku = -1;
    protected String titleForm = "";
    int year, month, day, hour, minute, seconds;

    protected void setTypeSku(int typeSku) {
        this.typeSku = typeSku;
    }

    protected void setTitleForm(String titleForm) {
        this.titleForm = titleForm;
    }

    protected void open_AllRecords() {
        try {

            ActionProductMaster.db = new ProductRs("RecordsProduct");
            ActionSettings.nb = new ComunicationSettingRs("Number_Setting");
            skus = new Rs("RecordSku");
            skuReport = new Rs("Record_Reports");
        } catch (Exception ex) {
            System.out.println("Errore" + ex.getMessage());
        }
    }

    protected void close_AllRecords() {
        try {
            ActionProductMaster.db.close();
            ActionSettings.nb.close();
            skus.close();
            skuReport.close();
        } catch (RecordStoreNotOpenException ex) {
            System.out.println("Errore:" + ex.getMessage());
        } catch (RecordStoreException ex) {
            System.out.println("Errore:" + ex.getMessage());
        }
    }

    protected void list_Skus() {
        desc = "";
        addAllControl = 0;
        try {
            skusList = skus.getRecordsList(typeSku);
            skuTable = new String[skusList.length][2];
            for (int i = 0; i < skusList.length; i++) {
                desc = skusList[i].getDescription();
                if (desc != null) {
                    skuTable[i][0] = skusList[i].getVolume() + "";
                    skuTable[i][1] = desc;
                }
            }
            form = new Form(titleForm);
            form.setLayout(new BorderLayout());
            totLabel = new Label(LangXml.TotAm + " : " + getTot() + "");
            form.addComponent(BorderLayout.NORTH, totLabel);

            model = new DefaultTableModel(
                    new String[]{LangXml.Volume, LangXml.Desc},
                    skuTable) {
            };
            table = new Table(model) {

                protected Component createCell(Object value, int row, int column, boolean editable) {
                    Component c = super.createCell(value, row, column, false);
                    if ((column == 0) && (row > -1)) {
                        final TextField t = new TextField(5);
                        t.setText(value.toString());
                        t.setConstraint(TextField.NUMERIC | TextField.NON_PREDICTIVE);
                        t.setInputMode("123");
                        t.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent ae) {
                                if (!t.getText().equals("")) {
                                    save_SkuVolume(t.getText(), (String) table.getModel().
                                            getValueAt(table.getSelectedRow(), 1));
                                } else {
                                    deleteSku((String) table.getModel().
                                            getValueAt(table.getSelectedRow(), 1));
                                }
                                totLabel.setText(LangXml.TotAm + " : " + getTot() + "");
                            }
                        });
                        return t;
                    } else {
                        return c;
                    }
                }

                public boolean isCellEditable(int row, int col) {
                    return col != 1;
                }
            };
            table.getComponentAt(0).setEnabled(false);
            table.getComponentAt(1).setEnabled(false);
            table.setScrollable(false);
            table.setIncludeHeader(true);
            form.addComponent(BorderLayout.CENTER, table);
            form.addCommand(LangXml.Cmd_back1);
            if (skuTable.length > 0) {
                form.addCommand(LangXml.Cmd_send15);
                form.addCommand(LangXml.Cmd_details2);
                form.addCommand(LangXml.Cmd_delete3);
            }
            form.addCommand(LangXml.Cmd_AddAllSkus4);
            form.addCommand(LangXml.Cmd_AddSku5);
            form.setCommandListener((ActionListener) this);
            form.show();
        } catch (Exception ex) {
            System.out.println("Errore " + ex.getMessage());
        }
    }

    protected void list_addAllSkus() {

        if (typeSku == 0) {
            ActionProductMaster.addAllControlSales = 1;
        } else if (typeSku == 1) {
            ActionProductMaster.addAllControlOrders = 1;
        } else if (typeSku == 2) {
            ActionProductMaster.addAllControlInv = 1;
        }

        addAllControl = 1;
        String[][] tableData = null;
        load_HashTables_sales();
        Vector PM_vector = ActionProductMaster.listPMbyHashtable();
        Sku s = null;
        if (PM_vector != null) {
            tableData = new String[PM_vector.size()][2];
            for (int i = 0; i < productList.length; i++) {
                Product temp = (Product) PM_vector.elementAt(i);
                if (ht_skus.containsKey(productList[i].getDescription())) {
                    s = (Sku) ht_skus.get(temp.getDescription());
                    tableData[i][0] = s.getVolume() + "";
                    tableData[i][1] = s.getDescription();
                    ht_skus.remove(temp.getDescription());
                } else {
                    tableData[i][0] = "NA";
                    tableData[i][1] = temp.getDescription();
                }
            }
        } else {
            int i = 0;
            tableData = new String[ht_skus.size()][2];
            Enumeration count = ht_skus.elements();
            while (count.hasMoreElements()) {
                s = (Sku) count.nextElement();
                tableData[i][0] = s.getVolume() + "";
                tableData[i][1] = s.getDescription();
                i++;
            }
        }
        TaskUtility.sort(tableData);
        formAllSkus = new Form(titleForm);
        formAllSkus.setLayout(new BorderLayout());
        totLabel = new Label(LangXml.TotAm + " : " + getTot() + "");
        formAllSkus.addComponent(BorderLayout.NORTH, totLabel);
        model = new DefaultTableModel(
                new String[]{LangXml.Volume, LangXml.Desc}, tableData) {

            public boolean isCellEditable(int row, int col) {
                return col != 0 && col != 1;
            }
        };

        table = new Table(model) {

            protected Component createCell(Object value, int row, int column, boolean editable) {
                Component c = super.createCell(value, row, column, false);
                if ((column == 0)) {
                    final TextField t = new TextField(5);
                    t.setText(value.toString());
                    t.setConstraint(TextField.NUMERIC | TextField.NON_PREDICTIVE);
                    t.setInputMode("123");
                    t.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent ae) {
                            if (!t.getText().equals("")) {
                                save_SkuVolume(t.getText(), (String) table.getModel().
                                        getValueAt(table.getSelectedRow(), 1));
                            } else {
                                deleteSku((String) table.getModel().
                                        getValueAt(table.getSelectedRow(), 1));
                            }
                            totLabel.setText(LangXml.TotAm + " : " + getTot() + "");
                        }
                    });
                    t.addFocusListener(new FocusListener() {

                        public void focusGained(Component cmpnt) {
                            if (cmpnt.hasFocus()) {
                                if (t.getText().equals("NA")) {
                                    t.clear();
                                }
                            }
                        }

                        public void focusLost(Component cmpnt) {
                            if (t.getText().equals("")) {
                                t.setText("NA");
                            }
                        }
                    });
                    return t;
                } else {
                    return c;
                }
            }

            public boolean isCellEditable(int row, int col) {
                return col != 1;
            }
        };
        table.getComponentAt(0).setEnabled(false);
        table.getComponentAt(1).setEnabled(false);
        table.setIncludeHeader(true);
        table.setScrollableX(false);
        table.setScrollableY(true);

        formAllSkus.addComponent(BorderLayout.CENTER, table);
        formAllSkus.addCommand(LangXml.Cmd_back1);
        if (table.getModel().getRowCount() > 0) {
            formAllSkus.addCommand(LangXml.Cmd_details2);
            formAllSkus.addCommand(LangXml.Cmd_send15);
            formAllSkus.addCommand(LangXml.Cmd_clearTable12);
        } else {
            ActionProductMaster.addAllControlOrders = 0;
        }
        formAllSkus.setScrollable(false);
        formAllSkus.setCommandListener((ActionListener) this);
        formAllSkus.show();

    }

    protected void listBrands() {
        int i = 0;
        Hashtable single_brands = new Hashtable();
        formBrands = new Form(LangXml.BrandsList);
        formBrands.setLayout(new BorderLayout());
        try {
            Vector productVector = ActionProductMaster.listPMbyHashtable();
            if (productVector != null) {
                for (i = 0; i < productVector.size(); i++) {
                    Product temp = (Product) productVector.elementAt(i);
                    if (!single_brands.containsKey(temp.getBrand().trim())) {
                        single_brands.put(temp.getBrand().trim(), "");
                    }
                }
            } else {
                productVector = new Vector();
            }
            int j = 0;
            String[] brands = new String[single_brands.size()];
            Enumeration en = single_brands.keys();
            while (en.hasMoreElements()) {
                brands[j] = (String) en.nextElement();
                j++;
            }
            TaskUtility.sort(brands);
            final DefaultListModel myListD = new DefaultListModel(brands);
            chooseBrand = new List(myListD);
            ActionListener al = new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    BrandProductList = myListD.getItemAt(myListD.getSelectedIndex()) + "";
                    listProducts(BrandProductList);
                }
            };
            chooseBrand.addActionListener(al);
            formBrands.addComponent(BorderLayout.CENTER, chooseBrand);
            formBrands.addCommand(LangXml.Cmd_back7);
            formBrands.setCommandListener((ActionListener) this);
            formBrands.show();
        } catch (Exception ex) {
        }
    }

    protected void listProducts(String brand) {

        addAllControl = 0;
        int j = 0;
        try {
            productList = ActionProductMaster.db.getProductList();
            String[] temp = new String[productList.length];
            Product theProduct = null;
            for (int i = 0; i < productList.length; i++) {
                theProduct = productList[i];
                if (theProduct.getBrand().equals(brand)) {
                    temp[i] = theProduct.getDescription();
                    j++;
                } else {
                    temp[i] = "";
                }
            }
            String[] descProduct = new String[j];
            j = 0;
            for (int i = 0; i < temp.length; i++) {
                if (!temp[i].equals("")) {
                    descProduct[j] = temp[i];
                    j++;
                }
            }
            TaskUtility.sort(descProduct);
            final DefaultListModel myListD = new DefaultListModel(descProduct);
            chooseProduct = new List(myListD);
            ActionListener al = new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    TypeProduct = myListD.getItemAt(myListD.getSelectedIndex()) + "";
                    insertSKU();
                }
            };
            chooseProduct.addActionListener(al);
            formProducts = new Form(LangXml.PrdList);
            formProducts.setLayout(new BorderLayout());
            formProducts.addComponent(BorderLayout.CENTER, chooseProduct);
            formProducts.addCommand(LangXml.Cmd_back8);
            formProducts.setCommandListener((ActionListener) this);
            formProducts.show();
        } catch (Exception ex) {
        }
    }

    protected double getTot() {

        double current_tot = 0;
        skusList = skus.getRecordsList(typeSku);
        for (int i = 0; i < skusList.length; i++) {
            current_tot = current_tot + (skusList[i].getVolume()
                    * skusList[i].getSelling_price());
        }
        return current_tot;
    }

    protected void save_SkuVolume(String vol, String description) {

        Sku s = null;
        Product p = null;
        load_HashTables_sales();

        if (ht_skus.containsKey(description)) {
            s = (Sku) ht_skus.get(description);
            updateVolume(s, vol);
        } else if (ht_product.containsKey(description)) {
            p = (Product) ht_product.get(description);
            saveSkus(p, vol);
        }
    }

    protected boolean saveSKU() {

        boolean result = false;
        setDateToSkus();
        try {
            Sku p = new Sku();
            if (addAllControl == 0) {
                Product pr = getProduct();
                p.setTypeSku(typeSku);
                p.setFpc_code(pr.getFpc_code());
                p.setDescription(pr.getDescription());
                p.setVolume(Integer.parseInt(num_sales.getText()));
                p.setSelling_price(pr.getSelling_price());
                p.setBrand(pr.getBrand());
                skus.addRecord(p);
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    protected boolean saveSkus(Product pr, Object Value) {

        boolean result = false;
        int volume = Integer.parseInt((String) Value);
        setDateToSkus();

        try {
            Sku p = new Sku();
            p.setTypeSku(typeSku);
            p.setFpc_code(pr.getFpc_code());
            p.setDescription(pr.getDescription());
            p.setVolume(volume);
            p.setSelling_price(pr.getSelling_price());
            p.setBrand(pr.getBrand());
            skus.addRecord(p);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    protected void save_SkusReport(int typeSku){
        
        Sku s = new Sku();
        skusList = skus.getRecordsList(typeSku);
        setDateToSkus();
        for (int i=0;i<skusList.length;i++){
            s = skusList[i];
            s.setTypeSku(typeSku);
            s.setYear(year);
            s.setMonth(month);
            s.setDay(day);
            s.setHour(hour);
            s.setMin(minute);
            s.setSec(seconds);
            skuReport.addRecord(s);
        }
    }

    protected boolean existSku() {
        boolean control = false;
        if (addAllControl == 0) {
            load_HashTables_sales();
            if (ht_skus.containsKey(TypeProduct)) {
                control = true;
            }
        }
        return control;

    }

    protected Sku selectSkuFromTable() {

        Sku sale_selected = null;
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();

        String descr = "";
        String volume = "";

        try {
            if (column == 0) {
                volume = (String) model.getValueAt(row, column);
                descr = (String) model.getValueAt(row, 1);
            } else {
                descr = (String) model.getValueAt(row, column);
                volume = (String) model.getValueAt(row, 0);
            }

            load_HashTables_sales();
            if (ht_skus.containsKey(descr)) {
                sale_selected = (Sku) ht_skus.get(descr);
            }

            return sale_selected;

        } catch (Exception e) {
            sale_selected = null;
            return sale_selected;
        }
    }

    protected void viewDetails(Sku s) {

        if (s != null) {
            currentSku = s;
            Det_Form = new Form("");
            Det_Form.setLayout(new BorderLayout());

            Label lfpc_code = new Label(LangXml.FPC_code + ":");
            fpc_code = new TextField(15);
            fpc_code.setEditable(false);

            Label lbrand = new Label(LangXml.Brand + ":");
            brand = new TextField(15);
            brand.setEditable(false);

            Label ldesc = new Label(LangXml.Desc + ":");
            description = new TextField(15);
            description.setEditable(false);

            Label lvol = new Label(LangXml.Volume + ":");
            vol = new TextField(15);
            vol.setConstraint(TextField.NUMERIC);
            vol.setInputMode("123");

            Label lamount = new Label(LangXml.TotAm + ":");
            amount = new TextField(15);
            amount.setEditable(false);

            val = s.getId();

            String temp = Integer.toString(s.getFpc_code());
            fpc_code.setText(temp);

            FPC = temp;
            brand.setText(s.getBrand());
            description.setText(s.getDescription());
            temp = Integer.toString(s.getVolume());
            vol.setText(temp);
            temp = Double.toString(s.getSelling_price() * s.getVolume());
            amount.setText(temp);

            formContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            formContainer.addComponent(lfpc_code);
            formContainer.addComponent(fpc_code);
            formContainer.addComponent(lbrand);
            formContainer.addComponent(brand);
            formContainer.addComponent(ldesc);
            formContainer.addComponent(description);
            formContainer.addComponent(lvol);
            formContainer.addComponent(vol);
            formContainer.addComponent(lamount);
            formContainer.addComponent(amount);

            Det_Form.addComponent(BorderLayout.CENTER, formContainer);
            Det_Form.addCommand(LangXml.Cmd_back10);
            Det_Form.addCommand(LangXml.Cmd_save11);
            Det_Form.setCommandListener((ActionListener) this);
            Det_Form.show();
        }
    }

    protected void viewDetails(Product s) {

        if (s != null) {
            rif_product_current = s;
            Det_Form = new Form("");
            Det_Form.setLayout(new BorderLayout());

            Label lfpc_code = new Label(LangXml.FPC_code + ":");
            fpc_code = new TextField(15);
            fpc_code.setEditable(false);

            Label lbrand = new Label(LangXml.Brand + ":");
            brand = new TextField(15);
            brand.setEditable(false);

            Label ldesc = new Label(LangXml.Desc + ":");
            description = new TextField(15);
            description.setEditable(false);

            Label lvol = new Label(LangXml.Volume + ":");
            vol = new TextField(15);
            vol.setConstraint(TextField.NUMERIC);
            vol.setInputMode("123");

            Label lamount = new Label(LangXml.TotAm + ":");
            amount = new TextField(15);
            amount.setEditable(false);

            val = s.getId();

            String temp = Integer.toString(s.getFpc_code());
            fpc_code.setText(temp);
            FPC = temp;
            brand.setText(s.getBrand());
            description.setText(s.getDescription());
            temp = "NA";
            vol.setText(temp);
            amount.setText(temp);

            formContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            formContainer.addComponent(lfpc_code);
            formContainer.addComponent(fpc_code);
            formContainer.addComponent(lbrand);
            formContainer.addComponent(brand);
            formContainer.addComponent(ldesc);
            formContainer.addComponent(description);
            formContainer.addComponent(lvol);
            formContainer.addComponent(vol);
            formContainer.addComponent(lamount);
            formContainer.addComponent(amount);
            Det_Form.addComponent(BorderLayout.CENTER, formContainer);
            Det_Form.addCommand(LangXml.Cmd_back10);
            Det_Form.setCommandListener((ActionListener) this);
            Det_Form.show();
        }
    }

    protected void updateVolume(Sku rif) {
        rif.setVolume(Integer.parseInt(vol.getText()));
        skus.updateRecord(rif, rif.getId());

    }

    protected void updateVolume(Sku rif, Object Volume) {

        Product prod = null;
        prod = (Product) ht_product.get(rif.getDescription());
        rif.setVolume(Integer.parseInt((String) Volume));
        rif.setSelling_price(prod.getSelling_price());
        skus.updateRecord(rif, rif.getId());

    }

    protected void deleteSku(Sku s) {
        if (s != null) {
            ht_skus.remove(s.getDescription());
            skus.deleteRecord(s.getId());
        }
    }

    protected void deleteSku(String description) {

        Sku s = null;
        Product p = null;
        load_HashTables_sales();
        if (ht_skus.containsKey(description)) {
            s = (Sku) ht_skus.get(description);
            deleteSku(s);
            ht_skus.remove(description);
        }
    }

    protected String retrieveAllSkusTable() {

        String listSales = "";
        try {
            skusList = skus.getRecordsList(typeSku);
            for (int i = 0; i < skusList.length; i++) {
                if (skusList[i].getVolume() > 0) {
                    listSales += skusList[i].getFpc_code() + ","
                            + skusList[i].getVolume() + ";";
//                          +skusList[i].getVolume()*skusList[i].getSelling_price()+";";
                }
            }
        } catch (Exception ex) {
        }
        return listSales;
    }

    protected void insertSKU() {

        formIns = new Form("");
        Label lnum_sales = new Label(LangXml.Dlg_insrt);
        num_sales = new TextField(30);
        num_sales.setConstraint(TextField.NUMERIC);
        num_sales.setInputMode("123");
        formIns.addComponent(lnum_sales);
        formIns.addComponent(num_sales);
        formIns.addCommand(LangXml.Cmd_save6);
        formIns.addCommand(LangXml.Cmd_back9);
        formIns.setCommandListener((ActionListener) this);
        formIns.show();

    }

    protected Product getProduct() {

        Product product = new Product();
        if (ht_product.containsKey(TypeProduct)) {
            product = (Product) ht_product.get(TypeProduct);
        }
        return product;
    }

    protected Product selectProductFromTable() {

        Product product_selected = null;
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();

        String descr = "";
        String volume = "";

        if (column == 0) {
            volume = (String) model.getValueAt(row, column);
            descr = (String) model.getValueAt(row, 1);
        } else {
            descr = (String) model.getValueAt(row, column);
            volume = (String) model.getValueAt(row, 0);
        }

        if (ht_product.containsKey(descr)) {
            product_selected = (Product) ht_product.get(descr);
        }

        return product_selected;
    }

    protected boolean divideSmsToSending(String sms) {

        boolean result = false;
        int i = 0;
        String content_sms = "";
        String header = "";
        try {
            if (sms.length() > 160) {
                Vector smsBody = TaskUtility.parserText(sms, ';');
                header = smsBody.firstElement().toString() + ";";
                do {
                    if ((content_sms + smsBody.elementAt(i).toString()).length() + 1 < 140) {
                        content_sms += smsBody.elementAt(i).toString() + ";";
                        i++;
                    } else {
                        if (sendSms(content_sms)) {
                            content_sms = header;
                            result = true;
                        } else {
                            result = false;
                        }
                    }
                } while (i < smsBody.size());
                if (content_sms.length() > 0) {
                    if (sendSms(content_sms)) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
            } else {
                if (sendSms(sms)) {
                    result = true;
                } else {
                    result = false;
                }
            }
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    protected boolean sendSms(String sms) {
        boolean send = false;
        try {
            if (!sms.endsWith(";")) {
                sms += ";";
            }
            TaskReceiveSms.manager.runTask(new TaskSendMessage(sms));
            send = true;
        } catch (Exception e) {
            send = false;
        }
        return send;
    }

    synchronized protected boolean sendThrowsGPRS(String numTel, String text) {
        boolean result = false;
        try {

            TaskSendThrowsGprs task = new TaskSendThrowsGprs(numTel, text);
            if (task.sendDataWithHttpGet()) {
                result = true;
                Dialog.show("Message", "Message Sent", "OK", null);
            } else {
                result = false;
            }

        } catch (Exception ex) {
            result = false;
        }

        return result;
    }

    protected boolean isNA(Table table, TableModel model) {

        boolean result = false;
        String isNA = "";
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        isNA = (String) model.getValueAt(row, column);
        if (isNA.equals("NA")) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public int valueConnectionSettings() {
        int result = -1;
        try {
            ActionSettings.nb = new ComunicationSettingRs("Number_Setting");
            ActionSettings.numberList = ActionSettings.nb.getNumber();
            if (ActionSettings.numberList.length <= 0) {
                result = -1;
            } else {
                if (ActionSettings.numberList[0].getWichProtocol().equals("")) {
                    result = -1;
                } else if (ActionSettings.numberList[0].getWichProtocol().equals("GPRS")) {
                    result = 0;
                } else if (ActionSettings.numberList[0].getWichProtocol().equals("AUTOMATIC")) {
                    result = 1;
                } else if (ActionSettings.numberList[0].getWichProtocol().equals("SMS")) {
                    result = 2;
                }
            }
            ActionSettings.nb.close();
        } catch (Exception ex) {
            Dialog.show(LangXml.Msg, "System Error", "OK", null);
            result = -2;
        }

        return result;
    }

    public boolean isMobile() {
        ActionSettings.nb = new ComunicationSettingRs("Number_Setting");
        if (ActionSettings.numberList[0].isMobile()) {
            return true;
        } else {
            return false;
        }
    }

    public String getYourNumberSetted() {

        String result = "";
        ActionSettings.nb = new ComunicationSettingRs("Number_Setting");
        ActionSettings.numberList = ActionSettings.nb.getNumber();

        if (ActionSettings.numberList.length <= 0) {
            result = "";
        } else {
            result = ActionSettings.numberList[0].getYourMobileNumber();
        }
        return result;
    }

    protected String retrieve_PMVersion() {
        String version = "PM;" + productList[0].getVersionCode() + ";";
        return version;
    }

    public boolean checkNumber() {

        boolean result = false;
        ActionSettings.numberList = ActionSettings.nb.getNumber();
        if (ActionSettings.numberList.length <= 0) {
            result = false;
        } else if (ActionSettings.numberList[0].isMobile()) {
            if (!ActionSettings.numberList[0].getMobileNumber().equals("")) {
                result = true;
            }
        } else {
            if (!ActionSettings.numberList[0].getGatewayNumber().equals("")) {
                result = true;
            }
        }

        return result;
    }

    protected void create_HashTables() {

        ht_product = new Hashtable();
        ht_skus = new Hashtable();
    }

    protected void load_HashTables_product() {
        productList = ActionProductMaster.db.getProductList();
        for (int i = 0; i < productList.length; i++) {
            ht_product.put(productList[i].getDescription(), productList[i]);
        }
    }

    private void load_HashTables_sales() {
        skusList = skus.getRecordsList(typeSku);
        for (int i = 0; i < skusList.length; i++) {
            ht_skus.put(skusList[i].getDescription(), skusList[i]);
        }
    }

    public void setDateToSkus() {

        Date timeStamp = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(timeStamp);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DATE);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        seconds = cal.get(Calendar.SECOND);
    }

    public void actionPerformed(ActionEvent ae) {
        Command d = ae.getCommand();
        if (d.getId() == 2) {
            if (isNA(table, model)) {
                viewDetails(selectProductFromTable());
            } else {
                viewDetails(selectSkuFromTable());
            }
        } else if (d.getId() == 3) {
            deleteSku(selectSkuFromTable());
            list_Skus();
        } else if (d.getId() == 4) {
            list_addAllSkus();
        } else if (d.getId() == 5) {
            listBrands();
        } else if (d.getId() == 6) {
            if (existSku()) {
                Dialog.show(LangXml.Msg, "The Product exist !!", "OK", null);
            } else {
                if (!saveSKU()) {
                    Dialog.show(LangXml.Msg, "Sale can't saved!!", "OK", null);
                }
            }
            if (addAllControl == 0) {
                list_Skus();
            } else {
                list_addAllSkus();
            }
        } else if (d.getId() == 7) {
            form.show();
        } else if (d.getId() == 8) {
            formBrands.show();
        } else if (d.getId() == 9) {
            if (addAllControl == 0) {
                formProducts.show();
            } else {
                formAllSkus.show();
            }
        } else if (d.getId() == 10) {
            if (addAllControl == 0) {
                form.show();
            } else {
                formAllSkus.show();
            }
        } else if (d.getId() == 11) {
            updateVolume(currentSku);
            if (addAllControl == 0) {
                list_Skus();
            } else {
                list_addAllSkus();
            }
        } else if (d.getId() == 12) {
            ht_skus.clear();
            skus.deleteAllRecords();
            ActionProductMaster.addAllControlSales = 0;
            list_Skus();
        }
    }
}