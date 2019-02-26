package com.bill.pro.gui;

import com.bill.pro.invoice.InvoiceGenerator;
import com.bill.pro.invoice.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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


                invoiceGenerator.calculateAndPrint(bill, cgstPercent,sgstPercent,buyer);
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


//        Vector data = model.getDataVector();
//        Vector row = (Vector) data.elementAt(1);
//
//        int mColIndex = 0;
//        List colData = new ArrayList(table1.getRowCount());
//        for (int i = 0; i < table1
//                .getRowCount(); i++) {
//            row = (Vector) data.elementAt(i);
//            colData.add(row.get(mColIndex));
//        }

        // Append a new column with copied data
      //  model.addColumn("Col3", colData.toArray());


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
