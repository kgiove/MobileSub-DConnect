/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.task;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import mobile.sdc.actions.ActionProductMaster;
import mobile.sdc.actions.ActionSettings;
import mobile.sdc.sku.Product;
import mobile.sdc.rms.ComunicationSettingRs;
import mobile.sdc.rms.ProductRs;

/**
 *
 * @author ietservizi
 */
public  class TaskUtility {

   public static Vector parserText(String text, char sep) {
        String line = "";
        Vector vector = new Vector();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == sep) {
                vector.addElement(line);
                line = "";
            } else {
                line += text.charAt(i);
            }
        }
        return vector;
    }

   public static String getDate(Date date){

        String dateString = "";
        Calendar cal = null;
        cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);
        dateString = year+"/"+(month<10? "0": "")+ month+"/"+(day<10? "0": "") + day+" ";
        dateString +=((hour<10? "0": "")+hour + ":" + (minute<10? "0": "") + minute + ":" +  (seconds<10? "0": "")+seconds);;

        return dateString;
    }
   
   public static int getDateAsNumber(Date date){

        String dateString = "";
        int dateAsNumber = 0;
        Calendar cal = null;
        cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);
        dateString =month+""+(day<10? "0": "") + day+"";
        dateString +=(hour<10? "0": "")+hour+""+(minute<10? "0": "") + minute+"";
        dateString += (seconds<10? "0": "")+seconds;
        dateAsNumber = Integer.parseInt(dateString);
        
        return dateAsNumber;
    }  
   
   static public String urlEncode(String sUrl) {
       int i = 0;
       String urlOK = "";
       while (i < sUrl.length()) {
           if (sUrl.charAt(i) == '<') {
               urlOK = urlOK + "%3C";
           } else if (sUrl.charAt(i) == '/') {
               urlOK = urlOK + "%2F";
           } else if (sUrl.charAt(i) == '>') {
               urlOK = urlOK + "%3E"; 
           } else if (sUrl.charAt(i) == ' ') {
               urlOK = urlOK + "%20";
           } else if (sUrl.charAt(i) == ':') {
               urlOK = urlOK + "%3A";
           } else if (sUrl.charAt(i) == '-') {
               urlOK = urlOK + "%2D"; 
           } else {
               urlOK = urlOK + sUrl.charAt(i);
           }
           i++;
       }
       return (urlOK);
   }
   
   static public void sort(String Vector[][]){
       int j;
       boolean flag = true;
       Object temp;
       while(flag){
            flag=false;
            for(j=0;j<Vector.length-1;j++){
                if(Vector[j][1].compareTo
                        (Vector[j+1][1])>0)
                {
                    temp=Vector[j];
                    Vector[j] = Vector[j+1];
                    Vector[j+1]=(String[]) temp;
                    flag=true;}}
        }

    }
   static public void sort(String Vector[]){
        int j;
        boolean flag = true;
        Object temp;
        while(flag){
            flag=false;
            for(j=0;j<Vector.length-1;j++){
                if(Vector[j].compareTo
                        (Vector[j+1])>0)
                {
                    temp=Vector[j];
                    Vector[j] = Vector[j+1];
                    Vector[j+1]= (String) temp;
                    flag=true;}}
        }

    }

   public static String[][] decodePM(String bodyMessage){
       Vector v = new Vector();
       v = parserText(bodyMessage,';');
       int lengh = v.size()/6;
       String[][] VectorProductMaster = new String[lengh][6];
       int j = 0;
       int k = -1;
       for(int i=0;i<v.size();i++){
           if(k<5)
               k++;
           else{
               k=0;
               j++;
           }
           VectorProductMaster[j][k] = v.elementAt(i).toString().toUpperCase();
       }
       return VectorProductMaster;
   }
   public static String getCurrentPmVersion(){
       
       String currPMVersion="Version ND";
       try{
        ActionProductMaster.db = new ProductRs("RecordsProduct");
        Product[] productList = ActionProductMaster.db.getProductList();
        ActionProductMaster.db.close();
        if(productList.length>0)
            currPMVersion="Version "+productList[0].getVersionCode()+"";     
       }catch(Exception ex){
           currPMVersion="Version ND";
       }
        
        return currPMVersion;
   }
   
    public static String getCurrentNumber(){
        String yourNumuber="";
        try{
            ActionSettings.nb = new ComunicationSettingRs("Number_Setting");
            ActionSettings.numberList = ActionSettings.nb.getNumber();
            ActionSettings.nb.close();
            if(ActionSettings.numberList.length>0){
                yourNumuber = ActionSettings.numberList[0].getYourMobileNumber();                
            }
        }catch(Exception ex){
            yourNumuber="";
        }
        return yourNumuber;
    }
   
   
   public static String[][] decodeSmsPM(String bodyMessage){
          
          Vector v = new Vector();
          v = TaskUtility.parserText(bodyMessage,';');
          int lengh = v.size()/6;
          String[][] VectorProductMaster = new String[lengh][6];
          int j = 0;
          int k = -1;
          for(int i=0;i<v.size();i++){
              if(k<5)
                  k++;
              else{
                  k=0;
                  j++;
              }
              VectorProductMaster[j][k] = v.elementAt(i).toString().toUpperCase();
          }
          return VectorProductMaster;
    }
    public static String replaceAll(String text, String searchString, String replacementString) {
       StringBuffer sBuffer = new StringBuffer();  
       int pos = 0;
       while ((pos = text.indexOf(searchString)) != -1) {
           sBuffer.append(text.substring(0, pos) + replacementString); 
           text = text.substring(pos + searchString.length());        
       } 
       sBuffer.append(text);
       return sBuffer.toString();
   }
}
