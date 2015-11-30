/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.sku;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author kevin
 */
public class SmsUpdatePM {
    
    private int id;
    private int currNumSmsPM;
    private int totNumSmsPM;
    private int uniqueKey ;
    private int pmVersion; 
    private String contentSms;
    
    public SmsUpdatePM(byte[] bytesArray) {
        unmarshall(bytesArray);
    }

    public SmsUpdatePM(){}  
  

    public String getContentSms() {
        return contentSms;
    }

    public void setContentSms(String contentSms) {
        this.contentSms = contentSms;
    }

    public int getCurrNumSmsPM() {
        return currNumSmsPM;
    }

    public void setCurrNumSmsPM(int currNumSmsPM) {
        this.currNumSmsPM = currNumSmsPM;
    }   

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPmVersion() {
        return pmVersion;
    }

    public void setPmVersion(int pmVersion) {
        this.pmVersion = pmVersion;
    }

    public int getTotNumSmsPM() {
        return totNumSmsPM;
    }

    public void setTotNumSmsPM(int totNumSmsPM) {
        this.totNumSmsPM = totNumSmsPM;
    }

    public int getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(int uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
    
    public byte[] marshall() {
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        byte[] resultArray = null;
        try{
           
            baos = new ByteArrayOutputStream();       
            dos = new DataOutputStream(baos);
     
            dos.writeInt(getCurrNumSmsPM());
            dos.writeInt(getTotNumSmsPM());            
            dos.writeInt(getUniqueKey());            
            dos.writeInt(getPmVersion());           
            dos.writeUTF(getContentSms());           

            resultArray = baos.toByteArray();
        }catch(IOException ioe){
            // Nel caso di errore wrappiamo l'eccezione
            System.out.println(ioe.getMessage());
        }finally{
            // Ed in ogni caso chiudiamo le connessioni
            if (baos!=null){
                try{
                    baos.close();
                }catch(IOException ioe){}
            }// fine if
            if (dos!=null){
                try{
                    dos.close();
                }catch(IOException ioe){}
            }// fine if
        }// fine finally
        // Ritorniamo il risultato
        return resultArray;
    }// fine

 public void unmarshall(byte[] bytes){
     // Inizializziamo gli oggetti che andremo ad utilizzare
     ByteArrayInputStream bais = null;
     DataInputStream dis = null;
     try{
         // Creiamo un ByteArrayInputStream a partire dall'array di byte passato come parametro
         bais = new ByteArrayInputStream(bytes);
         // Da questo otteniamo un DataInputStream
         dis = new DataInputStream(bais);
         setCurrNumSmsPM(dis.readInt());
         setTotNumSmsPM(dis.readInt());         
         setUniqueKey(dis.readInt());        
         setPmVersion(dis.readInt());         
         setContentSms(dis.readUTF());

     }catch(IOException ioe){
         System.out.println(ioe.getMessage());
     }finally{
         // Ed in ogni caso chiudiamo le connessioni
         if (bais!=null){
             try{
                 bais.close();
             }catch(IOException ioe){}
         }// fine if
         if (dis!=null){
             try{
                 dis.close();
             }catch(IOException ioe){}
         }// fine if
     }// fine finally
 }// fine

}
