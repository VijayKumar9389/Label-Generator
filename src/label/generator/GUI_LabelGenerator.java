

import java.io.File;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;

public class GUI_LabelGenerator extends javax.swing.JFrame {

    static ArrayManager am = new ArrayManager();
    static ArrayManager CoreSheet;
    static ArrayManager TestRun;
    
    DefaultTableModel model = new DefaultTableModel(); 
    static CardLayout Layout = new CardLayout();
    
    public GUI_LabelGenerator() {
        initComponents();
    }
    
    public static void CreateDirectory(String path){
        Path LabelGeneratorFolder = Paths.get(path);
        boolean CheckDir = Files.exists(LabelGeneratorFolder);
        
        if(CheckDir == false){
            try{
                Files.createDirectory(LabelGeneratorFolder);
            }
            catch(Exception e){
                
            }
        }
    }
    
    public static boolean IsNumaric(String s) {
    try { 
        Double.parseDouble(s); 
    } catch(NumberFormatException e) { 
        return false; 
    }
    return true;
}
    
    public static int GenerateID(String IDToGenerate) throws ClassNotFoundException, SQLException{
        
        int ID = 0;
        boolean FoundId = false;
             
        if (IDToGenerate == "coresheet"){
            
            for (int i = 0; i < CoreSheet.size(); i++) {
               
            if (i != ((CoreSheet)CoreSheet.Getelementbypos(i)).getID()){
                
            ID = i;
            FoundId = true;
            i = CoreSheet.size();
            
            }
            
            }
            
            if (FoundId == false){
                ID = CoreSheet.size();
                UpdateTablePopulation("+CoreSheet");
            }
            
        }
        
        else if (IDToGenerate == "corerun"){
            for (int i = 0; i < TestRun.size(); i++) {
            if (((TestRun)TestRun.Getelementbypos(i)).GetID() > ID)
            ID = ((TestRun)TestRun.Getelementbypos(i)).GetID();
            }
            ID++;
        }
                
        return ID;
    }
    
    public static void AddCoreRun() throws ClassNotFoundException, SQLException{
        
        int ParentSheet = ((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).getID();
        
        JTextField txtAddTime = new JTextField("");
        JTextField txtAddTubeNumber1 = new JTextField("");
        JTextField txtAddTubeNumber2 = new JTextField("");
        JTextField txtAddCoreFromDepth = new JTextField("");
        JTextField txtAddCoreToDepth = new JTextField("");
        JTextField txtAddMetersCored = new JTextField("");
        JTextField txtAddKellyDown = new JTextField("");
        JTextField txtAddCoreRunRecovery = new JTextField("");
        JTextField txtAddDescription = new JTextField("");
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        
        panel.add(new JLabel("Time"));
        panel.add(txtAddTime);
        panel.add(new JLabel("Meters Cored"));
        panel.add(txtAddMetersCored);
        panel.add(new JLabel("Kelly Down"));
        DefaultComboBoxModel ddlKellyDown = new DefaultComboBoxModel();
                ddlKellyDown.addElement(" ");
                ddlKellyDown.addElement("KD");
                JComboBox comboBox = new JComboBox(ddlKellyDown);
                panel.add(comboBox);
        panel.add(new JLabel("Meters Recovered"));
        panel.add(txtAddCoreRunRecovery);
        panel.add(new JLabel("Description"));
                DefaultComboBoxModel ddlDecription = new DefaultComboBoxModel();
                ddlDecription.addElement("OilSand");
                ddlDecription.addElement("Clay");
                ddlDecription.addElement("WaterSand");
                ddlDecription.addElement("Overburden");
                ddlDecription.addElement("Gavel");
                ddlDecription.addElement("OilSand/Clay");
                ddlDecription.addElement("OilSand/WaterSand");
                ddlDecription.addElement("Overburden/Clay");
                ddlDecription.addElement("Rock");
                JComboBox comboBox2 = new JComboBox(ddlDecription);
                panel.add(comboBox2);
        
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Test Run",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            
            if (txtAddTime.getText().equals("") ||
                    txtAddMetersCored.getText().equals("") ||
                    txtAddCoreRunRecovery.getText().equals("")){
                
                JOptionPane.showMessageDialog(null, "Please fill in all fields");
                AddCoreRun();
                
            }
            else if (IsNumaric(txtAddMetersCored.getText()) == false){
                
                JOptionPane.showMessageDialog(null, "Please enter a number for Meters Cored");
                AddCoreRun();
                
            }
            else if (IsNumaric(txtAddCoreRunRecovery.getText()) == false){
                
                JOptionPane.showMessageDialog(null, "Please enter a number for Meters Recovered");
                AddCoreRun();
                
            }
            else {
                
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
        Statement statement = con.createStatement();
        
        String sql = "INSERT INTO corerun (id, run, time, tubenumber1, tubenumber2, corefromdepth, coretodepth, meterscored, KellyDown, metersrecovered, description, parentcoresheet) " +
        "VALUES ("+GenerateID("corerun")+", "+GetRun(ParentSheet)+", '"+txtAddTime.getText()+"', "+GetTubeNumbers(ParentSheet)+", "+(GetTubeNumbers(ParentSheet) + 1)+", "+GetCoreFromDepth(ParentSheet)+", "+(GetCoreFromDepth(ParentSheet) + Double.parseDouble(txtAddMetersCored.getText()))+", "+Double.parseDouble(txtAddMetersCored.getText())+", '"+ddlKellyDown.getSelectedItem()+"', "+Double.parseDouble(txtAddCoreRunRecovery.getText())+", '"+ddlDecription.getSelectedItem()+"', "+ParentSheet+" );"; 
        
        statement.executeUpdate(sql);
        statement.close();
        con.close();
        
        UpdateTablePopulation("+CoreRun");
        PopulateArray();
        populateTable();
        GetTotalPercentageRecovery(ParentSheet);
                
            }
            
        } 
        else {
            System.out.println("Cancelled");
        }
  
    }
    
    public static void EditCoreRun() throws ClassNotFoundException, SQLException{
        DefaultTableModel model = (DefaultTableModel) tblTestRuns.getModel();
        int index = (int)tblTestRuns.getSelectedRow();
        int CoreRunID = (int)model.getValueAt(index, 10);
        int pos = GetCoreRunPosition(CoreRunID);
        int ParentSheet = ((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).getID();
        double MetersRecovered = ((TestRun)TestRun.Getelementbypos(pos)).getMetersRecovered();
        
        JTextField txtEditTime = new JTextField(((TestRun)TestRun.Getelementbypos(pos)).GetTime());
        JTextField txtEditCoreRunRecovery = new JTextField(""+MetersRecovered+"");
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        
        panel.add(new JLabel("Time"));
        panel.add(txtEditTime);
        panel.add(new JLabel("Kelly Down"));
                DefaultComboBoxModel ddlKellyDown = new DefaultComboBoxModel();
                ddlKellyDown.addElement(((TestRun)TestRun.Getelementbypos(pos)).GetKD());
                ddlKellyDown.addElement(" ");
                ddlKellyDown.addElement("KD");
                JComboBox comboBox = new JComboBox(ddlKellyDown);
                panel.add(comboBox);
        panel.add(new JLabel("Meters Recovered"));
        panel.add(txtEditCoreRunRecovery);
        panel.add(new JLabel("Description"));
                DefaultComboBoxModel ddlDecription = new DefaultComboBoxModel();
                ddlDecription.addElement(((TestRun)TestRun.Getelementbypos(pos)).GetDescription());
                ddlDecription.addElement("OilSand");
                ddlDecription.addElement("Clay");
                ddlDecription.addElement("WaterSand");
                ddlDecription.addElement("Overburden");
                ddlDecription.addElement("Gavel");
                ddlDecription.addElement("OilSand/Clay");
                ddlDecription.addElement("OilSand/WaterSand");
                ddlDecription.addElement("Overburden/Clay");
                ddlDecription.addElement("Rock");
                JComboBox comboBox2 = new JComboBox(ddlDecription);
                panel.add(comboBox2);
        
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Test Run",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {

            
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
        Statement statement = con.createStatement();
        
        String sql = "update corerun set time = '"+txtEditTime.getText()+"', kellydown = '"+ddlKellyDown.getSelectedItem()+"', metersrecovered = "+Double.parseDouble(txtEditCoreRunRecovery.getText())+", Description = '"+ddlDecription.getSelectedItem()+"' where id = "+CoreRunID+""; 
        
        statement.executeUpdate(sql);
        statement.close();
        con.close();

        PopulateArray();
        populateTable();
        GetTotalPercentageRecovery(ParentSheet);
          
        } 
        else {
            System.out.println("Cancelled");
        }
    populateTable();
       
    }
    
