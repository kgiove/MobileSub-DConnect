/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.task;

import com.sun.lwuit.Dialog;
import java.io.DataInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import mobile.sdc.sku.Product;
import mobile.sdc.actions.ActionProductMaster;
import mobile.sdc.rms.ProductRs;

/**
 *
 * @author kevin
 */
 public class TaskDownload extends TimerTask implements Runnable  {
     
//     final String URL = "http://pg.cefriel.it:8090/MobileSubDConnect/service/master";
     String allinamentoVersion="http://pg.cefriel.it:8090/MobileSubDConnect/service/message/";
     final String URL = "http://www.subdconnect.com:8080/MobileSubDConnect/service/master";
     String LocID = null;
     String getStr = null;
     Calendar cal = null;
     Date now = null;
     Vector vectTime = null;
     int hourNow,minNow,hourTm,minTm,dayNow,dayTm;
     String mrTm,frequency;
     String versionFromServer="";
     private HttpConnection connection;
     private DataInputStream input;   
     private Product[] productList;    
     
     public TaskDownload(){
         frequency = "NW";
     }
     
     public TaskDownload(String ftime){

         frequency="ED";
         vectTime = TaskUtility.parserText(ftime.trim()+":",':');
         hourTm = Integer.parseInt(vectTime.elementAt(0).toString());
         minTm = Integer.parseInt(vectTime.elementAt(1).toString());
         mrTm = vectTime.elementAt(2).toString();
         if(mrTm.equals("PM"))
             hourTm = hourTm+12;
     }
     
     public TaskDownload(String frequency,int day,String ftime){
         
         dayTm = day+1;
         this.frequency = frequency;
         vectTime = TaskUtility.parserText(ftime.trim()+":",':');
         hourTm = Integer.parseInt(vectTime.elementAt(0).toString());
         minTm = Integer.parseInt(vectTime.elementAt(1).toString());
         mrTm = vectTime.elementAt(2).toString();
         if(mrTm.equals("PM")){
             if(hourTm < 12)
                 hourTm = hourTm+12;
         }
     }

     public  final void run(){

         now = new Date();
         cal = Calendar.getInstance();
         cal.setTime(now);
         hourNow=cal.get(Calendar.HOUR_OF_DAY);
         minNow = cal.get(Calendar.MINUTE);
         dayNow = cal.get(Calendar.DAY_OF_WEEK);
        
         if(frequency.equals("ED")){
//              System.out.println("Dentro il metodo run del timertask: "
//                      +hourTm+ ","+minTm);
             if(hourTm==hourNow && minTm==minNow){                 
                 downloadPM();
             }
         }else if(frequency.equals("EW")){
             if(dayTm==dayNow && hourTm==hourNow && minTm==minNow)
                 downloadPM();        
         }else if(frequency.equals("NW"))
             downloadPM();
     }    
     
     private void downloadPM(){
         try{

             connection = (HttpConnection) Connector.open(URL);
             connection.setRequestMethod(HttpConnection.GET);           
              
             int responseCode = connection.getResponseCode();             
             if(responseCode == HttpConnection.HTTP_OK){
                  StringBuffer sb = new StringBuffer();                    
                    input = connection.openDataInputStream();
                    int chr;
                    while ((chr = input.read()) != -1)
                      sb.append((char) chr);   
                    close();
//                    System.out.println("" + sb.toString());
                    savePM_LocalDb(sb.toString());         
             }else{
                 Dialog.show("Message", "Network not avaible", "OK",null);
             }
         }catch(Exception ex){
             Dialog.show("Message", "Network not avaible", "OK",null);
         }
     }
     
     private boolean updatePmVersionServerUser(int VersionPM){
         boolean result=false;
         try{   
             String tel = TaskUtility.getCurrentNumber(); 
             if(!tel.equals("")){    
                 allinamentoVersion+=tel+"?"+TaskUtility.urlEncode("text=PM;"+VersionPM+";");
                 connection = (HttpConnection) Connector.open(allinamentoVersion);                
                 connection.setRequestMethod(HttpConnection.GET);
                 int responseCode = connection.getResponseCode();
                 if(responseCode == HttpConnection.HTTP_OK){                     
                     result=true;
                 }else{                    
                     result=false;
                 }
                 close();
             }
         }catch (Exception ex){result =false;}
         return result;
         
     }
     
     private void close() {
         try {
             input.close();
         } catch (Exception e) {
         } finally {
             input = null;
         }         
         try {
             connection.close();
         } catch (Exception e) {
         } finally {
             connection = null;
         }
     }
     
     private void savePM_LocalDb(String message){
      try{
          ActionProductMaster.db = new ProductRs("RecordsProduct");
          productList = null;
          productList = ActionProductMaster.db.getProductList();
          int currentVersion=-1;
          if(productList.length>0)
              currentVersion=productList[0].getVersionCode();         
          
          Product p = new Product(); 
          int start_due_punti = message.indexOf(":");         
          versionFromServer = message.substring(0, start_due_punti);
          if(currentVersion!=Integer.parseInt(versionFromServer)){
              ActionProductMaster.db.deleteAllRecords();
              String[][] VectorProductMaster = TaskUtility.decodePM(message.substring(start_due_punti+1));
              p.setVersionCode(Integer.parseInt(versionFromServer));
              p.setDate(TaskUtility.getDateAsNumber(new Date())); 
              for (int i=0;i<VectorProductMaster.length;i++){
                  p.setVersionCode(Integer.parseInt(versionFromServer));
                  p.setFpc_code(Integer.parseInt(VectorProductMaster[i][0].trim()));
                  p.setDescription(VectorProductMaster[i][1].trim());
                  p.setBrand(VectorProductMaster[i][2].trim());
                  p.setCost(Double.parseDouble(VectorProductMaster[i][3]));
                  p.setSelling_price(Double.parseDouble(VectorProductMaster[i][4]));
                  p.setTargInv(Integer.parseInt(VectorProductMaster[i][5]));
                  ActionProductMaster.db.addRecord(p);
              }
              
              ActionProductMaster.db.close();
              updatePmVersionServerUser(Integer.parseInt(versionFromServer)); 
              Dialog.show("Message", "Product Master Successfully Updated", "OK",null);
             
          }
      
          }catch(Exception ex){
              Dialog.show("Message", "Download failed: "+ex.getMessage(), "OK",null);
          }
     }
 }