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
import mobile.sdc.sku.Product;

public class ProductRs extends Rs{
  
    public ProductRs(String fileName){
        super(fileName);
    }
    
    public synchronized Product[] getProductList(){
        Vector buffer = new Vector();
        Product[] result = null;
        
        try {            
            int currentRecordId = 0;
            Product currentProduct = null;
            RecordEnumeration re = recordStore.enumerateRecords(null, null, false);
            while(re.hasNextElement()) {
                currentRecordId = re.nextRecordId();
                currentProduct = new Product(recordStore.getRecord(currentRecordId));
                currentProduct.setId(currentRecordId);
                buffer.addElement(currentProduct);
            }
        }catch(Exception ex){}
        int numPlayers = buffer.size();
        result = new Product[numPlayers];
        for (int i=0; i < numPlayers; i++){
            result[i] = (Product) buffer.elementAt(i);
        }
        return result;
    }
    
}