    public static void DeleteCoreRun() throws ClassNotFoundException, SQLException{
        
        int ParentSheet = ((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).getID();
        int CoreRunToDelete = 0;
        
        for (int i = 0; i < TestRun.size(); i++) {
            if (((TestRun)TestRun.Getelementbypos(i)).getParentCoreSheet() == ParentSheet && ((TestRun)TestRun.Getelementbypos(i)).GetRun() > CoreRunToDelete){
                CoreRunToDelete = i;
            }
        }
        
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
        Statement statement = con.createStatement();
        
        String sql = "Delete from Corerun where ID = "+((TestRun)TestRun.Getelementbypos(CoreRunToDelete)).GetID()+""; 
        
        statement.executeUpdate(sql);
        statement.close();
        con.close();
        
        UpdateTablePopulation("-CoreRun");
        PopulateArray();
        populateTable();
        GetTotalPercentageRecovery(ParentSheet);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        PDF = new javax.swing.JPanel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        lblViewDrillProgramPDF = new javax.swing.JLabel();
        lblViewLeasePDF = new javax.swing.JLabel();
        lblViewWellIDPDF = new javax.swing.JLabel();
        lblViewCompanyPDF = new javax.swing.JLabel();
        lblViewConductorCasingDepthPDF = new javax.swing.JLabel();
        lblViewConductorCasingElevationPDF = new javax.swing.JLabel();
        lblViewCorePointDepthPDF = new javax.swing.JLabel();
        lblViewCorePointElevationPDF = new javax.swing.JLabel();
        lblViewGroundElevationPDF = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        lblViewDrillingCompanyPDF = new javax.swing.JLabel();
        lblViewRigUnitNumberPDF = new javax.swing.JLabel();
        lblViewWellSpudDatePDF = new javax.swing.JLabel();
        lblViewWellSpudTimePDF = new javax.swing.JLabel();
        lblViewWellCompletionDatePDF = new javax.swing.JLabel();
        lblViewWellCompletionTimePDF = new javax.swing.JLabel();
        lblViewWellTDPDF = new javax.swing.JLabel();
        lblViewTotalPercentageRecoveryPDF = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtViewRemarkPDF = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblPDFTable = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ParentPanel = new javax.swing.JPanel();
        AddCoreSheet = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtAddDrillProgram = new javax.swing.JTextField();
        txtAddLease = new javax.swing.JTextField();
        txtAddWellID = new javax.swing.JTextField();
        txtAddCompany = new javax.swing.JTextField();
        txtAddConductorCasingDepth = new javax.swing.JTextField();
        txtAddCorePointDepth = new javax.swing.JTextField();
        txtAddGroundElevation = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtAddDrillingCompany = new javax.swing.JTextField();
        txtAddRigUnitNumber = new javax.swing.JTextField();
        txtAddWellSpudDate = new javax.swing.JTextField();
        txtAddWellCompletionDate = new javax.swing.JTextField();
        txtAddWellTD = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAddRemarks = new javax.swing.JTextArea();
        btnAddLogo = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnCancelAddCoreSheet = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtAddWellSpudTime = new javax.swing.JTextField();
        txtAddWellCompletionTime = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        ViewCoreSheet = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblViewDrillProgram = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lblViewLease = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblViewWellID = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        lblViewCompany = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        lblViewConductorCasingDepth = new javax.swing.JLabel();
        lblViewConductorCasingElevation = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        lblViewCorePointDepth = new javax.swing.JLabel();
        lblViewCorePointElevation = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        lblViewGroundElevation = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        lblViewDrillingCompany = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        lblViewRigUnitNumber = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        lblViewWellSpudDate = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        lblViewWellSpudTime = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        lblViewWellCompletionDate = new javax.swing.JLabel();
        lblViewWellCompletionTime = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        lblViewWellTD = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        lblViewTotalPercentageRecovery = new javax.swing.JLabel();
        Remarks = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTestRuns = new javax.swing.JTable();
        btnAddTestRun = new javax.swing.JButton();
        btnDeleteTestRun = new javax.swing.JButton();
        txtEditTestRun = new javax.swing.JButton();
        btnEditCoreSheet = new javax.swing.JButton();
        btnDeleteCoreSheet = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtViewRemark = new javax.swing.JTextArea();
        HomeScreen = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        EditCoreSheet = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        txtEditDrillProgram = new javax.swing.JTextField();
        txtEditLease = new javax.swing.JTextField();
        txtEditWellID = new javax.swing.JTextField();
        txtEditCompany = new javax.swing.JTextField();
        txtEditConductorCasingDepth = new javax.swing.JTextField();
        txtEditCorePointDepth = new javax.swing.JTextField();
        txtEditGroundElevation = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        txtEditDrillingCompany = new javax.swing.JTextField();
        txtEditRigUnitNumber = new javax.swing.JTextField();
        txtEditWellSpudDate = new javax.swing.JTextField();
        txtEditWellCompletionDate = new javax.swing.JTextField();
        txtEditWellTD = new javax.swing.JTextField();
        btnEditUploadImage = new javax.swing.JButton();
        jLabel55 = new javax.swing.JLabel();
        btnEditSave = new javax.swing.JButton();
        btnEditCancel = new javax.swing.JButton();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        txtEditWellSpudTime = new javax.swing.JTextField();
        txtEditWellCompletionTime = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtEditRemarks = new javax.swing.JTextArea();
        jLabel16 = new javax.swing.JLabel();
        LabelTop = new javax.swing.JPanel();
        lblTopWellID = new javax.swing.JLabel();
        lblTopCoreFrom = new javax.swing.JLabel();
        lblTopCoreTo = new javax.swing.JLabel();
        lblTopRun = new javax.swing.JLabel();
        lblTopTubeNo = new javax.swing.JLabel();
        IconTop = new javax.swing.JLabel();
        LabelBottom = new javax.swing.JPanel();
        lblBottomWellID = new javax.swing.JLabel();
        lblBottomCoreFrom = new javax.swing.JLabel();
        lblBottomCoreTo = new javax.swing.JLabel();
        lblBottomRun = new javax.swing.JLabel();
        lblBottomTubeNo = new javax.swing.JLabel();
        iconBottom = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        btnAddCoreSheet = new javax.swing.JButton();
        txtSearchForCoreSheet = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        scrollCoreSheets = new javax.swing.JScrollPane();
        lstCoreSheets = new javax.swing.JList<>();
        jLabel59 = new javax.swing.JLabel();

        jPanel4.setPreferredSize(new java.awt.Dimension(192, 288));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        PDF.setMinimumSize(new java.awt.Dimension(2550, 3300));
        PDF.setPreferredSize(new java.awt.Dimension(2550, 3300));

        jLabel62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/crossBorders_logo_PDF2.png"))); // NOI18N

        jLabel63.setFont(new java.awt.Font("Tahoma", 1, 40)); // NOI18N
        jLabel63.setText("Toll Free:");

        jLabel64.setFont(new java.awt.Font("Tahoma", 1, 40)); // NOI18N
        jLabel64.setText("Address:");

        jLabel65.setFont(new java.awt.Font("Tahoma", 1, 40)); // NOI18N
        jLabel65.setText("Website:");

        jLabel66.setFont(new java.awt.Font("Tahoma", 1, 40)); // NOI18N
        jLabel66.setText("Email:");

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 40)); // NOI18N
        jLabel67.setText("1 (866) 788 - 3380");

        jLabel68.setFont(new java.awt.Font("Tahoma", 1, 40)); // NOI18N
        jLabel68.setText("• Phone:");

        jLabel69.setFont(new java.awt.Font("Tahoma", 0, 40)); // NOI18N
        jLabel69.setText("(306) 781 - 4484 ");

        jLabel70.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel70.setText("info@crossborders.ca");

        jLabel71.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel71.setText("• Fax:");

        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel72.setText("(306) 781 - 4489 ");

        jLabel73.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel73.setText("Box 509 Pilot Butte, SK, Canada S0G 3Z0 ");

        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel74.setText("www.crossborders.ca");

        jLabel75.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel75.setText("Core Recovery Log");

        jLabel76.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel76.setText("Drill Program");

        jLabel77.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel77.setText("Lease");

        jLabel78.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel78.setText("Well ID");

        jLabel79.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel79.setText("Company");

        jLabel80.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel80.setText("Conductor Casing Depth (m)");

        jLabel81.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel81.setText("Elevation");

        jLabel82.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel82.setText("Core Point Depth (m)");

        jLabel83.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel83.setText("Elevation");

        jLabel84.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel84.setText("Ground Elevation");

        jLabel85.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel85.setText("Remarks");

        lblViewDrillProgramPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewDrillProgramPDF.setText("jLabel86");

        lblViewLeasePDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewLeasePDF.setText("jLabel87");

        lblViewWellIDPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewWellIDPDF.setText("jLabel88");

        lblViewCompanyPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewCompanyPDF.setText("jLabel89");

        lblViewConductorCasingDepthPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewConductorCasingDepthPDF.setText("jLabel90");

        lblViewConductorCasingElevationPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewConductorCasingElevationPDF.setText("jLabel91");

        lblViewCorePointDepthPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewCorePointDepthPDF.setText("jLabel92");

        lblViewCorePointElevationPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewCorePointElevationPDF.setText("jLabel93");

        lblViewGroundElevationPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewGroundElevationPDF.setText("jLabel94");

        jLabel95.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel95.setText("Drilling Comapny");

        jLabel96.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel96.setText("Rig Unit No.");

        jLabel97.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel97.setText("Well Spud Date");

        jLabel99.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel99.setText("Time");

        jLabel98.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel98.setText("Well Completion Date");

        jLabel100.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel100.setText("Time");

        jLabel101.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel101.setText("Well TD (m)");

        jLabel102.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel102.setText("Total Percentage Recovery (%)");

        lblViewDrillingCompanyPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewDrillingCompanyPDF.setText("jLabel103");

        lblViewRigUnitNumberPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewRigUnitNumberPDF.setText("jLabel104");

        lblViewWellSpudDatePDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewWellSpudDatePDF.setText("jLabel105");

        lblViewWellSpudTimePDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewWellSpudTimePDF.setText("jLabel106");

        lblViewWellCompletionDatePDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewWellCompletionDatePDF.setText("jLabel107");

        lblViewWellCompletionTimePDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewWellCompletionTimePDF.setText("jLabel108");

        lblViewWellTDPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewWellTDPDF.setText("jLabel109");

        lblViewTotalPercentageRecoveryPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblViewTotalPercentageRecoveryPDF.setText("jLabel110");

        txtViewRemarkPDF.setColumns(20);
        txtViewRemarkPDF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        txtViewRemarkPDF.setRows(5);
        jScrollPane5.setViewportView(txtViewRemarkPDF);

        tblPDFTable.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        tblPDFTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Time", "Run", "Tube No.", "Tube No.", "Cored From (depth m)", "Cored To (depth m)", "Meters Cored (cut)", "Kelly Down", "Core Run Recoveryl", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPDFTable.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        tblPDFTable.setPreferredSize(new java.awt.Dimension(2000, 64));
        tblPDFTable.setRowHeight(32);
        tblPDFTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(tblPDFTable);
        if (tblPDFTable.getColumnModel().getColumnCount() > 0) {
            tblPDFTable.getColumnModel().getColumn(0).setResizable(false);
            tblPDFTable.getColumnModel().getColumn(1).setResizable(false);
            tblPDFTable.getColumnModel().getColumn(2).setResizable(false);
            tblPDFTable.getColumnModel().getColumn(3).setResizable(false);
            tblPDFTable.getColumnModel().getColumn(4).setResizable(false);
            tblPDFTable.getColumnModel().getColumn(5).setResizable(false);
            tblPDFTable.getColumnModel().getColumn(6).setResizable(false);
            tblPDFTable.getColumnModel().getColumn(7).setResizable(false);
            tblPDFTable.getColumnModel().getColumn(8).setResizable(false);
        }

        jLabel17.setText("jLabel17");

