/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile.sdc.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author kgiove
 */
public class TaskSendThrowsGprs {

    private String URL = "http://www.subdconnect.com:8080/MobileSubDConnect/accept.sms";
//    private String URL = "http://pg.cefriel.it:8090/MobileSubDConnect/service/message/";
    private HttpConnection connection;
    private InputStream input;
    String data = "";
    String tel = "";
//    String param = "text";
    String param = "message";

    public TaskSendThrowsGprs(String tel, String data) {
        this.data = data;
        this.tel = tel;
    }

    public boolean sendDataWithHttpGet() {
        boolean result = false;
        try {

//         URL+=tel+"?"+TaskUtility.urlEncode(param+"="+data);
            URL += "?mobileno=" + tel + "&" + TaskUtility.urlEncode(param + "=" + data);
            connection = (HttpConnection) Connector.open(URL);
            connection.setRequestMethod(HttpConnection.GET);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpConnection.HTTP_OK) {

                result = true;
            } else {

                result = false;
            }

            close();

        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    public boolean sendDataWithHttpPost() throws IOException {
        boolean result = false;
        HttpConnection httpConn = null;
        InputStream is = null;
        OutputStream os = null;

        try {
            httpConn = (HttpConnection) Connector.open(URL);
            httpConn.setRequestMethod(HttpConnection.POST);
            httpConn.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Confirguration/CLDC-1.0");
            httpConn.setRequestProperty("Accept_Language", "en-US");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            os = httpConn.openOutputStream();
            String params;
            params = "text=" + data;
            os.write(params.getBytes());
            result = true;

        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            if (httpConn != null) {
                httpConn.close();
            }
        }

        return result;
    }

    private void close() {
        try {
            input.close();
        } catch (Exception e) {
        } finally {
            input = null;
        }
        try {
            connection.close();
        } catch (Exception e) {
        } finally {
            connection = null;
        }
    }
}