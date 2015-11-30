/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.actions;

import mobile.sdc.sku.MessageSms;
import mobile.sdc.rms.MessageRs;
import java.util.Vector;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import mobile.sdc.task.TaskUtility;
import mobile.sdc.task.TaskReceiveSms;

/**
 *
 * @author ietservizi
 */
public class ActionDecodeSmsSkus {
    
    MessageSms [] messageSku;
    Vector inbox = null;


    public Vector decodeMessage(int type) throws RecordStoreNotOpenException, RecordStoreException{
        
        TaskReceiveSms.quequeMessage = new MessageRs("quequeMessage");
        messageSku = TaskReceiveSms.quequeMessage.getMessageSmsList();
        inbox = new Vector();
        for(int i=0;i<messageSku.length;i++){
            if(messageSku[i].getCode()==type)
                inbox.addElement(messageSku[i].getTipoData());
        }
        return inbox;

    }

    public MessageSms getMessage(String TipoData){

        int id_sms = 0;
        MessageSms message = null;

        TaskReceiveSms.quequeMessage = new MessageRs("quequeMessage");
        messageSku = TaskReceiveSms.quequeMessage.getMessageSmsList();
        for(int i=0;i<messageSku.length;i++){
            if(messageSku[i].getTipoData().equals(TipoData)){
                id_sms = messageSku[i].getId();
                messageSku[i].setLetto(true);
                TaskReceiveSms.quequeMessage.updateRecord(messageSku[i], id_sms);
                message=messageSku[i];

                break;
            }
        }

        return message;

    }

    public String[][] getTableBody(String TipoData){

        Vector temp_pv = new Vector();
        Vector temp_v = new Vector();

        getMessage(TipoData);
        String [][] tableSkus = new String[temp_pv.size()-1][3];

        for(int j=0;j<temp_pv.size()-1;j++){

        temp_v = TaskUtility.parserText(temp_pv.elementAt(j).toString()+",",',');
        tableSkus[j][0]= temp_v.elementAt(0).toString();
        tableSkus[j][1]= temp_v.elementAt(1).toString();
        tableSkus[j][2]= temp_v.elementAt(2).toString();
        }
        return tableSkus;
    }
}