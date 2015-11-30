/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.actions;

import mobile.sdc.settings.LangXml;
import mobile.sdc.sku.Product;
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.table.DefaultTableModel;
import com.sun.lwuit.table.Table;
import com.sun.lwuit.table.TableModel;
import java.util.Date;
import mobile.sdc.sku.Sku;
import mobile.sdc.menu.ReportMenu;
import mobile.sdc.rms.Rs;
import mobile.sdc.visitForm.Visit;

/**
 *
 * @author ietservizi
 */
public class ActionReport implements ActionListener {
    
    private final static int TYPE_SALE = 0;
    private final static int TYPE_ORDER = 1;
    private final static int TYPE_INVENTORY = 2;
    protected Form dateForm,Form_listDateSale,formSms;
    protected List choose;
    java.util.Calendar cal = null;
    String date,time;
    int year,month,day,hour,min,sec;
    Sku[] skuReportList = null;
    TableModel model;
    Table table;
    protected String BrandSelected;
    protected Form form;
    protected Form formBrands;
    protected Product [] productList = null;
    protected List chooseBrand;
    protected Container formContainer;
    protected Label lb_Sale,lb_Order,lb_Inv,lb_Space,lb_free_msg,lb_tot;

    private void openRecordReport(){
         Visit.skuReport =  new Rs("Record_Reports");
    }   
 
    public String getTodayAsString(){

        cal = java.util.Calendar.getInstance();
        cal.setTime(new Date());
        int mese = cal.get(java.util.Calendar.MONTH);
        cal.set(java.util.Calendar.MONTH, mese+1);

        String today = cal.get(java.util.Calendar.YEAR)+"/"+
                cal.get(java.util.Calendar.MONTH)+"/"+
                +cal.get(java.util.Calendar.DAY_OF_MONTH)+"";   

        return today;

    }

    public void listSelectedReportSkus(int typeSku){
        int i;
        String titleReport = "";
        switch(typeSku){
            case 0: titleReport = LangXml.TabSale; break;
            case 1: titleReport = LangXml.TabOrd; break;
            case 2: titleReport = LangXml.TabInv; break;
        }        
        
        Form_listDateSale = new Form(titleReport);
        Form_listDateSale.setLayout(new BorderLayout());
        try {
            openRecordReport();
            
            skuReportList = Visit.skuReport.getRecordsList(typeSku);            
            String [] dates = new String[skuReportList.length];
            for(i=0;i<dates.length;i++){
                dates[i]="";
            }
            for(i=0; i < skuReportList.length; i++){
                if(skuReportList[i].getTypeSku()== typeSku){
                    year = skuReportList[i].getYear();
                    month = skuReportList[i].getMonth();
                    day = skuReportList[i].getDay();
                    hour = skuReportList[i].getHour();
                    min = skuReportList[i].getMin();
                    sec = skuReportList[i].getSec();
                    String dataSales = year+"/"+month+"/"+day+"";
                    String today = getTodayAsString();
//                    System.out.println("DataRecord:"+dataSales);
//                    System.out.println("DataToday:"+today);
                    if(dataSales.equals(today)){
                        dates[i]=getDateFormat(year,month,day,hour,min,sec);
                    }else{
                        Visit.skuReport.deleteRecord(skuReportList[i].getId());
                    }
                }
            }
            showListDates(dates,typeSku);
        } catch(Exception ex) {
            System.out.println("Messaggio: "+ex.getMessage());
        }
    }

    

