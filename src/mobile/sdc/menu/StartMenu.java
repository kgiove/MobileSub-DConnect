package mobile.sdc.menu;

import mobile.sdc.actions.ActionSettings;
import mobile.sdc.settings.LangXml;
import mobile.sdc.actions.ActionProductMaster;
import mobile.sdc.actions.ActionSms;
import mobile.sdc.task.TaskReceiveSms;
import mobile.sdc.visitForm.VisitOrder;
import mobile.sdc.visitForm.VisitSale;
import mobile.sdc.visitForm.VisitInventory;
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.midlet.MIDlet;

public class StartMenu extends MIDlet implements ActionListener {

    private String password_1 = "3579";
    private FileConnection currentRoot = null;
    private FileConnection newDir = null;
    private ActionProductMaster pm = null;
    private VisitSale vs = null;
    private VisitOrder vo = null;
    private VisitInventory vi = null;
    private ReportMenu rm = null;
    private TextField demoText;
    private Container container = null;
    private ActionSms sm = null;
    private ActionSettings st = null;
    private TaskReceiveSms taskSms = null;
    public static IncomingMenu mn = null;
    public static Display display;
    public static Form form_Main, login_Form;
    public static DefaultListModel myListD = null;
    public static List myList = null;
    public static String midletClassName;

    public void startApp() {

        midletClassName = this.getClass().getName();
        Resources res;
        Display.init(this);  // Initialize LWUIT

        try {
            res = Resources.open("/res/businessTheme.res");
            UIManager.getInstance().setThemeProps(res.getTheme(res.getThemeResourceNames()[0]));
        } catch (IOException ioe) {
        }

        login();
    }

    private void execute() {

        createWorkingtDirectories();
        taskSms = new TaskReceiveSms();
        taskSms.SetupConnection();
        pm = new ActionProductMaster();
        vs = new VisitSale();
        vo = new VisitOrder();
        vi = new VisitInventory();
        rm = new ReportMenu();
        mn = new IncomingMenu();
        st = new ActionSettings();
        sm = new ActionSms();

        form_Main = new Form("Mobile Sub-D Connect");
        Font font = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
        form_Main.getTitleStyle().setFont(font);
        form_Main.setLayout(new BorderLayout());

        String[] Items = {
            LangXml.ProductMaster_name,
            LangXml.Sale_name,
            LangXml.Ord_name,
            LangXml.Inv_name,
            LangXml.Report_name,
            LangXml.RecData_name,
            LangXml.Settings_name,
            LangXml.Send_msg
        };

        myListD = new DefaultListModel(Items);
        myList = new List(myListD);
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                switch (myListD.getSelectedIndex()) {
                    case 0:
                        pm.start();
                        break;
                    case 1:
                        vs.start();
                        break;
                    case 2:
                        vo.start();
                        break;
                    case 3:
                        vi.start();
                        break;
                    case 4:
                        rm.start();
                        break;
                    case 5:
                        mn.start();
                        break;
                    case 6:
                        st.start();
                        break;
                    case 7:
                        sm.start();
                        break;
                }
            }
        };

        myList.addActionListener(al);
        form_Main.addComponent(BorderLayout.CENTER, myList);
        form_Main.addCommand(LangXml.cmd_exit);
        form_Main.setCommandListener((ActionListener) this);
        form_Main.show();
    }

    private void login() {

        login_Form = new Form(LangXml.Login);
        Font font = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
        login_Form.getTitleStyle().setFgColor(0x99cc00);
        login_Form.getTitleStyle().setFont(font);
        login_Form.getTitleStyle().setBgColor(0x555555);
        login_Form.setLayout(new BorderLayout());
        container = new Container();
        demoText = new TextField(5);

        container.addComponent(demoText);
        demoText.setConstraint(TextField.PASSWORD);
        demoText.setInputMode("123");
        login_Form.addComponent(BorderLayout.CENTER, container);
        login_Form.addCommand(new Command(LangXml.Confirm, 0));
        login_Form.setCommandListener(this);
        login_Form.show();

    }

    private void authentication() {

        if (demoText.getText().equals(password_1)) {
            execute();
        } else {
            Dialog.show(LangXml.Msg, LangXml.AlarmLogin, "OK", null);
            login();
        }
    }

    public void createWorkingtDirectories() {

        try {

            currentRoot = getCurrentRoot("file:///E:/Settings");
            if (!currentRoot.exists()) {
                newDir =
                        (FileConnection) Connector.open(
                        "file:///E:/Settings/",
                        Connector.WRITE);
                newDir.mkdir();

                newDir = (FileConnection) Connector.open(
                        "file:///E:/Settings/Language",
                        Connector.WRITE);
                newDir.mkdir();
            }

        } catch (Exception ex) {
        }

    }

    public FileConnection getCurrentRoot(String url) {
        FileConnection currentRoot = null;
        try {
            currentRoot = (FileConnection) Connector.open(
                    url, Connector.READ);
        } catch (Exception e) {
            System.out.println(e.getClass());
            currentRoot = null;
        }

        return currentRoot;
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        if (TaskReceiveSms.connection != null) {
            try {
                TaskReceiveSms.connection.close();
            } catch (IOException e) {
            }
        }
    }

    public void actionPerformed(ActionEvent ae) {

        Command c = ae.getCommand();

        if (Display.getInstance().getCurrent() == form_Main) {
            destroyApp(true);
            notifyDestroyed();

        } else if (Display.getInstance().getCurrent() == login_Form) {
            if (c.getId() == 0) {
                authentication();
            }
        }
    }
}