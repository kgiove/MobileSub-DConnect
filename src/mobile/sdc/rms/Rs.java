/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.rms;

import com.sun.lwuit.Dialog;
import java.util.Vector;
import javax.microedition.rms.*;
import mobile.sdc.sku.Sku;
import mobile.sdc.sku.MessageSms;
import mobile.sdc.sku.Product;
import mobile.sdc.sku.SmsUpdatePM;

/**
 *
 * @author kgiove
 */
public class Rs {
    public RecordStore recordStore = null;

    public Rs(String fileName){
        try{
            recordStore = RecordStore.openRecordStore(fileName, true);
        }catch(RecordStoreException rse){
            rse.printStackTrace();
        }
    }

    public void addRecord(Object obj){
        byte[] rec = null;
        try{
            if(obj instanceof Sku)
                rec = ((Sku) obj).marshall();
            else if(obj instanceof Product)
                rec = ((Product) obj).marshall();
            else if(obj instanceof MessageSms)
                rec = ((MessageSms) obj).marshall();
            else if(obj instanceof SmsUpdatePM)
                rec = ((SmsUpdatePM) obj).marshall();          
            
            recordStore.addRecord(rec, 0, rec.length);            
         
        }catch(Exception ex){
            db(ex.toString());
        }
    }

    public void updateRecord(Object obj,int id){

        byte[] rec = null;
        try{
            if((obj instanceof Sku))
                 rec = ((Sku) obj).marshall();
            if(obj instanceof Product)
                rec = ((Product) obj).marshall();
            else if(obj instanceof MessageSms)
                rec = ((MessageSms) obj).marshall();
            else if(obj instanceof SmsUpdatePM)
                rec = ((SmsUpdatePM) obj).marshall(); 
            
             recordStore.setRecord(id, rec, 0, rec.length);

        }catch(Exception ex){
            Dialog.show("Errore",ex.toString(), "OK",null);
            db(ex.toString());
        }
    }
    public synchronized Sku[] getRecordsList(int typeSku){
        Vector buffer = new Vector();
        Sku[] result = null;
        RecordFilter filter = null;
        try {
            int currentRecordId = 0;
            Sku currentEntity = null;
            filter = new SkuFilter(typeSku);
            RecordEnumeration re = recordStore.enumerateRecords(filter, null, false);
            //RecordEnumeration re = recordStore.enumerateRecords(null, null, false);
            while(re.hasNextElement()) {
                currentRecordId = re.nextRecordId();
                currentEntity = new Sku(recordStore.getRecord(currentRecordId));
                currentEntity.setId(currentRecordId);
                buffer.addElement(currentEntity);
            }
        }catch(Exception ex){
            System.out.println("Errore nella lettura");
        }

        int numPlayers = buffer.size();
        result = new Sku[numPlayers];
        for (int i=0; i < numPlayers; i++){
            result[i] = (Sku) buffer.elementAt(i);
        }
        return result;
    }



    public synchronized void deleteRecord(int recordId){
        try{
            recordStore.deleteRecord(recordId);
        }catch(Exception ex){}
    }

    public synchronized void deleteAllRecords(){
        try {
            RecordEnumeration re = recordStore.enumerateRecords(null, null, false);
            int currentRecordId = 0;
            while(re.hasNextElement()) {               
                currentRecordId = re.nextRecordId();
                deleteRecord(currentRecordId);
            }
        }catch(Exception ex){
            System.out.println("Errore nella cancellazione");
        }
    }

    public void close()throws RecordStoreNotOpenException,RecordStoreException{
        if (recordStore.getNumRecords() == 0) {           
            String fileName =
            recordStore.getName();
            recordStore.closeRecordStore();
            RecordStore.deleteRecordStore(fileName);
        } else {
            recordStore.closeRecordStore();
        }
    }

    public synchronized int size(){
        int size = 0;
        try {
            size = recordStore.getNumRecords();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
        }
        return size;
    }

    public void db(String str){
        System.err.println("Msg: " + str);
    }

    class SkuFilter implements RecordFilter {
        private int typeSku = -1;

        public SkuFilter(int typeSku){
            this.typeSku = typeSku;
        }
        public boolean matches(byte[] recordData) {
            Sku filteredSku = null;
            try{
                filteredSku = new Sku(recordData);

            }catch(Exception ex){}
            return (filteredSku!=null && filteredSku.getTypeSku()==typeSku
                    && filteredSku.getTypeSku()!=-1);
        }
    }
}