        javax.swing.GroupLayout PDFLayout = new javax.swing.GroupLayout(PDF);
        PDF.setLayout(PDFLayout);
        PDFLayout.setHorizontalGroup(
            PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PDFLayout.createSequentialGroup()
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PDFLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel62)
                            .addGroup(PDFLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel76)
                                    .addComponent(jLabel75)
                                    .addComponent(jLabel77)
                                    .addComponent(jLabel78)
                                    .addComponent(jLabel79)
                                    .addComponent(jLabel80)
                                    .addComponent(jLabel81)
                                    .addComponent(jLabel82)
                                    .addComponent(jLabel83)
                                    .addComponent(jLabel84)
                                    .addComponent(jLabel85))))
                        .addGap(18, 18, 18)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PDFLayout.createSequentialGroup()
                                .addComponent(jLabel65)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel74))
                            .addGroup(PDFLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane5)
                                    .addGroup(PDFLayout.createSequentialGroup()
                                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblViewGroundElevationPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewConductorCasingDepthPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewConductorCasingElevationPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewCorePointDepthPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewCorePointElevationPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewDrillProgramPDF, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                                            .addComponent(lblViewLeasePDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewWellIDPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewCompanyPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(220, 220, 220)
                                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel95)
                                            .addComponent(jLabel96)
                                            .addComponent(jLabel97)
                                            .addComponent(jLabel99)
                                            .addComponent(jLabel98)
                                            .addComponent(jLabel100)
                                            .addComponent(jLabel101)
                                            .addComponent(jLabel102))
                                        .addGap(111, 111, 111)
                                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblViewDrillingCompanyPDF, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                            .addComponent(lblViewRigUnitNumberPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewWellSpudDatePDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewWellSpudTimePDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewWellCompletionDatePDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewWellCompletionTimePDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewWellTDPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblViewTotalPercentageRecoveryPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                            .addGroup(PDFLayout.createSequentialGroup()
                                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(PDFLayout.createSequentialGroup()
                                        .addComponent(jLabel66)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel70)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel71)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel72))
                                    .addGroup(PDFLayout.createSequentialGroup()
                                        .addComponent(jLabel64)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel73))
                                    .addGroup(PDFLayout.createSequentialGroup()
                                        .addComponent(jLabel63)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel67)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel68)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel69)))
                                .addGap(75, 75, 75)
                                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(PDFLayout.createSequentialGroup()
                        .addGap(250, 250, 250)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 2049, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(251, Short.MAX_VALUE))
        );
        PDFLayout.setVerticalGroup(
            PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PDFLayout.createSequentialGroup()
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PDFLayout.createSequentialGroup()
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PDFLayout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel63)
                                    .addComponent(jLabel67)
                                    .addComponent(jLabel68)
                                    .addComponent(jLabel69))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel66)
                                    .addComponent(jLabel70)
                                    .addComponent(jLabel71)
                                    .addComponent(jLabel72))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel64)
                                    .addComponent(jLabel73)))
                            .addGroup(PDFLayout.createSequentialGroup()
                                .addGap(53, 53, 53)
                                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(123, 123, 123)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel65)
                            .addComponent(jLabel74)))
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addComponent(jLabel75)
                .addGap(70, 70, 70)
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76)
                    .addComponent(lblViewDrillProgramPDF)
                    .addComponent(jLabel95)
                    .addComponent(lblViewDrillingCompanyPDF))
                .addGap(50, 50, 50)
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(lblViewLeasePDF)
                    .addComponent(jLabel96)
                    .addComponent(lblViewRigUnitNumberPDF))
                .addGap(50, 50, 50)
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(lblViewWellIDPDF)
                    .addComponent(jLabel97)
                    .addComponent(lblViewWellSpudDatePDF))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel99)
                    .addComponent(lblViewWellSpudTimePDF))
                .addGap(1, 1, 1)
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel79)
                    .addComponent(lblViewCompanyPDF))
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PDFLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel98)
                            .addComponent(lblViewWellCompletionDatePDF))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel100)
                            .addComponent(lblViewWellCompletionTimePDF))
                        .addGap(50, 50, 50)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel101)
                            .addComponent(lblViewWellTDPDF)))
                    .addGroup(PDFLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel80)
                            .addComponent(lblViewConductorCasingDepthPDF))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel81)
                            .addComponent(lblViewConductorCasingElevationPDF))))
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PDFLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblViewCorePointDepthPDF))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblViewCorePointElevationPDF)
                            .addComponent(jLabel83)))
                    .addGroup(PDFLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel102)
                            .addComponent(lblViewTotalPercentageRecoveryPDF))))
                .addGap(74, 74, 74)
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84)
                    .addComponent(lblViewGroundElevationPDF))
                .addGap(50, 50, 50)
                .addGroup(PDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel85)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1347, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1750, 990));
        setSize(new java.awt.Dimension(0, 0));

        jScrollPane1.setBorder(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1350, 985));

        ParentPanel.setMinimumSize(new java.awt.Dimension(21, 21));
        ParentPanel.setPreferredSize(new java.awt.Dimension(1350, 1300));
        ParentPanel.setLayout(new java.awt.CardLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel1.setText("Details");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Drill Program");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Lease");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Well ID");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Company ");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Conductor Casing Depth");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Core Point Depth");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Ground Elevation");

        txtAddDrillProgram.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtAddLease.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtAddWellID.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtAddCompany.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtAddConductorCasingDepth.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtAddCorePointDepth.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAddCorePointDepth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddCorePointDepthActionPerformed(evt);
            }
        });

        txtAddGroundElevation.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Drilling Company");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("Rig Unit No.");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Well Spud Date ");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setText("Well Competion Date");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setText("Well TD (m)");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setText("Remarks");

        txtAddDrillingCompany.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtAddRigUnitNumber.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAddRigUnitNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddRigUnitNumberActionPerformed(evt);
            }
        });

        txtAddWellSpudDate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAddWellSpudDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddWellSpudDateActionPerformed(evt);
            }
        });

        txtAddWellCompletionDate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtAddWellTD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtAddRemarks.setColumns(20);
        txtAddRemarks.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAddRemarks.setRows(5);
        jScrollPane3.setViewportView(txtAddRemarks);

        btnAddLogo.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnAddLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_upload (1).png"))); // NOI18N
        btnAddLogo.setBorderPainted(false);
        btnAddLogo.setContentAreaFilled(false);
        btnAddLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLogoActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setText("Upload your Logo");

        btnSave.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_save.png"))); // NOI18N
        btnSave.setBorderPainted(false);
        btnSave.setContentAreaFilled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancelAddCoreSheet.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnCancelAddCoreSheet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_cancel.png"))); // NOI18N
        btnCancelAddCoreSheet.setBorderPainted(false);
        btnCancelAddCoreSheet.setContentAreaFilled(false);
        btnCancelAddCoreSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelAddCoreSheetActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setText("Time");

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setText("Time");

        txtAddWellSpudTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtAddWellCompletionTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout AddCoreSheetLayout = new javax.swing.GroupLayout(AddCoreSheet);
        AddCoreSheet.setLayout(AddCoreSheetLayout);
        AddCoreSheetLayout.setHorizontalGroup(
            AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddCoreSheetLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel15)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12)
                    .addComponent(jLabel1))
                .addGap(187, 187, 187)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddDrillingCompany, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddGroundElevation, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddCorePointDepth, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddConductorCasingDepth, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddCompany, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddWellID, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddDrillProgram, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddLease, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, AddCoreSheetLayout.createSequentialGroup()
                            .addComponent(txtAddWellCompletionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19)
                            .addGap(18, 18, 18)
                            .addComponent(txtAddWellCompletionTime, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AddCoreSheetLayout.createSequentialGroup()
                            .addComponent(txtAddWellSpudDate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20)
                            .addGap(18, 18, 18)
                            .addComponent(txtAddWellSpudTime, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtAddRigUnitNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtAddWellTD, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(AddCoreSheetLayout.createSequentialGroup()
                        .addComponent(btnAddLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelAddCoreSheet, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(588, Short.MAX_VALUE))
        );
        AddCoreSheetLayout.setVerticalGroup(
            AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddCoreSheetLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtAddDrillProgram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddCoreSheetLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(txtAddLease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtAddWellID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtAddCompany, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtAddConductorCasingDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(txtAddCorePointDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtAddGroundElevation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtAddDrillingCompany, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtAddRigUnitNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAddWellSpudTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20)
                        .addComponent(txtAddWellSpudDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(AddCoreSheetLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel11)))
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddCoreSheetLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel12))
                    .addGroup(AddCoreSheetLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(txtAddWellCompletionTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(AddCoreSheetLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtAddWellCompletionDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtAddWellTD, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(AddCoreSheetLayout.createSequentialGroup()
                        .addGroup(AddCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSave)
                            .addComponent(btnCancelAddCoreSheet)
                            .addComponent(btnAddLogo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(95, Short.MAX_VALUE))
        );

        txtAddDrillProgram.getAccessibleContext().setAccessibleName("txtDrillingProgram");
        jLabel14.getAccessibleContext().setAccessibleName("jLabel14");
        jLabel14.getAccessibleContext().setAccessibleDescription("");

        ParentPanel.add(AddCoreSheet, "card2");

        ViewCoreSheet.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ViewCoreSheet.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(250, 100));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        ViewCoreSheet.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1313, 110));

        lblViewDrillProgram.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewDrillProgram.setText("jLabel21");
        ViewCoreSheet.add(lblViewDrillProgram, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 193, -1, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setText("Drill Program");
        ViewCoreSheet.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 193, -1, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel22.setText("Lease");
        ViewCoreSheet.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 233, -1, -1));

        lblViewLease.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewLease.setText("jLabel23");
        ViewCoreSheet.add(lblViewLease, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 233, -1, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel23.setText("Well ID");
        ViewCoreSheet.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 277, -1, -1));

        lblViewWellID.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewWellID.setText("jLabel24");
        ViewCoreSheet.add(lblViewWellID, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 277, -1, -1));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel24.setText("Company");
        ViewCoreSheet.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 317, -1, -1));

        lblViewCompany.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewCompany.setText("jLabel25");
        ViewCoreSheet.add(lblViewCompany, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 317, -1, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel25.setText("Conductor Casing Depth");
        ViewCoreSheet.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 357, -1, -1));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setText("Elevation");
        ViewCoreSheet.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 388, -1, -1));

        lblViewConductorCasingDepth.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewConductorCasingDepth.setText("jLabel27");
        ViewCoreSheet.add(lblViewConductorCasingDepth, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 357, -1, -1));

        lblViewConductorCasingElevation.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewConductorCasingElevation.setText("jLabel27");
        ViewCoreSheet.add(lblViewConductorCasingElevation, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 388, -1, -1));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setText("Core Point Depth");
        ViewCoreSheet.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 428, -1, -1));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel28.setText("Elevation");
        ViewCoreSheet.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 459, -1, -1));

        lblViewCorePointDepth.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewCorePointDepth.setText("jLabel29");
        ViewCoreSheet.add(lblViewCorePointDepth, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 428, -1, -1));

        lblViewCorePointElevation.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewCorePointElevation.setText("jLabel29");
        ViewCoreSheet.add(lblViewCorePointElevation, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 459, -1, -1));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setText("Ground Elevation");
        ViewCoreSheet.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 499, -1, -1));

        lblViewGroundElevation.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewGroundElevation.setText("jLabel30");
        ViewCoreSheet.add(lblViewGroundElevation, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 499, -1, -1));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setText("Drilling Company");
        ViewCoreSheet.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(631, 193, -1, -1));

        lblViewDrillingCompany.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewDrillingCompany.setText("jLabel31");
        ViewCoreSheet.add(lblViewDrillingCompany, new org.netbeans.lib.awtextra.AbsoluteConstraints(935, 193, -1, -1));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel31.setText("Rig Unit No.");
        ViewCoreSheet.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(631, 233, -1, -1));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setText("Well Spud Date");
        ViewCoreSheet.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(631, 277, -1, -1));

        lblViewRigUnitNumber.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewRigUnitNumber.setText("jLabel33");
        ViewCoreSheet.add(lblViewRigUnitNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(935, 233, -1, -1));

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel34.setText("Time");
        ViewCoreSheet.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(631, 308, -1, -1));

        lblViewWellSpudDate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewWellSpudDate.setText("JLabel34");
        ViewCoreSheet.add(lblViewWellSpudDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(935, 277, -1, -1));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel36.setText("Well Completion Date");
        ViewCoreSheet.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(631, 355, -1, -1));

        lblViewWellSpudTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewWellSpudTime.setText("jLabel35");
        ViewCoreSheet.add(lblViewWellSpudTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(935, 308, -1, -1));

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel38.setText("Time");
        ViewCoreSheet.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(631, 379, -1, -1));

        lblViewWellCompletionDate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewWellCompletionDate.setText("jLabel36");
        ViewCoreSheet.add(lblViewWellCompletionDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(935, 348, -1, -1));

        lblViewWellCompletionTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewWellCompletionTime.setText("jLabel37");
        ViewCoreSheet.add(lblViewWellCompletionTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(935, 381, -1, -1));

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel41.setText("Well TD (m)");
        ViewCoreSheet.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(631, 420, -1, -1));

        lblViewWellTD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewWellTD.setText("jLabel38");
        ViewCoreSheet.add(lblViewWellTD, new org.netbeans.lib.awtextra.AbsoluteConstraints(935, 420, -1, -1));

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel43.setText("Total Percentage Recovery (%)");
        ViewCoreSheet.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(631, 464, -1, -1));

        lblViewTotalPercentageRecovery.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblViewTotalPercentageRecovery.setText("jLabel39");
        ViewCoreSheet.add(lblViewTotalPercentageRecovery, new org.netbeans.lib.awtextra.AbsoluteConstraints(935, 464, -1, -1));

        Remarks.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Remarks.setText("Ground Elevation");
        ViewCoreSheet.add(Remarks, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 571, -1, -1));

        tblTestRuns.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tblTestRuns.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Run", "Time", "Tube No.", "Tube No.", "Cored From", "Cored To", "Meters Cored", "Kelly Down", "Meters Recovered", "Description ", "Id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true, true, false, false, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTestRuns.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblTestRuns.setGridColor(new java.awt.Color(240, 240, 240));
        tblTestRuns.setRowHeight(20);
        tblTestRuns.setSelectionBackground(new java.awt.Color(102, 204, 0));
        tblTestRuns.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTestRuns.setShowHorizontalLines(false);
        tblTestRuns.setShowVerticalLines(false);
        tblTestRuns.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblTestRuns);
        if (tblTestRuns.getColumnModel().getColumnCount() > 0) {
            tblTestRuns.getColumnModel().getColumn(10).setResizable(false);
            tblTestRuns.getColumnModel().getColumn(10).setPreferredWidth(0);
        }

        ViewCoreSheet.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 876, 1269, 389));

        btnAddTestRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_add-test-run (1).png"))); // NOI18N
        btnAddTestRun.setBorderPainted(false);
        btnAddTestRun.setContentAreaFilled(false);
        btnAddTestRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTestRunActionPerformed(evt);
            }
        });
        ViewCoreSheet.add(btnAddTestRun, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 804, 150, -1));

        btnDeleteTestRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_delete-test-run.png"))); // NOI18N
        btnDeleteTestRun.setBorderPainted(false);
        btnDeleteTestRun.setContentAreaFilled(false);
        btnDeleteTestRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteTestRunActionPerformed(evt);
            }
        });
        ViewCoreSheet.add(btnDeleteTestRun, new org.netbeans.lib.awtextra.AbsoluteConstraints(362, 804, 151, -1));

        txtEditTestRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_edit-test-run.png"))); // NOI18N
        txtEditTestRun.setToolTipText("");
        txtEditTestRun.setBorderPainted(false);
        txtEditTestRun.setContentAreaFilled(false);
        txtEditTestRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEditTestRunActionPerformed(evt);
            }
        });
        ViewCoreSheet.add(txtEditTestRun, new org.netbeans.lib.awtextra.AbsoluteConstraints(197, 804, 150, -1));

        btnEditCoreSheet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_edit-settings.png"))); // NOI18N
        btnEditCoreSheet.setBorderPainted(false);
        btnEditCoreSheet.setContentAreaFilled(false);
        btnEditCoreSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditCoreSheetActionPerformed(evt);
            }
        });
        ViewCoreSheet.add(btnEditCoreSheet, new org.netbeans.lib.awtextra.AbsoluteConstraints(152, 128, 172, -1));

        btnDeleteCoreSheet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_delete.png"))); // NOI18N
        btnDeleteCoreSheet.setBorderPainted(false);
        btnDeleteCoreSheet.setContentAreaFilled(false);
        btnDeleteCoreSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCoreSheetActionPerformed(evt);
            }
        });
        ViewCoreSheet.add(btnDeleteCoreSheet, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 130, 114, -1));

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_print.png"))); // NOI18N
        btnPrint.setBorderPainted(false);
        btnPrint.setContentAreaFilled(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        ViewCoreSheet.add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 804, 108, -1));

        btnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_export .png"))); // NOI18N
        btnExport.setBorderPainted(false);
        btnExport.setContentAreaFilled(false);
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        ViewCoreSheet.add(btnExport, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 128, 120, -1));

        txtViewRemark.setColumns(20);
        txtViewRemark.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtViewRemark.setRows(5);
        jScrollPane6.setViewportView(txtViewRemark);

        ViewCoreSheet.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 580, 362, -1));

        ParentPanel.add(ViewCoreSheet, "card3");

        HomeScreen.setForeground(new java.awt.Color(255, 255, 255));

        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/no logs (1).png"))); // NOI18N

        javax.swing.GroupLayout HomeScreenLayout = new javax.swing.GroupLayout(HomeScreen);
        HomeScreen.setLayout(HomeScreenLayout);
        HomeScreenLayout.setHorizontalGroup(
            HomeScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomeScreenLayout.createSequentialGroup()
                .addGap(423, 423, 423)
                .addComponent(jLabel61)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        HomeScreenLayout.setVerticalGroup(
            HomeScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomeScreenLayout.createSequentialGroup()
                .addGap(311, 311, 311)
                .addComponent(jLabel61)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ParentPanel.add(HomeScreen, "card5");

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel33.setText("Edit Details");

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel35.setText("Drill Program");

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setText("Lease");

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel39.setText("Well ID");

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel40.setText("Company ");

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel42.setText("Conductor Casing Depth");

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel44.setText("Core Point Depth");

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel45.setText("Ground Elevation");

        txtEditDrillProgram.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditLease.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditWellID.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditCompany.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditConductorCasingDepth.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditCorePointDepth.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditGroundElevation.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel46.setText("Drilling Company");

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel47.setText("Rig Unit No.");

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel48.setText("Well Spud Date ");

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel49.setText("Well Competion Date");

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel50.setText("Well TD (m)");

        jLabel52.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel52.setText("Remarks");

        txtEditDrillingCompany.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditRigUnitNumber.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEditRigUnitNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEditRigUnitNumberActionPerformed(evt);
            }
        });

        txtEditWellSpudDate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEditWellSpudDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEditWellSpudDateActionPerformed(evt);
            }
        });

        txtEditWellCompletionDate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditWellTD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnEditUploadImage.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnEditUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_upload (1).png"))); // NOI18N
        btnEditUploadImage.setBorderPainted(false);
        btnEditUploadImage.setContentAreaFilled(false);

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel55.setText("Upload your Logo");

        btnEditSave.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnEditSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_save-changes.png"))); // NOI18N
        btnEditSave.setBorderPainted(false);
        btnEditSave.setContentAreaFilled(false);
        btnEditSave.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnEditSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSaveActionPerformed(evt);
            }
        });

        btnEditCancel.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnEditCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_cancel.png"))); // NOI18N
        btnEditCancel.setBorderPainted(false);
        btnEditCancel.setContentAreaFilled(false);
        btnEditCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditCancelActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel56.setText("Time");

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel57.setText("Time");

        txtEditWellSpudTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditWellCompletionTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEditRemarks.setColumns(20);
        txtEditRemarks.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEditRemarks.setRows(5);
        jScrollPane4.setViewportView(txtEditRemarks);

        jLabel16.setText("jLabel16");

        javax.swing.GroupLayout EditCoreSheetLayout = new javax.swing.GroupLayout(EditCoreSheet);
        EditCoreSheet.setLayout(EditCoreSheetLayout);
        EditCoreSheetLayout.setHorizontalGroup(
            EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditCoreSheetLayout.createSequentialGroup()
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EditCoreSheetLayout.createSequentialGroup()
                        .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(EditCoreSheetLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel47)
                                    .addComponent(jLabel48)
                                    .addComponent(jLabel52)
                                    .addComponent(jLabel42)
                                    .addComponent(jLabel44)
                                    .addComponent(jLabel37)
                                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel40)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel33)
                                    .addComponent(jLabel49)
                                    .addGroup(EditCoreSheetLayout.createSequentialGroup()
                                        .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel46)
                                            .addComponent(jLabel45)
                                            .addComponent(jLabel50))
                                        .addGap(244, 244, 244)
                                        .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtEditWellTD, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(txtEditDrillingCompany, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditCoreSheetLayout.createSequentialGroup()
                                                        .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(txtEditWellCompletionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(txtEditWellSpudDate, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.TRAILING))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(txtEditWellCompletionTime, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(txtEditWellSpudTime, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addComponent(txtEditRigUnitNumber, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(txtEditCorePointDepth, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtEditGroundElevation, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(17, 17, 17))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, EditCoreSheetLayout.createSequentialGroup()
                                .addGap(397, 397, 397)
                                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEditLease, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEditWellID, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEditCompany, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEditConductorCasingDepth, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEditDrillProgram, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditCoreSheetLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 799, Short.MAX_VALUE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(EditCoreSheetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnEditSave, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        EditCoreSheetLayout.setVerticalGroup(
            EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditCoreSheetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(txtEditDrillProgram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(txtEditLease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(txtEditWellID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(txtEditCompany, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(txtEditConductorCasingDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(txtEditCorePointDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(txtEditGroundElevation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(txtEditDrillingCompany, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(txtEditRigUnitNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(txtEditWellSpudDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56)
                    .addComponent(txtEditWellSpudTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(txtEditWellCompletionDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57)
                    .addComponent(txtEditWellCompletionTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(txtEditWellTD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52))
                .addGap(18, 18, 18)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(EditCoreSheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEditSave)
                    .addComponent(btnEditCancel)
                    .addComponent(btnEditUploadImage))
                .addContainerGap())
        );

        ParentPanel.add(EditCoreSheet, "card2");

        LabelTop.setPreferredSize(new java.awt.Dimension(385, 222));

        lblTopWellID.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTopWellID.setText("jLabel86");

        lblTopCoreFrom.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblTopCoreFrom.setText("jLabel87");

        lblTopCoreTo.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblTopCoreTo.setText("jLabel89");

        lblTopRun.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblTopRun.setText("jLabel90");

        lblTopTubeNo.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblTopTubeNo.setText("jLabel91");

        IconTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/Top.png"))); // NOI18N
        IconTop.setToolTipText("");

        javax.swing.GroupLayout LabelTopLayout = new javax.swing.GroupLayout(LabelTop);
        LabelTop.setLayout(LabelTopLayout);
        LabelTopLayout.setHorizontalGroup(
            LabelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LabelTopLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(LabelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTopWellID, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(LabelTopLayout.createSequentialGroup()
                        .addGroup(LabelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(LabelTopLayout.createSequentialGroup()
                                .addComponent(lblTopRun)
                                .addGap(18, 18, 18)
                                .addComponent(lblTopTubeNo))
                            .addGroup(LabelTopLayout.createSequentialGroup()
                                .addComponent(lblTopCoreFrom)
                                .addGap(18, 18, 18)
                                .addComponent(lblTopCoreTo)))
                        .addGap(75, 75, 75)
                        .addComponent(IconTop)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LabelTopLayout.setVerticalGroup(
            LabelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LabelTopLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTopWellID)
                .addGap(21, 21, 21)
                .addGroup(LabelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(IconTop)
                    .addGroup(LabelTopLayout.createSequentialGroup()
                        .addGroup(LabelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTopCoreFrom)
                            .addComponent(lblTopCoreTo))
                        .addGap(21, 21, 21)
                        .addGroup(LabelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTopRun)
                            .addComponent(lblTopTubeNo))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(11, 11, 11))
        );

        ParentPanel.add(LabelTop, "card7");

        LabelBottom.setPreferredSize(new java.awt.Dimension(385, 222));

        lblBottomWellID.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblBottomWellID.setText("jLabel86");

        lblBottomCoreFrom.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblBottomCoreFrom.setText("jLabel87");

        lblBottomCoreTo.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblBottomCoreTo.setText("jLabel89");

        lblBottomRun.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblBottomRun.setText("jLabel90");

        lblBottomTubeNo.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblBottomTubeNo.setText("jLabel91");

        iconBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/New Bitmap Image (2).png"))); // NOI18N

        javax.swing.GroupLayout LabelBottomLayout = new javax.swing.GroupLayout(LabelBottom);
        LabelBottom.setLayout(LabelBottomLayout);
        LabelBottomLayout.setHorizontalGroup(
            LabelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LabelBottomLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(iconBottom)
                .addGap(18, 18, 18)
                .addGroup(LabelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBottomWellID, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(LabelBottomLayout.createSequentialGroup()
                        .addComponent(lblBottomRun)
                        .addGap(18, 18, 18)
                        .addComponent(lblBottomTubeNo))
                    .addGroup(LabelBottomLayout.createSequentialGroup()
                        .addComponent(lblBottomCoreFrom)
                        .addGap(18, 18, 18)
                        .addComponent(lblBottomCoreTo)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LabelBottomLayout.setVerticalGroup(
            LabelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LabelBottomLayout.createSequentialGroup()
                .addGroup(LabelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LabelBottomLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblBottomWellID)
                        .addGap(21, 21, 21)
                        .addGroup(LabelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblBottomCoreFrom)
                            .addComponent(lblBottomCoreTo))
                        .addGap(21, 21, 21)
                        .addGroup(LabelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblBottomRun)
                            .addComponent(lblBottomTubeNo)))
                    .addGroup(LabelBottomLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(iconBottom)))
                .addContainerGap())
        );

        ParentPanel.add(LabelBottom, "card7");

        jScrollPane1.setViewportView(ParentPanel);

        jPanel3.setMinimumSize(new java.awt.Dimension(400, 22));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/crossBorders_logo_onwhite-01.png"))); // NOI18N
        jPanel3.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, -20, -1, -1));

        btnAddCoreSheet.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        btnAddCoreSheet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_add-coresheet.png"))); // NOI18N
        btnAddCoreSheet.setBorder(null);
        btnAddCoreSheet.setBorderPainted(false);
        btnAddCoreSheet.setContentAreaFilled(false);
        btnAddCoreSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCoreSheetActionPerformed(evt);
            }
        });
        jPanel3.add(btnAddCoreSheet, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 320, 50));

        txtSearchForCoreSheet.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSearchForCoreSheet.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSearchForCoreSheet.setText("Search");
        txtSearchForCoreSheet.setBorder(null);
        txtSearchForCoreSheet.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSearchForCoreSheet.setOpaque(false);
        txtSearchForCoreSheet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchForCoreSheetMouseClicked(evt);
            }
        });
        jPanel3.add(txtSearchForCoreSheet, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 230, 50));

        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/button_Search-Bar.png"))); // NOI18N
        jPanel3.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 310, -1));

        lstCoreSheets.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 0));
        lstCoreSheets.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lstCoreSheets.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lstCoreSheets.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        lstCoreSheets.setSelectionBackground(new java.awt.Color(102, 204, 0));
        scrollCoreSheets.setViewportView(lstCoreSheets);

        jPanel3.add(scrollCoreSheets, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 270, 301, 630));

        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/label/generator/Images/yWtT5J.jpg"))); // NOI18N
        jPanel3.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -110, 400, 1090));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1399, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 963, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jScrollPane1.getVerticalScrollBar().setUnitIncrement(20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static int GetParentCoreSheet(int CoreRunID){
        
        int ParentCoreSheet = 0;
        
        for (int i = 0; i < TestRun.size(); i++) {
            if (((TestRun)TestRun.Getelementbypos(i)).GetID() == CoreRunID){
                ParentCoreSheet = ((TestRun)TestRun.Getelementbypos(i)).getParentCoreSheet();
            }
        }
        
        return ParentCoreSheet;
      
    }
    
    public static int GetCoreRunPosition(int CoreRunID){
        
        int Position = 0;
        
        for (int i = 0; i < TestRun.size(); i++) {
            if (((TestRun)TestRun.Getelementbypos(i)).GetID() == CoreRunID){
              Position = i;  
            }
        }
        
        return Position;
    }
    
    public static int GetRun(int ParentSheet){
        
        int NewRun = 0;
        
        for (int i = 0; i < TestRun.size(); i++) {
            if (((TestRun)TestRun.Getelementbypos(i)).getParentCoreSheet() == ParentSheet && ((TestRun)TestRun.Getelementbypos(i)).GetRun() > NewRun){
                NewRun = ((TestRun)TestRun.Getelementbypos(i)).GetRun();
            }
        }
        
        NewRun++;
        
        return NewRun;
      
    }
    
    public static int GetTubeNumbers(int ParentSheet){
        
        int NewTubeNumber = 0;
        
        for (int i = 0; i < TestRun.size(); i++) {
            if (((TestRun)TestRun.Getelementbypos(i)).getParentCoreSheet() == ParentSheet && ((TestRun)TestRun.Getelementbypos(i)).GetTubeNumber2() > NewTubeNumber){
                NewTubeNumber = ((TestRun)TestRun.Getelementbypos(i)).GetTubeNumber2();
            }
        }
        
        NewTubeNumber++;
        
        return NewTubeNumber;
    }
    
    public static void GetTotalPercentageRecovery(int ParentSheet) throws ClassNotFoundException, SQLException{
        double TotalPercentageRecovery = 0;
        double MetersRecovered = 0;
        double MetersCored = 0;
        String sql = null;
        boolean NoCoreRuns = true;
        
        for (int i = 0; i < TestRun.size(); i++) {
              if (((TestRun)TestRun.Getelementbypos(i)).getParentCoreSheet() == ParentSheet){
                MetersRecovered = MetersRecovered + ((TestRun)TestRun.Getelementbypos(i)).getMetersRecovered();
                MetersCored = MetersCored + ((TestRun)TestRun.Getelementbypos(i)).GetMetersCored();
                NoCoreRuns = false;
                  System.out.println(MetersRecovered);
                  System.out.println(MetersCored);
            }
        }
        
        TotalPercentageRecovery = MetersRecovered/MetersCored;
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
        Statement statement = con.createStatement();
        
        
        if (NoCoreRuns == false){
            sql = "update Coresheet set totalpercentagerecovery = "+Double.valueOf(twoDForm.format(TotalPercentageRecovery))+" where id = "+ParentSheet+""; 
        }
        else {
            sql = "update Coresheet set totalpercentagerecovery = "+0+" where id = "+ParentSheet+""; 
        }

        statement.executeUpdate(sql);
        statement.close();
        con.close();

        PopulateArray();
        populateTable();
        
        lblViewTotalPercentageRecovery.setText(((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).GetTotalPercentRecovery().toString());
        
    }
    
    public static double GetCoreFromDepth(int ParentSheet){
        
        int run = GetRun(ParentSheet);
        double CoreFromDepth = 0;
        
        if(run == 1){
            CoreFromDepth = ((CoreSheet)CoreSheet.Getelementbypos(ParentSheet)).GetCorePointDepth();
        }
        else {
            
            for (int i = 0; i < TestRun.size(); i++) {
                if(((TestRun)TestRun.Getelementbypos(i)).getParentCoreSheet() == ParentSheet){
                   CoreFromDepth = ((TestRun)TestRun.Getelementbypos(i)).GetCoreToDepth();
                }
            }
        }
        
        return CoreFromDepth;
    }
    
    public static void PrintTestRun(){
        DefaultTableModel model = (DefaultTableModel) tblTestRuns.getModel();
        int index = (int)tblTestRuns.getSelectedRow();
        int run = (int)model.getValueAt(index, 10);
        
        System.out.println(run);
        
    }
    
    public static void SaveButtonClicked() throws ClassNotFoundException, SQLException{
            
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
        Statement statement = con.createStatement();
        
        double ConductorCasingElevation = Double.parseDouble(txtAddGroundElevation.getText()) - Double.parseDouble(txtAddConductorCasingDepth.getText());
        double CorePointElevation = Double.parseDouble(txtAddGroundElevation.getText()) - Double.parseDouble(txtAddCorePointDepth.getText());
        
        String sql = "INSERT INTO coresheet (ID, DrillProgram, Lease, WellID, Company, ConductorCasingDepth, ConductorCasingElevation, CorePointDepth, CorePointElevation, GroundElevation, DrillingCompany, RigUnitNumber, WellSpudDate, WellSpudTime, WellCompletionDate, WellCompletionTime, WellTD, TotalPercentageRecovery, Remarks) " +
        "VALUES ("+GenerateID("coresheet")+", '"+txtAddDrillProgram.getText()+"', '"+txtAddLease.getText()+"', '"+txtAddWellID.getText()+"', '"+txtAddCompany.getText()+"', "+Double.parseDouble(txtAddConductorCasingDepth.getText())+", '"+CorePointElevation+"', '"+Double.parseDouble(txtAddCorePointDepth.getText())+"', '"+CorePointElevation+"', '"+Double.parseDouble(txtAddGroundElevation.getText())+"', '"+txtAddDrillingCompany.getText()+"', '"+txtAddRigUnitNumber.getText()+"', '"+txtAddWellSpudDate.getText()+"', '"+txtAddWellSpudTime.getText()+"', '"+txtAddWellCompletionDate.getText()+"', '"+txtAddWellCompletionTime.getText()+"', '"+txtAddWellTD.getText()+"', "+0+", '"+txtAddRemarks.getText()+"' );"; 
        
        statement.executeUpdate(sql);
        statement.close();
        con.close();
        
        PopulateArray();
        PopulateJList();
        
        }
    
    public static void SaveButtonClickedEdit() throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
        Statement statement = con.createStatement();
        
        int id = ((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getAnchorSelectionIndex())).getID();
        
        String sql = "update coresheet set drillprogram = '"+txtEditDrillProgram.getText()+"', lease = '"+txtEditLease.getText()+"', wellid = '"+txtEditWellID.getText()+"', company = '"+txtEditCompany.getText()+"', conductorcasingdepth = "+Double.parseDouble(txtEditConductorCasingDepth.getText())+", corepointdepth = "+Double.parseDouble(txtEditCorePointDepth.getText())+", groundelevation = "+Double.parseDouble(txtEditGroundElevation.getText())+", drillingcompany = '"+txtEditDrillingCompany.getText()+"', rigunitnumber = '"+txtEditRigUnitNumber.getText()+"', wellspuddate = '"+txtEditWellSpudDate.getText()+"', wellspudtime = '"+txtEditWellSpudTime.getText()+"', wellcompletiondate = '"+txtEditWellCompletionDate.getText()+"', wellcompletiontime = '"+txtEditWellCompletionTime.getText()+"', welltd = '"+txtEditWellTD.getText()+"', remarks = '"+txtEditRemarks.getText()+"'  where id = "+id+"";
        statement.executeUpdate(sql);
        statement.close();
        con.close();
        
        PopulateArray();
    }
    
    public static void Delete() throws ClassNotFoundException, SQLException{
        
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
        Statement statement = con.createStatement();
        
        int CoreSheetToDelete  = ((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).getID();
        
        String sql = "delete from coresheet where id = "+CoreSheetToDelete+"";
        statement.executeUpdate(sql);
        
        sql = "delete from corerun where parentcoresheet = "+CoreSheetToDelete+"";
        statement.executeUpdate(sql);
        
        statement.close();
        con.close();
        
        PopulateArray();
        
    }
    
    public static void PopulateArray() throws ClassNotFoundException, SQLException{
        
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
        Statement statement = con.createStatement();
        
        ResultSet Count = statement.executeQuery("select * from count");
        int NumberOFCoreSheetsToAdd = Count.getInt("CoreSheets");
        int NumberOFCoreRunsToAdd = Count.getInt("TestRuns");

        
        CoreSheet = new ArrayManager(1); 
        
        for (int i = 0; i < NumberOFCoreSheetsToAdd; i++) {
            ResultSet AddCoreSheet = statement.executeQuery("select * from CoreSheet where id = " + i);
            if(AddCoreSheet.next()){
            CoreSheet.add(new CoreSheet(AddCoreSheet.getInt("id"), AddCoreSheet.getString("DrillProgram"), AddCoreSheet.getString("Lease"), AddCoreSheet.getString("WellID"), AddCoreSheet.getString("Company"), AddCoreSheet.getDouble("ConductorCasingDepth"), AddCoreSheet.getDouble("ConductorCasingElevation"), AddCoreSheet.getDouble("CorePointDepth"), AddCoreSheet.getDouble("CorePointElevation"), AddCoreSheet.getDouble("GroundElevation"), AddCoreSheet.getString("DrillingCompany"), AddCoreSheet.getString("RigUnitNumber"), AddCoreSheet.getString("WellSpudDate"), AddCoreSheet.getString("WellSpudTime"), AddCoreSheet.getString("WellCompletionDate"), AddCoreSheet.getString("WellCompletionTime"), AddCoreSheet.getString("WellTD"), AddCoreSheet.getDouble("TotalPercentageRecovery"), AddCoreSheet.getString("Remarks")));
            }
        }
        
        TestRun = new ArrayManager(1);
        
        for (int i = 0; i < NumberOFCoreRunsToAdd; i++) {
            ResultSet AddCoreRun = statement.executeQuery("select * from CoreRun where id = " + i);
            if(AddCoreRun.next()){
            TestRun.add(new TestRun(AddCoreRun.getInt("id"), AddCoreRun.getInt("run"), AddCoreRun.getString("time"), AddCoreRun.getInt("tubenumber1"), AddCoreRun.getInt("tubenumber2"), AddCoreRun.getDouble("corefromdepth"), AddCoreRun.getDouble("coretodepth"), AddCoreRun.getDouble("meterscored"), AddCoreRun.getString("kellydown"), AddCoreRun.getDouble("metersrecovered"), AddCoreRun.getString("description"), AddCoreRun.getInt("ParentCoreSheet")));
            }
        }
        
        statement.close();
        con.close();
        
    }
    
    public static void populateTable(){
        
        int index = lstCoreSheets.getSelectedIndex();
        DefaultTableModel model = (DefaultTableModel) tblTestRuns.getModel();
        Object RowData[] = new Object[11];
        model.getDataVector().removeAllElements();
        
        for (int i = 0; i < TestRun.size(); i++) {
            
            if(((TestRun)TestRun.Getelementbypos(i)).getParentCoreSheet() == ((CoreSheet)CoreSheet.Getelementbypos(index)).getID()){
               
            RowData[0] = ((TestRun)TestRun.Getelementbypos(i)).GetRun();
            RowData[1] = ((TestRun)TestRun.Getelementbypos(i)).GetTime();
            RowData[2] = ((TestRun)TestRun.Getelementbypos(i)).GetTubeNumber1();
            RowData[3] = ((TestRun)TestRun.Getelementbypos(i)).GetTubeNumber2();
            RowData[4] = ((TestRun)TestRun.Getelementbypos(i)).GetCoreFromDepth();
            RowData[5] = ((TestRun)TestRun.Getelementbypos(i)).GetCoreToDepth();
            RowData[6] = ((TestRun)TestRun.Getelementbypos(i)).GetMetersCored();
            RowData[7] = ((TestRun)TestRun.Getelementbypos(i)).GetKD();
            RowData[8] = ((TestRun)TestRun.Getelementbypos(i)).getMetersRecovered();
            RowData[9] = ((TestRun)TestRun.Getelementbypos(i)).GetDescription();
            RowData[10] = ((TestRun)TestRun.Getelementbypos(i)).GetID();
            
            model.addRow(RowData);
            
            }
        }       
    }
    
    public static void PopulateViewSheet(){
        
     int index = lstCoreSheets.getSelectedIndex();
     
     lblViewDrillProgram.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetDrillProgram());
     lblViewLease.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetLease());
     lblViewWellID.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellID());
     lblViewCompany.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetCompany());
     lblViewConductorCasingDepth.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetConductorCasingDepth().toString());
     lblViewConductorCasingElevation.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetConductorCasingElevation().toString());
     lblViewCorePointDepth.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetCorePointDepth().toString());
     lblViewCorePointElevation.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetCorePointElevation().toString());
     lblViewGroundElevation.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetGroundElevation().toString());
     lblViewDrillingCompany.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetDrillingCompany());
     lblViewRigUnitNumber.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetRigUnitNumber());
     lblViewWellSpudDate.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellSpudDate());
     lblViewWellSpudTime.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellSpudTime());
     lblViewWellCompletionDate.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellCompletionDate());
     lblViewWellCompletionTime.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellCompletionTime());
     lblViewWellTD.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellTD());
     lblViewTotalPercentageRecovery.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetTotalPercentRecovery().toString());
     txtViewRemark.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).getRemarks());

    }   
    
    private static void PopulateJList(){ 
        
        String DrillPrograms[] = new String[CoreSheet.size()];
       
        
        for (int i = 0; i < CoreSheet.size(); i++) {
            DrillPrograms[i] = ""+((CoreSheet)CoreSheet.Getelementbypos(i)).GetCompany()+" - "+((CoreSheet)CoreSheet.Getelementbypos(i)).GetWellID()+" - "+((CoreSheet)CoreSheet.Getelementbypos(i)).GetWellCompletionDate()+"";
        }
        
        lstCoreSheets.removeAll();
        lstCoreSheets.setListData(DrillPrograms);
    }
    
    private static void UpdateTablePopulation(String cmd) throws ClassNotFoundException, SQLException{
        
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
        Statement statement = con.createStatement();
        
        ResultSet Count = statement.executeQuery("select * from count");
        int update = 0;
        
        if(cmd == "+CoreSheet"){
            update = Count.getInt("CoreSheets") + 1;
            String sql = "Update count set coresheets = "+update+"";
            statement.executeUpdate(sql);
        }
        else if (cmd == "+CoreRun"){
            update = Count.getInt("TestRuns") + 1;
            String sql = "Update count set testruns = "+update+"";
            statement.executeUpdate(sql);
        }
        else if (cmd == "-CoreRun"){
            update = Count.getInt("TestRuns") - 1;
            String sql = "Update count set testruns = "+update+"";
            statement.executeUpdate(sql);
        }
        
        statement.close();
        con.close();
        
    }    
    
    private void CreatePDF(){
        int run = (int)tblTestRuns.getSelectedRow() + 1;
        int id = 0;
        
//        for (int i = 0; i < 10; i++) {
//             if (((TestRun)TestRun.Getelementbypos(i)).getParentCoreSheet() == ((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).getID() && ((TestRun)TestRun.Getelementbypos(i)).GetRun() == run){
//                 id = ((TestRun)TestRun.Getelementbypos(i)).GetID();
//             }
//        }
        
        lblTopWellID.setText(((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).GetWellID());
        lblTopCoreFrom.setText(""+((TestRun)TestRun.Getelementbypos(0)).GetCoreFromDepth()+"");
        lblTopCoreTo.setText(""+((TestRun)TestRun.Getelementbypos(0)).GetCoreToDepth()+"");
        lblTopRun.setText("Run "+((TestRun)TestRun.Getelementbypos(0)).GetRun()+"");
        lblTopTubeNo.setText("Tube " + ((TestRun)TestRun.Getelementbypos(1)).GetTubeNumber1());
        
        BufferedImage bi = new BufferedImage(PDF.getWidth(), PDF.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        PDF.print(g);
        g.dispose();
        try {
            ImageIO.write(bi, "png", new File("ToplabelTube1.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            java.io.FileOutputStream fout=new java.io.FileOutputStream("ToplabelTube1.png");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Layout.show(ParentPanel, "LabelTop");
        ParentPanel.repaint();
        ParentPanel.revalidate();
       
    }
        
    public static ListSelectionListener listSelectionListener = new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
          
          
        PopulateViewSheet();
        populateTable();
        
        Layout.show(ParentPanel, "ViewCoreSheet");
        ParentPanel.repaint();  
        ParentPanel.revalidate();

      }
    };
    
    public void ClearTextBoxes(){
        
        txtAddWellID.setText("");
        txtAddConductorCasingDepth.setText("");
        txtAddCorePointDepth.setText("");
        txtAddGroundElevation.setText("");
        txtAddWellSpudDate.setText("");
        txtAddWellCompletionDate.setText("");
        txtAddWellTD.setText("");
        txtAddRemarks.setText("");

    }
    
    private void btnAddCoreSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCoreSheetActionPerformed

        Layout.show(ParentPanel, "AddCoreSheet");
        ParentPanel.repaint();
        ParentPanel.revalidate();
      
    }//GEN-LAST:event_btnAddCoreSheetActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        if(txtAddDrillProgram.getText().equals("") ||
                txtAddLease.getText().equals("") ||
                txtAddWellID.getText().equals("") ||
                txtAddCompany.getText().equals("") ||
                txtAddConductorCasingDepth.getText().equals("") ||
                txtAddCorePointDepth.getText().equals("") ||
                txtAddGroundElevation.getText().equals("") ||
                txtAddDrillingCompany.getText().equals("") ||
                txtAddRigUnitNumber.getText().equals("") ||
                txtAddWellSpudDate.getText().equals("") ||
                txtAddWellSpudTime.getText().equals("") ||
                txtAddWellCompletionDate.getText().equals("") ||
                txtAddWellCompletionTime.getText().equals("") ||
                txtAddRemarks.getText().equals("")){
            
             JOptionPane.showMessageDialog(null, "Please fill in all fields");
             
        }
        else if (IsNumaric(txtAddConductorCasingDepth.getText()) == false){
            JOptionPane.showMessageDialog(null, "Please Enter a number for Conductor Casing Depth");
        }
        else if (IsNumaric(txtAddCorePointDepth.getText()) == false){
            JOptionPane.showMessageDialog(null, "Please Enter a number for Core Point Depth");
        }
        else if (IsNumaric(txtAddGroundElevation.getText()) == false){
            JOptionPane.showMessageDialog(null, "Please Enter a number for Ground Elevation");
        }
        else if (IsNumaric(txtAddWellTD.getText()) == false){
            JOptionPane.showMessageDialog(null, "Please Enter a number for Well TD");
        }
        else {
            
        try {
            SaveButtonClicked();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ClearTextBoxes();
            
        }

    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtAddWellSpudDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddWellSpudDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddWellSpudDateActionPerformed

    private void txtAddRigUnitNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddRigUnitNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddRigUnitNumberActionPerformed

    private void txtEditRigUnitNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEditRigUnitNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEditRigUnitNumberActionPerformed

    private void txtEditWellSpudDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEditWellSpudDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEditWellSpudDateActionPerformed

    private void btnEditSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSaveActionPerformed
        
                        if(txtEditDrillProgram.getText().equals("") ||
                txtEditLease.getText().equals("") ||
                txtEditWellID.getText().equals("") ||
                txtEditCompany.getText().equals("") ||
                txtEditConductorCasingDepth.getText().equals("") ||
                txtEditCorePointDepth.getText().equals("") ||
                txtEditGroundElevation.getText().equals("") ||
                txtEditDrillingCompany.getText().equals("") ||
                txtEditRigUnitNumber.getText().equals("") ||
                txtEditWellSpudDate.getText().equals("") ||
                txtEditWellSpudTime.getText().equals("") ||
                txtEditWellCompletionDate.getText().equals("") ||
                txtEditWellCompletionTime.getText().equals("") ||
                txtEditRemarks.getText().equals("")){
            
             JOptionPane.showMessageDialog(null, "Please fill in all fields");
             
        }
        else if (IsNumaric(txtEditConductorCasingDepth.getText()) == false){
            JOptionPane.showMessageDialog(null, "Please Enter a number for Conductor Casing Depth");
        }
        else if (IsNumaric(txtEditCorePointDepth.getText()) == false){
            JOptionPane.showMessageDialog(null, "Please Enter a number for Core Point Depth");
        }
        else if (IsNumaric(txtEditGroundElevation.getText()) == false){
            JOptionPane.showMessageDialog(null, "Please Enter a number for Ground Elevation");
        }
        else if (IsNumaric(txtEditWellTD.getText()) == false){
            JOptionPane.showMessageDialog(null, "Please Enter a number for Well TD");
        }
        else {
        
        try {

        SaveButtonClickedEdit();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        PopulateViewSheet();
        
        Layout.show(ParentPanel, "ViewCoreSheet");
        ParentPanel.repaint();  
        ParentPanel.revalidate();  
        
        PopulateJList();
        
        }
    }//GEN-LAST:event_btnEditSaveActionPerformed

    private void btnDeleteTestRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTestRunActionPerformed
        
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this, "Delete Selected CoreRun", "Delete", dialogButton);
        if(dialogResult == 0) {
                 
            try {
            DeleteCoreRun();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        Layout.show(ParentPanel, "ViewCoreSheet");
        ParentPanel.repaint();  
        ParentPanel.revalidate();
                  
        } else {
          System.out.println("No Option");
        } 
        
    }//GEN-LAST:event_btnDeleteTestRunActionPerformed

    private void btnAddTestRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTestRunActionPerformed
        try {
        AddCoreRun();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    populateTable();
    }//GEN-LAST:event_btnAddTestRunActionPerformed

    private void txtEditTestRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEditTestRunActionPerformed
      
        try{
            
        try {
            EditCoreRun();
            GetTotalPercentageRecovery(((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).getID());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Please select a table to edit");
        }
        
    }//GEN-LAST:event_txtEditTestRunActionPerformed

    public ResultSet sql(String L){
        try {
            Class.forName("org.sqlite.JDBC");
            ResultSet rt;
            try (Connection con = DriverManager.getConnection("jdbc:sqlite:Data.sqlite")) {
                Statement statement = con.createStatement();
                rt = statement.executeQuery(L);
            }
            return rt;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void btnAddLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLogoActionPerformed
JFileChooser fileChooser = new JFileChooser();
fileChooser.setDialogTitle("Select The Company Logo");   
 
int userSelection = fileChooser.showOpenDialog(ParentPanel);
 
if (userSelection == JFileChooser.APPROVE_OPTION) {
    try {
        int id =sql("select count(*)  from ImageTable").getInt(1);
        addImageToDB(id, fileChooser.getSelectedFile().toString());
        Image img = getImage(id);
        img.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        ImageIcon ico=new ImageIcon(img) ;
        jLabel14.setIcon(ico);
    } catch (SQLException ex) {
        Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
    }
}
        
    }//GEN-LAST:event_btnAddLogoActionPerformed

    private void txtSearchForCoreSheetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchForCoreSheetMouseClicked
        txtSearchForCoreSheet.setText("");
    }//GEN-LAST:event_txtSearchForCoreSheetMouseClicked

    private void txtAddCorePointDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddCorePointDepthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddCorePointDepthActionPerformed

    private void btnEditCoreSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditCoreSheetActionPerformed
        Layout.show(ParentPanel, "EditCoreSheet");
        ParentPanel.repaint();
        ParentPanel.revalidate();
  
        int index = lstCoreSheets.getSelectedIndex();
        
        txtEditDrillProgram.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetDrillProgram());
        txtEditLease.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetLease());
        txtEditWellID.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellID());
        txtEditCompany.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetCompany());
        txtEditConductorCasingDepth.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetConductorCasingDepth().toString());
        txtEditCorePointDepth.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetCorePointDepth().toString());
        txtEditGroundElevation.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetGroundElevation().toString());
        txtEditDrillingCompany.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetDrillingCompany());
        txtEditRigUnitNumber.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetRigUnitNumber());
        txtEditWellSpudDate.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellSpudDate());
        txtEditWellSpudTime.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellSpudTime());
        txtEditWellCompletionDate.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellCompletionDate());
        txtEditWellCompletionTime.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellCompletionTime());
        txtEditWellTD.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellTD());
        txtEditRemarks.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).getRemarks());
    }//GEN-LAST:event_btnEditCoreSheetActionPerformed

    private void btnDeleteCoreSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteCoreSheetActionPerformed





