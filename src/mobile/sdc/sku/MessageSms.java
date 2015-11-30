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

public class MessageSms {

    private int id;
    private int code;
    private String address;
    private String tipoData;
    private String bodyMessage;
    private boolean letto = false;

    public MessageSms(byte[] bytesArray) {
        unmarshall(bytesArray);
    }

    public MessageSms(){}
    public MessageSms(String msg){
        bodyMessage=msg;
    }

    public void setId(int i){
        id = i;
    }
    public int getId(){
        return id;
    }
    public void setCode(int cd){
        code = cd;
    }
    public int getCode(){
        return code;
    }
    public void setAddress(String adr){
        address = adr;
    }
    public String getAddress(){
        return address;
    }
    public void setTipoData(String tp){
        tipoData=tp;
    }
    public String getTipoData(){
        return tipoData;
    }
    public void setMessage(String msg){
        bodyMessage=msg;
    }
    public String getMessage(){
        return bodyMessage;
    }
    public boolean getLetto(){
        return letto;
    }
    public void setLetto(boolean l){
        letto=l;
    }

 public byte[] marshall() {

     ByteArrayOutputStream baos = null;
     DataOutputStream dos = null;
     byte[] resultArray = null;
         try{        
         baos = new ByteArrayOutputStream();     
         dos = new DataOutputStream(baos);
         dos.writeInt(getCode());
         dos.writeUTF(getAddress());
         dos.writeUTF(getTipoData());
         dos.writeUTF(getMessage());
         dos.writeBoolean(getLetto());
    
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
           
             dis = new DataInputStream(bais);
             setCode(dis.readInt());
             setAddress(dis.readUTF());
             setTipoData(dis.readUTF());
             setMessage(dis.readUTF());
             setLetto(dis.readBoolean());

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