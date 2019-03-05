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
    private JTextField txtBuyerName;
    private JTextField txtAddressLine1;
    private JTextField txtAddressLine2;
    private JTextField txtCity;
    private JTextField txtState;
    private JTextField txtBuyergstin;
    private JPanel itemInfo;
    private JButton clearButton;
    InvoiceGenerator invoiceGenerator=new InvoiceGenerator();

    public AddItem2() {

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
                Map<Item,Integer> bill = new HashMap<>();

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

                    bill.put(item, Integer.parseInt(itemQuantity.trim()));

                }
                //Item Bill creation

                //GST Info
                double cgstPercent = Double.parseDouble(txtCgst.getText().trim());
                double sgstPercent = Double.parseDouble(txtSgst.getText().trim());


                //Buyer Info
                String [] buyer = new String [5];

                buyer[0]=txtBuyerName.getText();
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
                finally{
                }



                invoiceGenerator.calculateAndPrint(bill, cgstPercent,sgstPercent,buyer, invoiceNumber);


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
                txtBuyerName.setText("");
                txtAddressLine1.setText("");
                txtAddressLine2.setText("");
                txtBuyergstin.setText("");
                txtCity.setText("");
                txtState.setText("");


            }
        });
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

   public static void main(String args[]){
       AddItem2 obj = new AddItem2();
       JFrame f = new JFrame();
       f.setSize(600   , 600);
       f.setContentPane(obj.panel1);

       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.setVisible(true);
   }
}