int opcion = JOptionPane.showConfirmDialog(null, "Delete Core Sheet", "Delete", JOptionPane.YES_NO_OPTION);

if (opcion == 0) { //The ISSUE is here
           try {

            Delete();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
          Layout.show(ParentPanel, "HomeScreen");
          ParentPanel.repaint();
          ParentPanel.revalidate();
          
          PopulateJList();
} else {
   System.out.print("no");
}
            

            
        //}
        


    }//GEN-LAST:event_btnDeleteCoreSheetActionPerformed

    private void btnEditCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditCancelActionPerformed
        Layout.show(ParentPanel, "ViewCoreSheet");
          ParentPanel.repaint();
          ParentPanel.revalidate();
    }//GEN-LAST:event_btnEditCancelActionPerformed

    private void btnCancelAddCoreSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelAddCoreSheetActionPerformed
        Layout.show(ParentPanel, "ViewCoreSheet");
          ParentPanel.repaint();
          ParentPanel.revalidate();
          
          ClearTextBoxes();
    }//GEN-LAST:event_btnCancelAddCoreSheetActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
       
        int index = lstCoreSheets.getSelectedIndex();
        DefaultTableModel model = (DefaultTableModel) tblPDFTable.getModel();
        Object RowData[] = new Object[11];
        model.getDataVector().removeAllElements();
        
        for (int i = 0; i < TestRun.size(); i++) {
            if(((TestRun)TestRun.Getelementbypos(i)).getParentCoreSheet() == index){
            RowData[0] = ((TestRun)TestRun.Getelementbypos(i)).GetTime();
            RowData[1] = ((TestRun)TestRun.Getelementbypos(i)).GetRun();
            RowData[2] = ((TestRun)TestRun.Getelementbypos(i)).GetTubeNumber1();
            RowData[3] = ((TestRun)TestRun.Getelementbypos(i)).GetTubeNumber2();
            RowData[4] = ((TestRun)TestRun.Getelementbypos(i)).GetCoreFromDepth();
            RowData[5] = ((TestRun)TestRun.Getelementbypos(i)).GetCoreToDepth();
            RowData[6] = ((TestRun)TestRun.Getelementbypos(i)).GetMetersCored();
            RowData[7] = ((TestRun)TestRun.Getelementbypos(i)).GetKD();
            RowData[8] = ((TestRun)TestRun.Getelementbypos(i)).getMetersRecovered();
            RowData[9] = ((TestRun)TestRun.Getelementbypos(i)).GetDescription();
            RowData[10] = ""; 
            
            model.addRow(RowData);
            
            }
            
        }
        
     lblViewDrillProgramPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetDrillProgram());
     lblViewLeasePDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetLease());
     lblViewWellIDPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellID());
     lblViewCompanyPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetCompany());
     lblViewConductorCasingDepthPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetConductorCasingDepth().toString());
     lblViewConductorCasingElevationPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetConductorCasingElevation().toString());
     lblViewCorePointDepthPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetCorePointDepth().toString());
     lblViewCorePointElevationPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetCorePointElevation().toString());
     lblViewGroundElevationPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetGroundElevation().toString());
     lblViewDrillingCompanyPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetDrillingCompany());
     lblViewRigUnitNumberPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetRigUnitNumber());
     lblViewWellSpudDatePDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellSpudDate());
     lblViewWellSpudTimePDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellSpudTime());
     lblViewWellCompletionDatePDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellCompletionDate());
     lblViewWellCompletionTimePDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellCompletionTime());
     lblViewWellTDPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellTD());
     lblViewTotalPercentageRecoveryPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).GetTotalPercentRecovery().toString());
     txtViewRemarkPDF.setText(((CoreSheet)CoreSheet.Getelementbypos(index)).getRemarks());
     
        Image img = getImage(0);
        img.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        ImageIcon ico=new ImageIcon(img) ;
        jLabel51.setIcon(ico);
        
        Dimension size = ParentPanel.size();
        
        ParentPanel.setSize(2550, 3300);
        PDF.setSize(2550, 3300);
        BufferedImage bi = new BufferedImage(PDF.getWidth(), PDF.getHeight(), BufferedImage.TYPE_INT_RGB);
        ParentPanel.setSize(size);
        Graphics g = bi.createGraphics();
        PDF.print(g);
        g.dispose();
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Location to save");        
            File outputfile = new File(""+((CoreSheet)CoreSheet.Getelementbypos(index)).GetCompany()+"-"+((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellID()+"-"+((CoreSheet)CoreSheet.Getelementbypos(index)).GetWellCompletionDate()+".pdf");
            fileChooser.setSelectedFile(outputfile);
            int userSelection = fileChooser.showSaveDialog(ParentPanel);
            if (userSelection == JFileChooser.APPROVE_OPTION)
            {
                File fileToSave = fileChooser.getSelectedFile();
                ImageIO.write(bi, "jpeg", fileToSave);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //JOptionPane.showMessageDialog(null, "The PDF has been Printed To LabelGeneratorPDFs Folder on The Desktop");
        
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        
        try{
        
        DefaultTableModel model = (DefaultTableModel) tblTestRuns.getModel();
        int index = (int)tblTestRuns.getSelectedRow();
        int CoreRunID = (int)model.getValueAt(index, 10);
        int pos = GetCoreRunPosition(CoreRunID);
        int ParentSheet = ((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).getID();
        double MetersRecovered = ((TestRun)TestRun.Getelementbypos(pos)).getMetersRecovered();
        
        //String Desktop = System.getProperty("user.home") + "/Desktop";
        String path = "LabelGeneratorLabels";
        CreateDirectory(path);
        
        lblBottomWellID.setText(((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).GetWellID());
        lblBottomCoreFrom.setText(""+((TestRun)TestRun.Getelementbypos(pos)).GetCoreFromDepth()+"");
        lblBottomCoreTo.setText(""+((TestRun)TestRun.Getelementbypos(pos)).GetCoreToDepth()+"");
        lblBottomRun.setText("Run "+((TestRun)TestRun.Getelementbypos(pos)).GetRun()+"");
        lblBottomTubeNo.setText("Tube " + ((TestRun)TestRun.Getelementbypos(pos)).GetTubeNumber1());
        
        BufferedImage bi = new BufferedImage(385, 222, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        
        LabelBottom.print(g);
        g.dispose();
        try {
            ImageIO.write(bi, "png", new File(""+path+"//LabelBottom1.png"));
            print(""+path+"//LabelBottom1.png");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        lblBottomTubeNo.setText("Tube " + ((TestRun)TestRun.Getelementbypos(pos)).GetTubeNumber2());
        
        bi = new BufferedImage(385, 222, BufferedImage.TYPE_INT_RGB);
        g = bi.createGraphics();
        LabelBottom.print(g);
        g.dispose();
        try {
            ImageIO.write(bi, "png", new File(""+path+"//LabelBottom2.png"));
            //print(""+path+"//LabelBottom2.png");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        lblTopWellID.setText(((CoreSheet)CoreSheet.Getelementbypos(lstCoreSheets.getSelectedIndex())).GetWellID());
        lblTopCoreFrom.setText(""+((TestRun)TestRun.Getelementbypos(pos)).GetCoreFromDepth()+"");
        lblTopCoreTo.setText(""+((TestRun)TestRun.Getelementbypos(pos)).GetCoreToDepth()+"");
        lblTopRun.setText("Run "+((TestRun)TestRun.Getelementbypos(pos)).GetRun()+"");
        lblTopTubeNo.setText("Tube " + ((TestRun)TestRun.Getelementbypos(pos)).GetTubeNumber1());
        
        bi = new BufferedImage(385, 222, BufferedImage.TYPE_INT_RGB);
        g = bi.createGraphics();
        LabelTop.print(g);
        g.dispose();
        try {
            ImageIO.write(bi, "png", new File(""+path+"//LabelTop1.png"));
            //print(""+path+"//LabelTop1.png");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        lblTopTubeNo.setText("Tube " + ((TestRun)TestRun.Getelementbypos(pos)).GetTubeNumber2());
        
                bi = new BufferedImage(385, 222, BufferedImage.TYPE_INT_RGB);
        g = bi.createGraphics();
        LabelTop.print(g);
        g.dispose();
        try {
            ImageIO.write(bi, "png", new File(""+path+"//LabelTop2.png"));
            //print(""+path+"//LabelTop2.png");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "The labels have been Printed To LabelGeneratorLabels Folder in the program folder (The old labels have been replaced)");
        }
        
        catch (Exception e){
            JOptionPane.showMessageDialog(null, "Please select a table");
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    private byte[] getByteArrayFromFile(String filePath){
        byte[] result=null;
        FileInputStream fileInStr=null;
        try{
            File imgFile=new File(filePath);
            fileInStr=new FileInputStream(imgFile);
            long imageSize=imgFile.length();
            
            if(imageSize>Integer.MAX_VALUE){
                return null;    //image is too large
            }
            
            if(imageSize>0){
                result=new byte[(int)imageSize];
                fileInStr.read(result);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                fileInStr.close();
            } catch (Exception e) {
            }
        }
        return result;
    }
    
    public void addImageToDB(int id,String imageName){
        try{
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
            String query="INSERT INTO ImageTable(id,image) VALUES (?, ?)";
            PreparedStatement prepStmt=null;
            try{
                conn.setAutoCommit(false);
                prepStmt=conn.prepareStatement(query);
                prepStmt.setInt(1, id);
                
                byte[] imageFileArr=getByteArrayFromFile(imageName);
                prepStmt.setBytes(2, imageFileArr);
                
                prepStmt.executeUpdate();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Image saved successfully!","Successfull",JOptionPane.INFORMATION_MESSAGE);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try {
                    conn.close();
                    prepStmt.close();
                } catch (Exception e) {
                }
            }
        }catch(SQLException ex){
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Image getImage(int id){
        try{
            Image img=null;
            String query="select image from ImageTable where id='"+id+"'";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Data.sqlite");
            Statement stmt=null;
            try{
                stmt=conn.createStatement();
                ResultSet rslt=stmt.executeQuery(query);
                if(rslt.next()){
                    byte[] imgArr=rslt.getBytes("image");
                    img=Toolkit.getDefaultToolkit().createImage(imgArr);
                }
                
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try {
                    conn.close();
                    stmt.close();
                } catch (Exception e) {
                }
            }
            
            return img;
        }catch(SQLException ex){
            Logger.getLogger(GUI_LabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void print(String fileName)
    {
        FileInputStream psStream = null;
        try {
            psStream = new FileInputStream(fileName);
            } catch (FileNotFoundException ffne) {
              ffne.printStackTrace();
            }
            if (psStream == null) {
                return;
            }
        DocFlavor dof = DocFlavor.INPUT_STREAM.JPEG;
         if (fileName.endsWith(".gif")) {
                                dof = DocFlavor.INPUT_STREAM.GIF;
                        } else if (fileName.endsWith(".jpg")) {
                                dof = DocFlavor.INPUT_STREAM.JPEG;
                        } else if (fileName.endsWith(".png")) {
                                dof = DocFlavor.INPUT_STREAM.PNG;
                        }
        Doc myDoc = new SimpleDoc(psStream, dof, null);  
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(dof, aset);
         
        // this step is necessary because I have several printers configured
        PrintService myPrinter = null;
        for (int i = 0; i < services.length; i++){
            String svcName = services[i].toString();    
            System.out.println("service found: "+svcName);
            if (svcName.contains("DYMO")){
                myPrinter = services[i];
                System.out.println("my printer found: "+svcName);
                break;
            }
        }        
        if (myPrinter != null) {            
            DocPrintJob job = myPrinter.createPrintJob();
            try {
                //aset.add(OrientationRequested.PORTRAIT);
                aset.add(OrientationRequested.LANDSCAPE);
                aset.add(MediaSizeName.ISO_A7);
                //DocAttributeSet das = new HashDocAttributeSet();
                //das.add(new MediaPrintableArea(0,0,102,59,MediaPrintableArea.MM));      
                job.print(myDoc, aset);
                psStream.close();
            } catch (Exception pe) {pe.printStackTrace();}
        } else {
            System.out.println("no printer services found");
        }
    }
    
    public static void main(String args[]) throws ClassNotFoundException, SQLException {
        
        PopulateArray();
        //CreateDirectory("C:\\LabelGenerator\\Images");
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            new GUI_LabelGenerator().setVisible(true);
            
            lstCoreSheets.addListSelectionListener(listSelectionListener);
            
            Layout = new CardLayout();
            ParentPanel.setLayout(Layout);
            
            ParentPanel.add("HomeScreen", HomeScreen);
            ParentPanel.add("AddCoreSheet", AddCoreSheet);
            ParentPanel.add("ViewCoreSheet", ViewCoreSheet);
            ParentPanel.add("EditCoreSheet", EditCoreSheet);
            ParentPanel.add("PDF", PDF);
            ParentPanel.add("LabelTop", LabelTop);
            ParentPanel.add("LabelBottom", LabelBottom);

            PopulateJList();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JPanel AddCoreSheet;
    private static javax.swing.JPanel EditCoreSheet;
    private static javax.swing.JPanel HomeScreen;
    private javax.swing.JLabel IconTop;
    private static javax.swing.JPanel LabelBottom;
    private static javax.swing.JPanel LabelTop;
    private static javax.swing.JPanel PDF;
    private static javax.swing.JPanel ParentPanel;
    private javax.swing.JLabel Remarks;
    private static javax.swing.JPanel ViewCoreSheet;
    private javax.swing.JButton btnAddCoreSheet;
    private javax.swing.JButton btnAddLogo;
    private javax.swing.JButton btnAddTestRun;
    private javax.swing.JButton btnCancelAddCoreSheet;
    private javax.swing.JButton btnDeleteCoreSheet;
    private javax.swing.JButton btnDeleteTestRun;
    private javax.swing.JButton btnEditCancel;
    private javax.swing.JButton btnEditCoreSheet;
    private javax.swing.JButton btnEditSave;
    private javax.swing.JButton btnEditUploadImage;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel iconBottom;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private static javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private static javax.swing.JLabel lblBottomCoreFrom;
    private static javax.swing.JLabel lblBottomCoreTo;
    private static javax.swing.JLabel lblBottomRun;
    private static javax.swing.JLabel lblBottomTubeNo;
    private static javax.swing.JLabel lblBottomWellID;
    private static javax.swing.JLabel lblTopCoreFrom;
    private static javax.swing.JLabel lblTopCoreTo;
    private static javax.swing.JLabel lblTopRun;
    private static javax.swing.JLabel lblTopTubeNo;
    private static javax.swing.JLabel lblTopWellID;
    private static javax.swing.JLabel lblViewCompany;
    private javax.swing.JLabel lblViewCompanyPDF;
    private static javax.swing.JLabel lblViewConductorCasingDepth;
    private javax.swing.JLabel lblViewConductorCasingDepthPDF;
    private static javax.swing.JLabel lblViewConductorCasingElevation;
    private javax.swing.JLabel lblViewConductorCasingElevationPDF;
    private static javax.swing.JLabel lblViewCorePointDepth;
    private javax.swing.JLabel lblViewCorePointDepthPDF;
    private static javax.swing.JLabel lblViewCorePointElevation;
    private javax.swing.JLabel lblViewCorePointElevationPDF;
    private static javax.swing.JLabel lblViewDrillProgram;
    private javax.swing.JLabel lblViewDrillProgramPDF;
    private static javax.swing.JLabel lblViewDrillingCompany;
    private javax.swing.JLabel lblViewDrillingCompanyPDF;
    private static javax.swing.JLabel lblViewGroundElevation;
    private javax.swing.JLabel lblViewGroundElevationPDF;
    private static javax.swing.JLabel lblViewLease;
    private javax.swing.JLabel lblViewLeasePDF;
    private static javax.swing.JLabel lblViewRigUnitNumber;
    private javax.swing.JLabel lblViewRigUnitNumberPDF;
    private static javax.swing.JLabel lblViewTotalPercentageRecovery;
    private javax.swing.JLabel lblViewTotalPercentageRecoveryPDF;
    private static javax.swing.JLabel lblViewWellCompletionDate;
    private javax.swing.JLabel lblViewWellCompletionDatePDF;
    private static javax.swing.JLabel lblViewWellCompletionTime;
    private javax.swing.JLabel lblViewWellCompletionTimePDF;
    private static javax.swing.JLabel lblViewWellID;
    private javax.swing.JLabel lblViewWellIDPDF;
    private static javax.swing.JLabel lblViewWellSpudDate;
    private javax.swing.JLabel lblViewWellSpudDatePDF;
    private static javax.swing.JLabel lblViewWellSpudTime;
    private javax.swing.JLabel lblViewWellSpudTimePDF;
    private static javax.swing.JLabel lblViewWellTD;
    private javax.swing.JLabel lblViewWellTDPDF;
    private static javax.swing.JList<String> lstCoreSheets;
    private javax.swing.JScrollPane scrollCoreSheets;
    private javax.swing.JTable tblPDFTable;
    private static javax.swing.JTable tblTestRuns;
    private static javax.swing.JTextField txtAddCompany;
    private static javax.swing.JTextField txtAddConductorCasingDepth;
    private static javax.swing.JTextField txtAddCorePointDepth;
    private static javax.swing.JTextField txtAddDrillProgram;
    private static javax.swing.JTextField txtAddDrillingCompany;
    private static javax.swing.JTextField txtAddGroundElevation;
    private static javax.swing.JTextField txtAddLease;
    private static javax.swing.JTextArea txtAddRemarks;
    private static javax.swing.JTextField txtAddRigUnitNumber;
    private static javax.swing.JTextField txtAddWellCompletionDate;
    private static javax.swing.JTextField txtAddWellCompletionTime;
    private static javax.swing.JTextField txtAddWellID;
    private static javax.swing.JTextField txtAddWellSpudDate;
    private static javax.swing.JTextField txtAddWellSpudTime;
    private static javax.swing.JTextField txtAddWellTD;
    private static javax.swing.JTextField txtEditCompany;
    private static javax.swing.JTextField txtEditConductorCasingDepth;
    private static javax.swing.JTextField txtEditCorePointDepth;
    private static javax.swing.JTextField txtEditDrillProgram;
    private static javax.swing.JTextField txtEditDrillingCompany;
    private static javax.swing.JTextField txtEditGroundElevation;
    private static javax.swing.JTextField txtEditLease;
    private static javax.swing.JTextArea txtEditRemarks;
    private static javax.swing.JTextField txtEditRigUnitNumber;
    private javax.swing.JButton txtEditTestRun;
    private static javax.swing.JTextField txtEditWellCompletionDate;
    private static javax.swing.JTextField txtEditWellCompletionTime;
    private static javax.swing.JTextField txtEditWellID;
    private static javax.swing.JTextField txtEditWellSpudDate;
    private static javax.swing.JTextField txtEditWellSpudTime;
    private static javax.swing.JTextField txtEditWellTD;
    private javax.swing.JTextField txtSearchForCoreSheet;
    private static javax.swing.JTextArea txtViewRemark;
    private javax.swing.JTextArea txtViewRemarkPDF;
    // End of variables declaration//GEN-END:variables
}
