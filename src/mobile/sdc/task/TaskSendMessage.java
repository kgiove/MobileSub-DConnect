/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.task;

import mobile.sdc.actions.ActionSettings;
import com.sun.lwuit.Dialog;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;


/**
 *
 * @author ietservizi
 */
public class TaskSendMessage implements Runnable {

    private String text;
    private static final int DEFAULT_PORT = 2501;
    private String address = "";

    public TaskSendMessage(String text) {
        this.text = text;
    }

    public void run() {
        try {

            if (TaskReceiveSms.connection != null) {
                TextMessage message = (TextMessage) TaskReceiveSms.connection.newMessage(MessageConnection.TEXT_MESSAGE);
                address = selectNumber();
                message.setAddress(address);
                message.setPayloadText(text);
                TaskReceiveSms.connection.send(message);
                Dialog.show("Message", "Messaggio Sent ", "OK", null);

            } else {
                Dialog.show("Message", "Errore", "OK", null);
                notifySendMessageFailed(new Exception("Connection not ready!"));
            }
        } catch (Exception e) {
            Dialog.show("Message", "Errore :" + e.getMessage(), "OK", null);
            e.printStackTrace();
            notifySendMessageFailed(e);
        }
    }

    private void notifySendMessageFailed(Exception exception) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String selectNumber() {

        String result = "";
        ActionSettings.numberList = ActionSettings.nb.getNumber();

        if (ActionSettings.numberList[0].isMobile()) {
            if (!ActionSettings.numberList[0].getMobileNumber().equals("")) {
                result = "sms://" + ActionSettings.numberList[0].getMobileNumber() + ":" + DEFAULT_PORT;
            }
        } else {
            if (!ActionSettings.numberList[0].getGatewayNumber().equals("")) {
                result = "sms://" + ActionSettings.numberList[0].getGatewayNumber();
            }
        }

        return result;
    }
}