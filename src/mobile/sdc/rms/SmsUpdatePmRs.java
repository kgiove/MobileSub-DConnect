/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.rms;


import java.util.Vector;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import mobile.sdc.sku.SmsUpdatePM;

/**
 *
 * @author kevin
 */
public class SmsUpdatePmRs extends Rs{
    
    public SmsUpdatePmRs(String filename){
        super(filename);
    }
    
    public synchronized SmsUpdatePM[] getMessageSmsList(){
        Vector buffer = new Vector();
        SmsUpdatePM[] result = null;
        RecordComparator comparator = null;
        try {
            int currentRecordId = 0;
            SmsUpdatePM currentProduct = null;
            comparator = new SmsComparator();
            RecordEnumeration re = recordStore.enumerateRecords(null, comparator, false);
            while(re.hasNextElement()) {
                currentRecordId = re.nextRecordId();
                currentProduct = new SmsUpdatePM(recordStore.getRecord(currentRecordId));
                currentProduct.setId(currentRecordId);
                buffer.addElement(currentProduct);
            }
        }catch(Exception ex){}
        
        int numPlayers = buffer.size();
        result = new SmsUpdatePM[numPlayers];
        for (int i=0; i < numPlayers; i++){
            result[i] = (SmsUpdatePM) buffer.elementAt(i);
        }
        
        return result;
    }   
    
}
class SmsComparator implements RecordComparator {
     public int compare(byte[] record1, byte[] record2) {
         SmsUpdatePM sms1 = null;
         SmsUpdatePM sms2 = null;
         try{
             sms1 = new SmsUpdatePM(record1);
             sms2 = new SmsUpdatePM(record2);
         }catch(Exception e){
             //Non è stato possibile deserializzare entrambi i bean.
         }
         if (sms1 == null){
             if (sms2 == null)
                 return RecordComparator.EQUIVALENT;
             else
                 return RecordComparator.FOLLOWS;
         }else if (sms2 == null)
             return RecordComparator.PRECEDES;
         int name1 = sms1.getCurrNumSmsPM();
         int name2 = sms2.getCurrNumSmsPM();
         if (name1 > name2) {
             return RecordComparator.FOLLOWS;
         } else if (name1 < name2) {
             return RecordComparator.PRECEDES;
         } else {
             return RecordComparator.EQUIVALENT;
         }
     }
}