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
import mobile.sdc.sku.ComunicationSetting;

public class ComunicationSettingRs extends Rs {

    public ComunicationSettingRs(String fileName) {
        super(fileName);
    }

    public void addNumber(ComunicationSetting Record) {
        {
            byte[] rec = Record.marshall();
            int recordId = 1;
            try {
                if (recordStore.getNumRecords() == 0) {
                    recordStore.addRecord(rec, 0, rec.length);

                } else {
                    recordStore.setRecord(recordId, rec, 0, rec.length);

                }
            } catch (Exception e) {
                db(e.toString());
            }
        }
    }

    public synchronized ComunicationSetting[] getNumber() {
        Vector buffer = new Vector();
        ComunicationSetting[] result = null;
        try {
            int currentRecordId = 0;
            ComunicationSetting currentNumber = null;
            RecordEnumeration re = recordStore.enumerateRecords(null, null, false);
            while (re.hasNextElement()) {
                currentRecordId = re.nextRecordId();
                currentNumber = new ComunicationSetting(recordStore.getRecord(currentRecordId));
                currentNumber.setId(currentRecordId);
                buffer.addElement(currentNumber);
            }
        } catch (Exception ex) {
           
        }

        int numPlayers = buffer.size();
        result = new ComunicationSetting[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            result[i] = (ComunicationSetting) buffer.elementAt(i);
        }
        return result;
    }
}