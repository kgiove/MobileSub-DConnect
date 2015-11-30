/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.rms;

/**
 *
 * @author ietservizi
 */
import javax.microedition.rms.*;
import java.util.*;
import mobile.sdc.sku.MessageSms;

public class MessageRs extends Rs {

    public MessageRs(String fileName){
        super(fileName);
    }
    
    public synchronized MessageSms[] getMessageSmsList(){
        Vector buffer = new Vector();
        MessageSms[] result = null;
        try {
            int currentRecordId = 0;
            MessageSms currentProduct = null;
            RecordEnumeration re = recordStore.enumerateRecords(null, null, false);
            while(re.hasNextElement()) {
                currentRecordId = re.nextRecordId();
                currentProduct = new MessageSms(recordStore.getRecord(currentRecordId));
                currentProduct.setId(currentRecordId);
                buffer.addElement(currentProduct);
            }
        }catch(Exception ex){}
        
        int numPlayers = buffer.size();
        result = new MessageSms[numPlayers];
        for (int i=0; i < numPlayers; i++){
            result[i] = (MessageSms) buffer.elementAt(i);
        }
        return result;
    }
}