/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.visitForm;

import mobile.sdc.settings.LangXml;
import mobile.sdc.actions.ActionProductMaster;
import mobile.sdc.menu.StartMenu;
import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.events.ActionEvent;

/**
 *
 * @author ietservizi
 */
public class VisitInventory extends Visit {

    public void start() {
        try {
            setTypeSku(2);
            setTitleForm(LangXml.Inv_name);
            open_AllRecords();
            create_HashTables();
            load_HashTables_product();
            if (ActionProductMaster.addAllControlInv == 0) {
                list_Skus();
            } else {
                list_addAllSkus();
            }
        } catch (Exception e) {
        }
    }

    public String retrieveAllSkusTable() {
        return "INVE;" + super.retrieveAllSkusTable();
    }

    public void actionPerformed(ActionEvent ae) {

        super.actionPerformed(ae);
        Command d = ae.getCommand();
        if (d.getId() == 1) {
            close_AllRecords();
            StartMenu.form_Main.show();
        } else if (d.getId() == 15) {
            msg = retrieveAllSkusTable();
            curr_PMVersion = retrieve_PMVersion();
            int result = valueConnectionSettings();
            String numTel = getYourNumberSetted();
            if (numTel != null && !numTel.equals("")) {
                switch (result) {
                    case -2:
                        Dialog.show(LangXml.Msg, "System Error", "OK", null);
                        break;
                    case -1:
                        Dialog.show(LangXml.Msg, "You must set communication protocol", "OK", null);
                        break;
                    case 0:
                        if (sendThrowsGPRS(numTel, curr_PMVersion + msg)) {
                            ActionProductMaster.addAllControlInv = 0;
                            save_SkusReport(0);
                            list_Skus();
                        } else {
                            Dialog.show(LangXml.Msg, "Server not Avaible", "OK", null);
                        }
                        break;
                    case 1:
                        if (sendThrowsGPRS(numTel, curr_PMVersion + msg)) {
                            ActionProductMaster.addAllControlInv = 0;
                            save_SkusReport(0);
                            list_Skus();
                        } else {
                            if (Dialog.show(LangXml.Msg, "GPRS connection NOT available. Would you like to proceed sending info via SMS?", "Ok", "Cancel")) {
                                if (!checkNumber()) {
                                    Dialog.show(LangXml.Msg, LangXml.Dlg_NumAlert, "OK", null);
                                } else {
                                    if (divideSmsToSending(curr_PMVersion + msg)) {
                                        ActionProductMaster.addAllControlInv = 0;
                                        save_SkusReport(0);
                                        list_Skus();
                                    }
                                }
                            }
                        }
                        break;
                    case 2:
                        if (!checkNumber()) {
                            Dialog.show(LangXml.Msg, LangXml.Dlg_NumAlert, "OK", null);
                        } else {
                            String content = "";
                            if (isMobile()) {
                                content = msg;
                            } else {
                                content = curr_PMVersion + msg;
                            }
                            if (divideSmsToSending(content)) {
                                ActionProductMaster.addAllControlInv = 0;
                                save_SkusReport(0);
                                list_Skus();
                            }
                        }
                        break;
                }
            } else {
                Dialog.show(LangXml.Msg, LangXml.Protocol_NotOk, "OK", null);
            }
        }
    }
}