    private void showListDates(String [] dates, final int choice){

       String temp;
         for( int i=0;i<dates.length;i++){
              temp = dates[i];
              for(int j=i+1;j<dates.length;j++)
                {
                  if(temp.equals(dates[j])){
                      dates[j]="";
                  }
                }
            }
        int SizeDate = 0;
          for(int i =0;i<dates.length;i++){
               if(!dates[i].equals("")){
                     SizeDate++;

                 }
               }

        int j = 0;
        String [] finalDate = new String[SizeDate];
         for(int i =0;i<dates.length;i++){
            if(!dates[i].equals("")){
               finalDate[j]=dates[i];
               j++;
                 }
               }

        final DefaultListModel myListD = new DefaultListModel(finalDate);
        choose = new List(myListD);
        ActionListener al = new ActionListener(){
        public void actionPerformed(ActionEvent ae) {
           if(choice == 0)
               viewDetail_Sales(TYPE_SALE, myListD.getItemAt(myListD.getSelectedIndex())+"");
           else if(choice == 1)
               viewDetail_Sales(TYPE_ORDER, myListD.getItemAt(myListD.getSelectedIndex())+"");
           else if(choice == 2)
               viewDetail_Sales(TYPE_INVENTORY, myListD.getItemAt(myListD.getSelectedIndex())+"");
               }
             };

         choose.addActionListener(al);
         Form_listDateSale.addComponent(BorderLayout.CENTER,choose);
         Form_listDateSale.addCommand(LangXml.Cmd_back1);
         Form_listDateSale.setCommandListener((ActionListener) this);
         Form_listDateSale.show();
    }
    protected void viewDetail_Sales(int typeReport,String dateSale){
        String desc ;
        double tot = 0,finalTot = 0;
        String titleReport = "";
        
        switch(typeReport){
            case 0: titleReport = LangXml.TabSale; break;
            case 1: titleReport = LangXml.TabOrd; break;
            case 2: titleReport = LangXml.TabInv; break;
        }    
        
         
        try {
            String [][] tableData = new String[skuReportList.length][3];
            for (int i=0; i < skuReportList.length; i++){
                year =  skuReportList[i].getYear();
                month = skuReportList[i].getMonth();
                day = skuReportList[i].getDay();
                hour = skuReportList[i].getHour();
                min = skuReportList[i].getMin();
                sec = skuReportList[i].getSec();
                String data2 = getDateFormat(year,month,day,hour,min,sec);
                if(dateSale.equals(data2)){
                    tot = skuReportList[i].getVolume()*skuReportList[i].getSelling_price();
                    finalTot = finalTot+tot;
                    desc = skuReportList[i].getDescription();
                    tableData[i][0]= desc;
                    tableData[i][1]= skuReportList[i].getVolume()+"";
                    tableData[i][2]= tot+"";
                }
            }
            showTable(titleReport,showDetails(tableData),finalTot);
        } catch(Exception ex) {
            ex.getMessage();
        }
    }

    protected String[][] showDetails(String [][] tableData){

        int size=0;
            for(int i=0;i<tableData.length;i++)
                if(tableData[i][0]!=null){
                   size++;
                }
        String [][] processed_tableData = new String[size][3];
        for(int j=0;j<processed_tableData.length;j++){
            for(int i=0;i<tableData.length;i++){
                if(tableData[i][0]!=null){

                    processed_tableData[j][0]=tableData[i][0];
                    processed_tableData[j][1]=tableData[i][1];
                    processed_tableData[j][2]=tableData[i][2];
                    tableData[i][0] = null;
                    break;
                }
            }
         }
        return processed_tableData;

    }
    protected void showTable(String title,String [][] tableData,double tot){

        String[] tableHeaders =  new String[]{LangXml.Desc.substring(0, 4)+".",
                                              LangXml.Volume.substring(0, 3)+".",
                                              LangXml.Tot};
        DefaultTableModel model = new DefaultTableModel(tableHeaders,
                    tableData, false);
        table = new Table(model);
        form = new Form(title);
        form.setLayout(new BorderLayout());
        form.addComponent(BorderLayout.CENTER,table);
        Label testLabel = new Label (LangXml.TotAm+" : "+tot+"");
        form.addComponent(BorderLayout.NORTH,testLabel);
        form.addCommand(LangXml.Cmd_back2);
        form.setCommandListener((ActionListener) this);
        form.show();
    }
    protected String getDateFormat(int year,int month,int day,int hour,int min,int sec){
        
        String result_date = year+"/"+(month<10? "0": "")+ 
                month+"/"+(day<10? "0": "") + day+" ";
        result_date += ((hour<10? "0": "")+hour + ":" + (min<10? "0": "") 
                + min + ":" +  (sec<10? "0": "")+sec);
        return result_date;
    }

    public void actionPerformed(ActionEvent ae) {
        Command c = ae.getCommand();
        switch(c.getId()){
            case 1: ReportMenu.formReportMenu.show();   break;
            case 2: Form_listDateSale.show();           break;
        }
    }

}
