package com.bill.pro.gui;

import com.bill.pro.invoice.InvoiceGenerator;
import com.bill.pro.invoice.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class AddItem2 {
    private JTable table1;
    private JPanel panel1;
    private JTextField txtItemName;
    private JTextField txtItemQty;
    private JButton addItemButton;
    private JTextField txtItemPrice;
    private JTextField txtCgst;
    private JTextField txtSgst;
    private JButton btnSaveAndGenerateBill;
    private JPanel Buyer;
    private JTextField txtAddressLine1;
    private JTextField txtAddressLine2;
    private JTextField txtCity;
    private JTextField txtState;
    private JTextField txtBuyergstin;
    private JPanel itemInfo;
    private JButton clearButton;
    private JComboBox cmbBuyerName;
    private JButton clearItemButton;
    InvoiceGenerator invoiceGenerator=new InvoiceGenerator();
    Map<String, String > buyerNamesMap = new HashMap<>();

    public AddItem2() {

        //TODO: add a method here to populate the Map from a file on disk
        populateBuyerMap();


       /* String[] buyerNames = {"Ahmed", "Hashmi", "Mohammad Boldiwala", "Mohammad sariawala"};

        buyerNamesMap =  new HashMap<>();
        buyerNamesMap.put("Ahmed", "buyerAddressLine1-Ahmed,buyerAddressLine2-Ahmed,City-Ahmed,State-Ahmed,GSTIN-Ahmed");
        buyerNamesMap.put("Hashmi", "buyerAddressLine1-Hashmi,buyerAddressLine2-Hashmi,City-Hashmi,State-Hashmi,GSTIN-Hashmi");
        buyerNamesMap.put("Mohammad Boldiwala", "buyerAddressLine1-Boldiwala,buyerAddressLine2-Boldiwala,City-Boldiwala,State-Boldiwala,GSTIN-Boldiwala");
        buyerNamesMap.put("Mohammad sariawala", "buyerAddressLine1-sariawala,buyerAddressLine2-sariawala,City-sariawala,State-sariawala,GSTIN-sariawala");
*/


        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model =  (DefaultTableModel) table1.getModel();
                String itemName = txtItemName.getText();
                String itemQty =  txtItemQty.getText();
                String itemPrice = txtItemPrice.getText();

                if( !itemName.isEmpty() && !itemQty.isEmpty() && !itemPrice.isEmpty())
                model.addRow(new Object[] { txtItemName.getText(), txtItemQty.getText(), txtItemPrice.getText() });
                else if (itemName.isEmpty())
                    JOptionPane.showMessageDialog(null, "Please Enter Item Name");
                else if (itemQty.isEmpty())
                    JOptionPane.showMessageDialog(null, "Please Enter Item Quantity");
                else if (itemPrice.isEmpty())
                    JOptionPane.showMessageDialog(null, "Please Add Item Price");



            }
        });


        btnSaveAndGenerateBill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<Item,Double> bill = new LinkedHashMap<>();

                DefaultTableModel model = (DefaultTableModel) table1.getModel();
               //Creating item bill
                for(int i=0;i<model.getRowCount();i++){
                    String itemName = (String) model.getValueAt(i,0);
                    System.out.println("Value of itemName is -->" + itemName);
                    String itemQuantity = (String) model.getValueAt(i,1);
                    System.out.println("Value of itemQuntity is -->" + itemQuantity);
                    String itemPrice = (String) model.getValueAt(i,2);
                    System.out.println("Value of itemPrice is -->" + itemPrice);

                    Item item = new Item();
                    item.setMaterialCode(itemName);
                    item.setDescription(itemName);
                    item.setPrice(Double.parseDouble(itemPrice.trim()));

                    bill.put(item, Double.parseDouble(itemQuantity.trim()));

                }
                //Item Bill creation

                //GST Info
                double cgstPercent = Double.parseDouble(txtCgst.getText().trim());
                double sgstPercent = Double.parseDouble(txtSgst.getText().trim());


                //Buyer Info
                String [] buyer = new String [5];

                buyer[0]=(String)cmbBuyerName.getSelectedItem();
                buyer[1]=txtAddressLine1.getText();
                buyer[2]=txtAddressLine2.getText();
                buyer[3]=txtCity.getText() + "   " +txtState.getText();
                buyer[4] = txtBuyergstin.getText();


                //Read the invoice number from a file
                String invoiceNumber = "";
                String invoiceNumFile = "invoiceNum.txt";
                File file = new File(invoiceNumFile);
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        //process the line
                        System.out.println("Read value from the file is ---> "+ line.trim());
                        invoiceNumber = line;
                    }
                    br.close();

                }
                catch(Exception ex ){
                    System.out.println("Exception Ocurred while reading the file");
                }



                invoiceGenerator.calculateAndPrint(bill, cgstPercent,sgstPercent,buyer, invoiceNumber);


                //Saving and Adding the buyer profile
                checkAndSaveBuyerName(buyer);


                //Write the updated invoice number to a file
                BufferedWriter bw;
                try {
                     bw = new BufferedWriter(new FileWriter(file));
                    int inv = Integer.parseInt(invoiceNumber);
                    ++inv;
                    bw.write(""+inv);
                    bw.close();
                }
                catch(Exception ex ){
                    System.out.println("Exception Occurred while writing the file" + ex);
                }

            }


        });


        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //remove all item fomr the list
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                model.getDataVector().removeAllElements();
                model.fireTableDataChanged(); // notifies the JTable that the model has changed


                //Item info clear
                txtItemName.setText("");
                txtItemQty.setText("");
                txtItemPrice.setText("");
                txtCgst.setText("");
                txtSgst.setText("");

                //buyer info clear
                cmbBuyerName.removeAllItems();
                txtAddressLine1.setText("");
                txtAddressLine2.setText("");
                txtBuyergstin.setText("");
                txtCity.setText("");
                txtState.setText("");


            }
        });


        cmbBuyerName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String command  = e.getActionCommand();

                System.out.println("Command is --->" + command);

                switch (command) {

                    case "comboBoxChanged":{


                        System.out.println("Command" + e.getActionCommand());
                        String inputText = (String) cmbBuyerName.getSelectedItem();
                        if(null == inputText || inputText.isEmpty())
                            return;

                        System.out.println(cmbBuyerName.getSelectedItem());

                        for (String str : buyerNamesMap.keySet()) {
                            if (((DefaultComboBoxModel) cmbBuyerName.getModel()).getIndexOf(str) == -1) {
                                if (str.toLowerCase().contains(inputText.toLowerCase()))
                                    cmbBuyerName.addItem(str);
                            }

                        }
                        break;
                }
                    case "comboBoxEdited":{


                        System.out.println("Inside the box changed command");
                        String selectedText = (String)cmbBuyerName.getSelectedItem();
                        if(selectedText==null || selectedText.isEmpty())
                            return;
                        //Add the code here to populate other text boxes based on the mapping of cmbBuyerName.getSelectedItem
                        String textBoxValues = buyerNamesMap.get(selectedText);
                        if(textBoxValues!=null && !textBoxValues.isEmpty()){
                            //Fill the text boxes with the values :
                            String[] arr = textBoxValues.split(",");
                            txtAddressLine1.setText(arr[0]);
                            txtAddressLine2.setText(arr[1]);
                            txtCity.setText(arr[2]);
                            txtState.setText(arr[3]);
                            txtBuyergstin.setText(arr[4]);
                        }

                    }

                }

            }
        });

        //Clear item action listener
        clearItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                model.removeRow(table1.getSelectedRow());
            }
        });
    }

    private void populateBuyerMap() {
        System.out.println("*******Populate Buyer Map Method called******");

        String fileName = "buyerInfo.txt";

        File file = new File(fileName);
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                //process the line
                System.out.println("Read value from the file is ---> "+ line.trim());
                String arr[] = line.split("_");
                buyerNamesMap.put(arr[0], arr[1]);
            }
            br.close();

        }
        catch(Exception ex ){
            System.out.println("Exception Ocurred while reading the saved buyerProfiles from disk");
        }

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        DefaultTableModel model = new DefaultTableModel();
         table1 = new JTable(model);
        table1.setModel(model);
        table1.setVisible(true);
        model.addColumn("Item Name");
        model.addColumn("Item Qty");
        model.addColumn("Item price");


    }


    private void checkAndSaveBuyerName(String[] buyerInfo) {

        if(buyerNamesMap.containsKey(buyerInfo[0]))
            return;

        String fileName = "buyerInfo.txt";
        File file = new File(fileName);

        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
            String buyerName = buyerInfo[0];
            String buyerAddress1 = buyerInfo[1];
            String buyerAddress2 = buyerInfo[2];
            String[] cityState = buyerInfo[3].split("   ");
            String buyerCity = cityState[0];
            String buyerState = cityState[1];

            System.out.println("Saving ---> buyer City"+ buyerCity);
            System.out.println("Saving ---> buyer State"+ buyerState);


            String buyerGstin = buyerInfo[4];
            String entry = buyerInfo[0] + "_"+buyerAddress1+","+buyerAddress2+","+buyerCity+","+buyerState+","+buyerGstin;
            bw.write(entry+"\n");
            bw.close();

            //Adding the saved buyer to the map as well
            buyerNamesMap.put(buyerName, entry);
        }
        catch(Exception ex ){
            System.out.println("Exception Occurred while writing to the buyer profile file to the disk" + ex);
        }

    }

   public static void main(String args[]){
       AddItem2 obj = new AddItem2();
       JFrame f = new JFrame();
       f.setSize(600   , 600);
       f.setContentPane(obj.panel1);

       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.setVisible(true);
   }
}
