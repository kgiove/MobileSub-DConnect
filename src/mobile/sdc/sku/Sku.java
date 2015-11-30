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
 * @author kgiove
 */
public class Sku {

    private int id;
    private int typeSku = -1;
    private int fpc_code;
    private String description;
    private int volume;
    private double selling_price;
    private String brand;
    private int year;
    private int month;
    private int day ;
    private int hour;
    private int min;
    private int sec;

    public Sku(){}

    public Sku(byte[] bytesArray) {
        unmarshall(bytesArray);
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFpc_code() {
        return fpc_code;
    }

    public void setFpc_code(int fpc_code) {
        this.fpc_code = fpc_code;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getTypeSku() {
        return typeSku;
    }

    public void setTypeSku(int typeSku) {
        this.typeSku = typeSku;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public double getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(double selling_price) {
        this.selling_price = selling_price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public byte[] marshall() {
        // Inizializziamo gli oggetti che andremo ad utilizzare
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        byte[] resultArray = null;
        try{
            // Creiamo un ByteArrayOutputStream per inserire i vari elementi
            baos = new ByteArrayOutputStream();
            // Da questo otteniamo un DataOutputStream
            dos = new DataOutputStream(baos);
            // Il primo valore letto si riferisce alla String del nome
            //dos.writeInt(getId());
            dos.writeInt(getTypeSku());
            dos.writeInt(getFpc_code());
            dos.writeUTF(getDescription());
            dos.writeInt(getVolume());
            dos.writeDouble(getSelling_price());
            dos.writeInt(getYear());
            dos.writeInt(getMonth());
            dos.writeInt(getDay());
            dos.writeInt(getHour());
            dos.writeInt(getMin());
            dos.writeInt(getSec());
            dos.writeUTF(getBrand());
            // Otteniamo quindi l'array di byte
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
            // Creiamo un ByteArrayInputStream a partire dall'array di byte passato come parametro
            bais = new ByteArrayInputStream(bytes);
            // Da questo otteniamo un DataInputStream
            dis = new DataInputStream(bais);
            setTypeSku(dis.readInt());
            setFpc_code(dis.readInt());
            setDescription(dis.readUTF());
            setVolume(dis.readInt());
            setSelling_price(dis.readDouble());
            setYear(dis.readInt());
            setMonth(dis.readInt());
            setDay(dis.readInt());
            setHour(dis.readInt());
            setMin(dis.readInt());
            setSec(dis.readInt());
            setBrand(dis.readUTF());

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
