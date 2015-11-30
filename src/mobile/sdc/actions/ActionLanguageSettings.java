/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.actions;

import com.sun.lwuit.CheckBox;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import mobile.sdc.menu.StartMenu;
import mobile.sdc.settings.LangXml;
import org.kxml.Xml;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.XmlParser;

/**
 *
 * @author ietservizi
 */
public class ActionLanguageSettings implements ActionListener  {

    private Form languageSetting = null;
    private String fileXML = "";
    private ComboBox comboBox = null;
    private Container container = null;


    public void start() {

        languageSetting = new Form(LangXml.Sel_Lang);
        languageSetting.setLayout(new BorderLayout());
        viewXML_file();
        languageSetting.addComponent(BorderLayout.CENTER, container);
        languageSetting.addCommand(LangXml.Cmd_back1);
        if(comboBox.size()>0)
            languageSetting.addCommand(LangXml.Cmd_confirm2);
        languageSetting.setCommandListener(this);
        languageSetting.show();

    }    
    
    public void viewXML_file(){


        container = new Container();
        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        comboBox = new ComboBox();
        comboBox.setListCellRenderer(new checkBoxRenderer());
       
        try{
            FileConnection fileXml = getCurrentRoot("file:///E:/BIB/Language");
            Enumeration pr = fileXml.list();
            while(pr.hasMoreElements()){
                fileXML = pr.nextElement().toString();
            if(fileXML.endsWith("xml")){
                comboBox.addItem(fileXML.substring(0, fileXML.length()-4));                
                }           
            }
            fileXml.close();
            container.addComponent(comboBox);

        }catch (Exception ex) {}



}
    protected void parseXML(String fileXml){

    try{

        String path_File_Xml = "file:///E:/BIB/Language/"+fileXml+".xml";
        FileConnection file = (FileConnection) Connector.open(path_File_Xml);
        if(file.exists()){
        
        Reader xmlReader = new InputStreamReader(file.openInputStream());
        XmlParser xmlParser = new XmlParser(xmlReader);
        ParseEvent parseEvent = null;
        xmlParser.skip();
        parseEvent = xmlParser.read(Xml.START_TAG,null,"Language");
        boolean parsingActive = true;
            while(parsingActive){
                parseEvent = xmlParser.read();
                if (parseEvent.getType()==Xml.START_TAG){
                    if (parseEvent.getName().equals("ProductMaster")){
                        LangXml.ProductMaster_name = xmlParser.read().getText();
                        StartMenu.myListD.setItem(0, LangXml.ProductMaster_name);
                    }else if (parseEvent.getName().equals("Sale")){
                        LangXml.Sale_name = xmlParser.read().getText();
                        StartMenu.myListD.setItem(1, LangXml.Sale_name);
                    }else if (parseEvent.getName().equals("Order")){
                        LangXml.Ord_name = xmlParser.read().getText();
                        StartMenu.myListD.setItem(2, LangXml.Ord_name);
                    }else if(parseEvent.getName().equals("Inv")){
                        LangXml.Inv_name = xmlParser.read().getText();
                        StartMenu.myListD.setItem(3, LangXml.Inv_name);
                    }else if(parseEvent.getName().equals("Report")){
                        LangXml.Report_name = xmlParser.read().getText();
                        StartMenu.myListD.setItem(4, LangXml.Report_name);
                    }else if(parseEvent.getName().equals("Rec_Data")){
                        LangXml.RecData_name = xmlParser.read().getText();
                        StartMenu.myListD.setItem(5, LangXml.RecData_name);
                    }else if(parseEvent.getName().equals("NewSms")){
                        LangXml.New_Sms = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Setting")){
                        LangXml.Settings_name = xmlParser.read().getText();
                        StartMenu.myListD.setItem(6, LangXml.Settings_name);
                    }else if(parseEvent.getName().equals("Fpc")){
                        LangXml.FPC_code = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Desc")){
                        LangXml.Desc = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Brand")){
                        LangXml.Brand = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Cost")){
                        LangXml.Cost = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Sell_Price")){
                        LangXml.Sell_price = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Targ")){
                        LangXml.Target_Inv = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Vol")){
                        LangXml.Volume = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("TotAm")){
                        LangXml.TotAm = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("PrdList")){
                        LangXml.PrdList = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("BrandsList")){
                        LangXml.BrandsList = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("DataSett")){
                        LangXml.DataSett = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("InsVolReq")){
                        LangXml.MsgRequestVol = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("RepMenu")){
                        LangXml.RepMenu = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("RepSale")){
                        LangXml.RepSale = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("RepOrd")){
                        LangXml.RepOrd = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("RepInv")){
                        LangXml.RepInv = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("SaleDatesList")){
                        LangXml.SaleDateList = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("OrderDateList")){
                        LangXml.OrderDateList = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("InvDateList")){
                        LangXml.InvDateList = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("TabSale")){
                        LangXml.TabSale = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("TabOrd")){
                        LangXml.TabOrd = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("TabInv")){
                        LangXml.TabInv = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("SetNumber")){
                        LangXml.SetNumber = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("DelProdmaster")){
                        LangXml.DelProdmaster = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("WorkSpace")){
                        LangXml.WorkSpace = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("LanguageSett")){
                        LangXml.LanguageSett = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Tot")){
                        LangXml.Tot = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Msg")){
                        LangXml.Msg = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Neg")){
                        LangXml.Neg = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Det")){
                        LangXml.Det = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("NumbSett")){
                        LangXml.NumbSett = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Dlg_UpPM")){
                        LangXml.Dlg_UpPM = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Dlg_DelPM")){
                        LangXml.Dlg_DelPM = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Dlg_NumAlert")){
                        LangXml.Dlg_NumAlert = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Dlg_NumSucc")){
                        LangXml.Protocol_ok = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Dlg_UpPMAlt")){
                        LangXml.Dlg_UpPMAlt = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Dlg_AltDelPM")){
                        LangXml.Dlg_AltDelPM = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Dlg_insrt")){
                        LangXml.Dlg_insrt = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Dlg_AlNumb")){
                        LangXml.Dlg_AlNumb = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Dlg_AlNumb")){
                        LangXml.Dlg_AlNumb = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("Sel_Lang")){
                        LangXml.Sel_Lang = xmlParser.read().getText();
                    }else if(parseEvent.getName().equals("cmd_exit")){
                        StartMenu.form_Main.removeCommand(LangXml.cmd_exit);
                        LangXml.cmd_exit = new Command(xmlParser.read().getText());
                        StartMenu.form_Main.addCommand(LangXml.cmd_exit);
                    }else if(parseEvent.getName().equals("Cmd_back")){
                        String back = xmlParser.read().getText();
                        LangXml.Cmd_back1 = new Command(back,1);
                        LangXml.Cmd_back2 = new Command(back,2);
                        LangXml.Cmd_back3 = new Command(back,3);
                        LangXml.Cmd_back7 = new Command(back,7);
                        LangXml.Cmd_back8 = new Command(back,8);
                        LangXml.Cmd_back9 = new Command(back,9);
                        LangXml.Cmd_back10 = new Command(back,10);
                    }else if(parseEvent.getName().equals("Cmd_send15")){
                        LangXml.Cmd_send15 = new Command(xmlParser.read().getText(),15);
                    }else if(parseEvent.getName().equals("Cmd_details2")){
                        LangXml.Cmd_details2 = new Command(xmlParser.read().getText(),2);
                    }else if(parseEvent.getName().equals("Cmd_delete3")){
                        LangXml.Cmd_delete3 = new Command(xmlParser.read().getText(),3);
                    }else if(parseEvent.getName().equals("Cmd_AddAllSkus4")){
                        LangXml.Cmd_AddAllSkus4 = new Command(xmlParser.read().getText(),4);
                    }else if(parseEvent.getName().equals("Cmd_AddSku5")){
                        LangXml.Cmd_AddSku5 = new Command(xmlParser.read().getText(),5);
                    }else if(parseEvent.getName().equals("Cmd_clearTable12")){
                        LangXml.Cmd_clearTable12 = new Command(xmlParser.read().getText(),12);
                    }else if(parseEvent.getName().equals("Cmd_save6")){
                        String save = xmlParser.read().getText();
                        LangXml.Cmd_save2 = new Command(save,2);
                        LangXml.Cmd_save4 = new Command(save,4);
                        LangXml.Cmd_save6 = new Command(save,6);
                        LangXml.Cmd_save7 = new Command(save,7);
                        LangXml.Cmd_save11 = new Command(save,11);
                    }else if(parseEvent.getName().equals("Cmd_confirm2")){
                        LangXml.Cmd_confirm2 = new Command(xmlParser.read().getText(),2);
                    }
                }else{
                  // In caso contrario vrifichiamo se si tratta della chiusura del tag meteo_data
                  // nel qual caso terminiamo il ciclo
                        parsingActive = !(parseEvent.getType()==Xml.END_TAG &&
                        parseEvent.getName().equals("Language"));
                        file.close();
                    }
                 }
              }
            }catch(Exception ex){
            System.out.println("Message:"+ex.getMessage());

        }
    }

