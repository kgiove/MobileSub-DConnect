
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.actions;

import mobile.sdc.settings.LangXml;
import mobile.sdc.menu.StartMenu;
import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import mobile.sdc.task.TaskReceiveSms;
import mobile.sdc.task.TaskSendMessage;
import mobile.sdc.visitForm.Visit;


/**
 *
 * @author ietservizi
 */
public class ActionSms extends Visit implements ActionListener {
   
    Form main_sms = null;
    public static TextField BodyMessage = null;
    public static int countSms_free_msg;

    public void start(){

        main_sms = new Form("Your Message:");
        main_sms.setLayout(new BorderLayout());
        BodyMessage = new TextField();
        main_sms.addComponent(BorderLayout.CENTER,BodyMessage);
        main_sms.setCommandListener(this);
        main_sms.addCommand(LangXml.Cmd_back1);
        main_sms.addCommand(LangXml.Cmd_send15);
        main_sms.show();

    }

    private String getSms(){
        
        String sms = BodyMessage.getText();
        return sms;
    }
   
    public void actionPerformed(ActionEvent ae) {
         Command c = ae.getCommand();
            if(c.getId()== 1){
                StartMenu.form_Main.show();
            } else if(c.getId()==15){
                int result = valueConnectionSettings();
                String numTel = getYourNumberSetted();
                if(numTel!=null && !numTel.equals("")){
                    switch(result){
                        
                        case -2:Dialog.show(LangXml.Msg, "System Error", "OK",null);
                        break;
                        case -1:Dialog.show(LangXml.Msg, "You must set communication protocol", "OK",null);
                        break;
                        case 0: if(sendThrowsGPRS(numTel,getSms())){                        
                        }else{
                            Dialog.show(LangXml.Msg, "Server not Avaible", "OK",null);
                        }
                        break; 
                        case 1:if(sendThrowsGPRS(numTel,getSms())){
                        }else{
                            if(Dialog.show(LangXml.Msg,"GPRS connection NOT available. Would you like to proceed sending info via SMS?",
                                "Ok", "Cancel")){
                                if(!checkNumber()){
                                    Dialog.show(LangXml.Msg,LangXml.Dlg_NumAlert, "OK",null);
                                }else{
                                    TaskReceiveSms.manager.runTask(new TaskSendMessage(getSms()));                                
                                }
                            }
                        }
                        break;
                        case 2: if(!checkNumber()){
                        Dialog.show(LangXml.Msg,LangXml.Dlg_NumAlert, "OK",null);
                    }else{
                            TaskReceiveSms.manager.runTask(new TaskSendMessage(getSms())); 
                        }
                        break;
                    }
                }else{
                    Dialog.show(LangXml.Msg, LangXml.Protocol_NotOk, "OK",null);
                }
            }
    }
}
