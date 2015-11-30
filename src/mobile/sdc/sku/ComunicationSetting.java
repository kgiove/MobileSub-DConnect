/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.sku;

/**
 *
 * @author ietservizi
 */
import java.io.*;
public class ComunicationSetting {
    
    private int id;
    private String yourMobileNumber="";
    private String mobileNumber="";
    private String gatewayNumber="";
    private boolean isMobile = false;
    private String wichProtocol="";
    private String day="";
    private int numDay= -1;
    private String frequency="";
    private String time="";
    private String status ="OFF";

    public ComunicationSetting(byte[] bytesArray) {
        unmarshall(bytesArray);
    }
    public ComunicationSetting(){}

    public void setId(int i){
        id = i;
    }
    public int getId(){
        return id;
    }
    public String getYourMobileNumber() {
        return yourMobileNumber;
    }
    public void setYourMobileNumber(String yourMobileNumber) {
        this.yourMobileNumber = yourMobileNumber;
    }
    public void setMobileNumber(String m){
        mobileNumber = m;
    }
    public String getMobileNumber(){
        return mobileNumber;
    }
    public void setGatewayNumber(String g){
        gatewayNumber = g;
    }
    public String getGatewayNumber(){
        return gatewayNumber;
    } 
   
    public void setIsMobile(boolean m){
        isMobile = m;
    }
    public boolean isMobile(){
        return isMobile;
    }

    public String getWichProtocol() {
        return wichProtocol;
    }

    public void setWichProtocol(String wichProtocol) {
        this.wichProtocol = wichProtocol;
    }
    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String startDate) {
        this.day = startDate;
    }

    public int getNumDay() {
        return numDay;
    }

    public void setNumDay(int numDay) {
        this.numDay = numDay;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] marshall() {
     // Inizializziamo gli oggetti che andremo ad utilizzare
     ByteArrayOutputStream baos = null;
     DataOutputStream dos = null;
     byte[] resultArray = null;
         try{
       
         baos = new ByteArrayOutputStream();        
         dos = new DataOutputStream(baos);  
         dos.writeUTF(getYourMobileNumber());
         dos.writeUTF(getMobileNumber());         
         dos.writeUTF(getGatewayNumber());
         dos.writeUTF(getWichProtocol());
         dos.writeUTF(getFrequency());
         dos.writeUTF(getDay());
         dos.writeInt(getNumDay());
         dos.writeUTF(getTime());
         dos.writeUTF(getStatus());
         dos.writeBoolean(isMobile());
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


    /* L'implementazione di questo metodo dovrà utilizzare l'array di byte per popolare la presente istanza dell'oggetto
 *@throw UnMarshallException Viene sollevata nel caso in cui ci fossero degli errori in fase di trasformazione
 */
 public void unmarshall(byte[] bytes){
     // Inizializziamo gli oggetti che andremo ad utilizzare
     ByteArrayInputStream bais = null;
     DataInputStream dis = null;
        try{
             
             bais = new ByteArrayInputStream(bytes);             
             dis = new DataInputStream(bais);
             setYourMobileNumber(dis.readUTF());
             setMobileNumber(dis.readUTF());
             setGatewayNumber(dis.readUTF());
             setWichProtocol(dis.readUTF());
             setFrequency(dis.readUTF());
             setDay(dis.readUTF());
             setNumDay(dis.readInt());
             setTime(dis.readUTF());
             setStatus(dis.readUTF());
             setIsMobile(dis.readBoolean());

     }catch(IOException ioe){
     // Nel caso di errore wrappiamo l'eccezione
     //throw new UnMarshallException(ioe.getMessage());

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