    public  FileConnection getCurrentRoot(String url){
        FileConnection currentRoot = null;
        try{
            currentRoot = (FileConnection) Connector.open(
                            url, Connector.READ);

        }catch(Exception e){
            System.out.println(e.getClass());
            currentRoot = null;
        }

        return  currentRoot;
    }

    public void actionPerformed(ActionEvent ae) {
        Command cmd = ae.getCommand();
            switch (cmd.getId()){
                case 1: StartMenu.form_Main.show();
                break;
                case 2: parseXML(comboBox.getSelectedItem().toString());
                        languageSetting.setTitle(LangXml.Sel_Lang);
                        languageSetting.removeAllCommands();
                        languageSetting.addCommand(LangXml.Cmd_back1 );
                        languageSetting.addCommand(LangXml.Cmd_confirm2 );
                break;
            }

    }

    private static class checkBoxRenderer extends CheckBox implements ListCellRenderer {
        /** Creates a new instance of checkBoxRenderer */
        public checkBoxRenderer() {
        super("");
            }

        public Component getListCellRendererComponent(List list,
        Object value, int index, boolean isSelected) {
        setText("" + value);
        if (isSelected) {
        setFocus(true);
        setSelected(true);
        } else {
        setFocus(false);
        setSelected(false);
        }
        return this;
        }
        // Returning the list focus component
        public Component getListFocusComponent(List list) {
        setText("");
        setFocus(true);
        setSelected(true);
        return this;
        }
    }
}
