/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.actions;

import mobile.sdc.settings.LangXml;
import mobile.sdc.sku.Product;
import mobile.sdc.rms.ProductRs;
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.table.DefaultTableModel;
import com.sun.lwuit.table.Table;
import com.sun.lwuit.table.TableModel;
import java.util.Vector;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import mobile.sdc.menu.StartMenu;
import mobile.sdc.task.TaskUtility;


public final class ActionProductMaster implements ActionListener{

    private Form form,Det_Form;
    private String [][] prova;
    static Product[] productList = null;
    boolean[] selectedProduct = null;
    static public ProductRs db = null;
    private TextField fpc_code;
    private TextField brand;
    private TextField description;
    private TextField cost;
    private TextField sellingPrice;
    private TextField cod_inv;
    private Label lfpc_code;
    private Container formContainer;
    private Table table = null;
    private TableModel model;
    private String curr_VersionPM="";
    static public int addAllControlSales = 0;
    static public int addAllControlOrders = 0;
    static public int addAllControlInv = 0;
    int sizePM;
    
    public void start(){
        try {
            db = new ProductRs("RecordsProduct");            
            listProductMaster();
        }catch(Exception e) {}
    }
    
    public static Vector listPMbyHashtable(){
        
        Vector listPM = new Vector();        
        productList = db.getProductList();
            listPM = new Vector();
            for(int j=0;j<productList.length;j++){
                listPM.addElement(productList[j]);
            }

        return listPM;
    }
    
    private void listProductMaster(){      
       
    try {
        
         Product current = null;
         productList = db.getProductList();
         if(productList!= null && productList.length>0){
            prova = new String[productList.length][2];
            for(int i=0;i<productList.length;i++){
                current = productList[i];                
                prova[i][0]=current.getFpc_code()+"";
                prova[i][1]= current.getDescription();             
         }    
         curr_VersionPM = current.getVersionCode()+"";
        } else {
            prova = new String[0][2];}
         
        TaskUtility.sort(prova);
       
        model = new DefaultTableModel(
                new String[] {LangXml.FPC_code, LangXml.Desc},
                prova) {
                public boolean isCellEditable(int row, int col) {
                    return col != 0 && col != 1;
                }
            };
        
        showTable();

     } catch(Exception ex) {
         Dialog.show("Message", ex.getMessage(), "OK",null);
     }

    }
    private void viewSingleSaleTable(){
        
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        String desc = "";
        String fpc = "";
        int id;
        
        try{
            if(column == 0){
                fpc = (String)model.getValueAt(row,column);
                desc = (String)model.getValueAt(row, 1);
            }else{
                desc = (String)model.getValueAt(row,column);
                fpc =   (String)model.getValueAt(row, 0); 
            }
            id = Integer.parseInt(fpc);
            productList = db.getProductList();
            for(int i=0;i<productList.length;i++){
                if(productList[i].getFpc_code() == id){
                    updateData(productList[i]);
                }
            }
        }catch(Exception e){}
    }
    private void updateData(Product p){

        Det_Form = new Form(LangXml.ProductMaster_name);
        Det_Form.setLayout(new BorderLayout());

        lfpc_code = new Label(LangXml.FPC_code+":");
        fpc_code = new TextField(15);
        fpc_code.setConstraint(TextField.NUMERIC);
        fpc_code.setInputMode("123");

        Label lbrand = new Label(LangXml.Brand+":");
        brand = new TextField(15);

        Label ldesc = new Label(LangXml.Desc+":");
        description = new TextField(15);

        Label lcost = new Label(LangXml.Cost+":");
        cost = new TextField(15);
        cost.setConstraint(TextField.NUMERIC);
        cost.setInputMode("123");

        Label lsell = new Label(LangXml.Sell_price+":");
        sellingPrice = new TextField(15);
        sellingPrice.setConstraint(TextField.NUMERIC);
        sellingPrice.setInputMode("123");

        Label lcod = new Label(LangXml.Target_Inv+":");
        cod_inv = new TextField(15);
        cod_inv.setConstraint(TextField.NUMERIC);
        cod_inv.setInputMode("123");     

        String temp = Integer.toString(p.getFpc_code());
        fpc_code.setText(temp);    

        brand.setText(p.getBrand());

        description.setText(p.getDescription());

        temp = Double.toString(p.getCost());
        cost.setText(temp);

        temp = Double.toString(p.getSelling_price());
        sellingPrice.setText(temp);

        temp = Integer.toString(p.getTargInv());
        cod_inv.setText(temp);

        formContainer=new Container(new BoxLayout(BoxLayout.Y_AXIS));
       
        formContainer.addComponent(lfpc_code);
        formContainer.addComponent(fpc_code);

        formContainer.addComponent(lbrand);
        formContainer.addComponent(brand);

        formContainer.addComponent(ldesc);
        formContainer.addComponent(description);

        formContainer.addComponent(lcost);
        formContainer.addComponent(cost);

        formContainer.addComponent(lsell);
        formContainer.addComponent(sellingPrice);

        formContainer.addComponent(lcod);
        formContainer.addComponent(cod_inv);
        
        Det_Form.addComponent(BorderLayout.CENTER,formContainer);        
        Det_Form.addCommand(LangXml.Cmd_back3);
        Det_Form.setCommandListener(this);
        Det_Form.show();
    }
              
    private String getTitleForm(){

        String title ="";
        if(model.getRowCount()>0){            
            title = "PM Version: "+curr_VersionPM;
        }else{           
            title="PM Version: NA";
        }        
        return title;
    }
    private void showTable(){
        
        table = new Table(model);
        table.getComponentAt(0).setEnabled(false);
        table.getComponentAt(1).setEnabled(false);
        table.getComponentAt(0).setPreferredW(105);
        table.setScrollable(false);
        table.setIncludeHeader(true);
        form = new Form(getTitleForm());
        form.setLayout(new BorderLayout());
        form.addComponent(BorderLayout.CENTER,table);
        form.addCommand(LangXml.Cmd_back1);
        form.addCommand(LangXml.Cmd_details2);
        form.setCommandListener(this);
        form.showBack();

}    
    public void actionPerformed(ActionEvent ae) {
        Command d = ae.getCommand();
        if(d.getId()== 1){
            try {
                db.close();
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            }
            StartMenu.form_Main.show();
        } else if(d.getId() == 2){
            viewSingleSaleTable();
        }else if (d.getId() == 3){
            showTable();
        }
    }
}