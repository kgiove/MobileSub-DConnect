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

public class Product extends Sku {
   
    private double cost;
    private int targInv;
    private int SegmentNumber ;
    private int TotNumSmsPM;
    private int versionKey ;
    private int date_sms;

    public Product(byte[] bytesArray) {
        unmarshall(bytesArray);
    }

    public Product(){}   
    
    public void setCost(double c){
        cost = c;
    }
    public double getCost(){
        return cost;
    }

    public int getTargInv() {
        return targInv;
    }

    public void setTargInv(int targInv) {
        this.targInv = targInv;
    }
   
    public void setSegmentNumber(int num){
        SegmentNumber = num;
    }
    public int getSegmentNumber(){
        return SegmentNumber;
    }
    public void setTotNumSmsPM(int num){
        TotNumSmsPM = num;
    }

    public int getTotNumSmsPM(){
        return TotNumSmsPM;
    }

    public void setVersionCode(int key){
        versionKey=key;
    }
    public int getVersionCode(){
        return versionKey;
    }
    public void setDate(int d){
        date_sms=d;
    }
    public int getDate(){
        return date_sms;
    }

    public byte[] marshall() {
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        byte[] resultArray = null;
        try{           
            baos = new ByteArrayOutputStream();          
            dos = new DataOutputStream(baos);
          
            dos.writeInt(getFpc_code());
            dos.writeUTF(getBrand());
            dos.writeUTF(getDescription());
            dos.writeDouble(getCost());
            dos.writeDouble(getSelling_price());
            dos.writeInt(getTargInv());
            dos.writeInt(getSegmentNumber());
            dos.writeInt(getTotNumSmsPM());
            dos.writeInt(getVersionCode());
            dos.writeInt(getDate());          
            resultArray = baos.toByteArray();
        }catch(IOException ioe){           
            System.out.println(ioe.getMessage());
        }finally{          
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
        return resultArray;
    }// fine

   
 public void unmarshall(byte[] bytes){
     
     ByteArrayInputStream bais = null;
     DataInputStream dis = null;
     try{
       
         bais = new ByteArrayInputStream(bytes);
         // Da questo otteniamo un DataInputStream
         dis = new DataInputStream(bais);
         setFpc_code(dis.readInt());
         setBrand(dis.readUTF());
         setDescription(dis.readUTF());
         setCost(dis.readDouble());
         setSelling_price(dis.readDouble());
         setTargInv(dis.readInt());
         setSegmentNumber(dis.readInt());
         setTotNumSmsPM(dis.readInt());
         setVersionCode(dis.readInt());
         setDate(dis.readInt());
     }catch(IOException ioe){
         
         System.out.println(ioe.getMessage());
     }finally{     
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