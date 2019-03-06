package com.bill.pro.invoice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;


public class InvoiceGenerator {

 private BaseFont bfBold;
 private BaseFont bf;
 private int pageNumber = 0;

 public static void main(String[] args) {

  String pdfFilename = "";
  InvoiceGenerator generateInvoice = new InvoiceGenerator();
 /* if (args.length < 1)
  {
   System.err.println("Usage: java "+ generateInvoice.getClass().getName()+
   " PDF_Filename");
   System.exit(1);
  }*/

  pdfFilename = "ahmed_pdf.pdf";
 // generateInvoice.createPDF(pdfFilename);
  generateInvoice.test();

 }


 public File test(){
  Document doc = new Document();
  PdfWriter docWriter = null;
  initializeFonts();
  String path = /*"docs/" +*/ "myTestFile.pdf";
  File file=new File(path);
  try {

   docWriter = PdfWriter.getInstance(doc , new FileOutputStream(path));
   doc.addAuthor("betterThanZero");
   doc.addCreationDate();
   doc.addProducer();
   doc.addCreator("MySampleCode.com");
   doc.addTitle("Invoice");
   doc.setPageSize(PageSize.LETTER);

   doc.open();
   PdfContentByte cb = docWriter.getDirectContent();

   boolean beginPage = true;
   int y = 0;

   int count=1;

     generateLayout(doc, cb, new String[]{"Buyer Name","Address Line 1 ", "Address Line 2 ", "City and State", "XXXXDummyGSTIN"});
     Item item = new Item();
     item.setDescription("This is test Item");
     item.setMaterialCode("ITM0001");
     item.setPrice(120.00);

     Map<Item,Integer> map = new HashMap<>();
     map.put(item, 1);
     Map.Entry pair = map.entrySet().iterator().next();

   generateDetail( doc,  cb, 615, pair,1);

   printBillCalculation( "118", "1000", "118",cb,
   100, 10.0,10.0); // Adding the default cgst and sgst percent

    System.out.println("Y is ---->"+y);

  }
  catch (DocumentException dex)
  {
   dex.printStackTrace();
  }
  catch(FileNotFoundException e){
   return null;
  }
  catch (Exception ex)
  {
   ex.printStackTrace();
  }

  finally
  {
   if (doc != null)
   {
    doc.close();
   }
   if (docWriter != null)
   {
    docWriter.close();
   }
  }
  return file;
 }

 public File calculateAndPrint(Map<Item, Double> bill, double cgstPercent, double sgstPercent, String[] buyer, String invoiceNumber){
  double total =0 ;
  double grandTotal=0;
  Set<Item> itemSet = bill.keySet();

  for(Item i: itemSet){
   total+=bill.get(i)*i.price;
  }


  double sgst=(total*(sgstPercent/100));
  double cgst=(total*(cgstPercent/100));

  grandTotal = total + sgst+cgst;

  String arr[] = LocalDateTime.now().toString().split("\\.");


  String pdfName = "bill_"+ arr[0].replaceAll(":", "_") + ".pdf";

  return createPDF(pdfName, bill, Double.toString(cgst), Double.toString(grandTotal),
          invoiceNumber, Double.toString(sgst), buyer, sgstPercent, cgstPercent);


 }


 public  File createPDF (String pdfFilename, Map<Item,Double> Bill,String cgst,String total,
                         String invoiceNumber,String sgst, String[] buyer, double sgstPercent, double cgstPercent){

  Document doc = new Document();
  PdfWriter docWriter = null;
  initializeFonts();
  String path = /*"docs/" +*/ pdfFilename;
  File file=new File(path);
  try {

   docWriter = PdfWriter.getInstance(doc , new FileOutputStream(path));
   doc.addAuthor("betterThanZero");
   doc.addCreationDate();
   doc.addProducer();
   doc.addCreator("MySampleCode.com");
   doc.addTitle("Invoice");
   doc.setPageSize(PageSize.LETTER);

   doc.open();
   PdfContentByte cb = docWriter.getDirectContent();
   
   boolean beginPage = true;
   int y = 0;
   
   Iterator itr=Bill.entrySet().iterator();
   int count=1;
   while(itr.hasNext()){//for Starts here 
	Map.Entry<Item, Double> pair=(Map.Entry<Item, Double>) itr.next();
    if(beginPage){
     beginPage = false;
     generateLayout(doc, cb, buyer);
     generateHeader(doc, cb,invoiceNumber);
     y = 615; 
    }
    System.out.println("Y is ---->"+y);
    generateDetail(doc,cb,y,pair,count);
    count++;
    y = y - 15;
    if(y < 50){
     printPageNumber(cb);
     doc.newPage();
     beginPage = true;
    }
   }//here For End 
   
   //Print the labour charges
  // if(labour!=null)
   //generateLabourDetail(doc,cb,y,null,count);
   
   //printing the total details :
   y=105;
   printBillCalculation(cgst, total,sgst, cb, y, sgstPercent, cgstPercent);
   
   
   printPageNumber(cb);

   
  }
  catch (DocumentException dex)
  {
   dex.printStackTrace();
  }
  catch(FileNotFoundException e){
	  return null;
  }
  catch (Exception ex)
  {
   ex.printStackTrace();
  }

  finally
  {
   if (doc != null)
   {
    doc.close();
   }
   if (docWriter != null)
   {
    docWriter.close();
   }
  }
  return file;
 }

private void printBillCalculation(String sgst, String total, String cgst,PdfContentByte cb,
		int y, double sgstPercent, double cgstPercent) {
	final LineSeparator lineSeparator = new LineSeparator();
	   lineSeparator.drawLine(cb, 20, 570, y);
	   y=y-15;

       Double dblTotal = Double.parseDouble(total);
       Double dblSgst = Double.parseDouble(sgst);
       Double dblCgst = Double.parseDouble(cgst);

       DecimalFormat df = new DecimalFormat("#######.##");


       //Add the disclaimer
       cb.setFontAndSize(bf, 2);
      createContent(cb,25,y,"I/We hereby certify that my/our registeration certificate under the GST act, 2017 is in force of the data on which",PdfContentByte.ALIGN_LEFT);
      createContent(cb,25,y-10,"the sale of the goods specified in this \"Tax Invoice\" is made by me/us and that the transaction of sale covered by",PdfContentByte.ALIGN_LEFT);
      createContent(cb,25,y-20,"this \"Tax Invoice\" has been affected by me/us & it shall be accounted for in the turnover of sales while filing of",PdfContentByte.ALIGN_LEFT);
      createContent(cb,25,y-30,"return and the due tax, if any payable on the sale has been paid or shall be paid.",PdfContentByte.ALIGN_LEFT);



       cb.setFontAndSize(bfBold, 8);
	   createContent(cb,498,y,"SGST @ "+sgstPercent+"%:",PdfContentByte.ALIGN_RIGHT);
	   createContent(cb,568,y,df.format(dblSgst),PdfContentByte.ALIGN_RIGHT);
	   //printing the service charge
	   y=y-15;
	   createContent(cb,498,y,"CGST @ "+cgstPercent+"%:",PdfContentByte.ALIGN_RIGHT);
	   createContent(cb,568,y,df.format(dblCgst),PdfContentByte.ALIGN_RIGHT);
			   
			 
	   


	   y=y-15;
	   createContent(cb,498,y,"TOTAL :",PdfContentByte.ALIGN_RIGHT);
	   createContent(cb,568,y, df.format(dblTotal) ,PdfContentByte.ALIGN_RIGHT);
	   
}

 private void generateLayout(Document doc, PdfContentByte cb, String [] buyer)  {

  try {

   cb.setLineWidth(1f);

   // Invoice Header box layout
   cb.rectangle(420,700,150,60);
   cb.moveTo(420,720);
   cb.lineTo(570,720);
   cb.moveTo(420,740);
   cb.lineTo(570,740);
   cb.moveTo(480,700);
   cb.lineTo(480,760);
   cb.stroke();


   // Both Party Address and GST No header
   cb.rectangle(20,675,390,90);
   cb.moveTo(200,675);
   cb.lineTo(200,765);
   cb.stroke();

   //Giving   party Address
   //createHeadings(cb,22,753,"Seller");
   createHeadings(cb,22,753,"N. Pithawala & Bros", 14);
   createContent(cb,22,733,"Dealers in: All Kinds of Iron and Steel",10);
   createContent(cb,22,723,"840/841, Bhawani Peth, Near Bharat Talkies", 10);
   createContent(cb,22,713,"Pune - 411042", 10);

   createHeadings(cb,22,693,"Email: pithawalaa@yahoo.in");

   //Taker party address
   createHeadings(cb,222,753,"Buyer:");
   createHeadings(cb,222,733,buyer[0]);
   createHeadings(cb,222,723,buyer[1]);
   createHeadings(cb,222,713,buyer[2]);
   createHeadings(cb,222,703,buyer[3]);

   createHeadings(cb,222,683,"GST no. "+buyer[4]);



   // Invoice Header box Text Headings 
   createHeadings(cb,422,743,"GSTIN.");
   createHeadings(cb,422,723,"Invoice No.");
   createHeadings(cb,422,703,"Invoice Date");

   // Invoice Detail box layout 
   cb.rectangle(20,50,550,600);
   cb.moveTo(20,630);
   cb.lineTo(570,630);
   cb.moveTo(50,105);  // Changed y from 50 to 105 to accomodate the disclaimer
   cb.lineTo(50,650);
   cb.moveTo(150,105); // Changed y from 50 to 105 to accomodate the disclaimer
   cb.lineTo(150,650);

   cb.moveTo(430,50);
   cb.lineTo(430,650);
   
   //New Line added
   //cb.moveTo(470, 50);
   cb.moveTo(470, 105);
   //cb.lineTo(470, 650);//changed to shorten the length
   cb.lineTo(470, 650);
   
   cb.moveTo(500,50);
   cb.lineTo(500,650);
   cb.stroke();

   // Invoice Detail box Text Headings 
   createHeadings(cb,22,633,"Sr No.");
   createHeadings(cb,52,633,"Item Number");
   createHeadings(cb,152,633,"Item Description");
   createHeadings(cb,432,633,"Price");
   createHeadings(cb,472,633,"Qty");
   createHeadings(cb,502,633,"Total");

   //add the images
   /*URL url = Thread.currentThread().getContextClassLoader().getResource("resources/BOARD_final_curves_resized.png");
   Image companyLogo = Image.getInstance(url);
   companyLogo.setAbsolutePosition(25,700);
   companyLogo.scalePercent(25);
   doc.add(companyLogo);*/

  }


  catch (Exception ex){
   ex.printStackTrace();
  }

 }
 
 private void generateHeader(Document doc, PdfContentByte cb,String invoiceNumber)  {

  try {

   DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");


   //get current date time with Calendar()
   Calendar cal = Calendar.getInstance();

   
   
   createHeadings(cb,482,743,"27ABQPP3776M1ZO");
   createHeadings(cb,482,723,invoiceNumber);
   createHeadings(cb,482,703,dateFormat.format(cal.getTime()));

  }

  catch (Exception ex){
   ex.printStackTrace();
  }

 }
 
 private void generateDetail(Document doc, PdfContentByte cb, int y,Map.Entry<Item,Double> pair,int serialno)  {
  DecimalFormat df = new DecimalFormat("0.00");
  
  try {

   createContent(cb,48,y,Integer.toString(serialno),PdfContentByte.ALIGN_RIGHT);
   createContent(cb,52,y, pair.getKey().getMaterialCode(),PdfContentByte.ALIGN_LEFT);
   createContent(cb,152,y,pair.getKey().getDescription(),PdfContentByte.ALIGN_LEFT);
   
   //double price = Double.valueOf(df.format(Math.random() * 10));
   //double extPrice = price * (index+1) ;
   //createContent(cb,498,y,Integer.toString(pair.getKey().getPRICE()),PdfContentByte.ALIGN_RIGHT);
   createContent(cb,455,y,Double.toString(pair.getKey().getPrice()),PdfContentByte.ALIGN_RIGHT);

   //createContent(cb,568,y,Integer.toString(pair.getValue()),PdfContentByte.ALIGN_RIGHT);
   createContent(cb,495,y,Double.toString(pair.getValue()),PdfContentByte.ALIGN_RIGHT);
   
   createContent(cb,540,y,Double.toString(pair.getKey().getPrice()*pair.getValue()),PdfContentByte.ALIGN_RIGHT);


  }

  catch (Exception ex){
   ex.printStackTrace();
  }

 }



 private void createHeadings(PdfContentByte cb, float x, float y, String text){


  cb.beginText();
  cb.setFontAndSize(bfBold, 8);
  cb.setTextMatrix(x,y);
  cb.showText(text.trim());
  cb.endText(); 

 }


 private void createHeadings(PdfContentByte cb, float x, float y, String text, int size){


  cb.beginText();
  cb.setFontAndSize(bfBold, size);
  cb.setTextMatrix(x,y);
  cb.showText(text.trim());
  cb.endText();

 }
 
 private void printPageNumber(PdfContentByte cb){


  cb.beginText();
  cb.setFontAndSize(bfBold, 8);
  cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "Page No. " + (pageNumber+1), 570 , 25, 0);
  cb.endText(); 
  
  pageNumber++;
  
 }
 
 private void createContent(PdfContentByte cb, float x, float y, String text, int align){


  cb.beginText();
  cb.setFontAndSize(bf, 8);
  cb.showTextAligned(align, text.trim(), x , y, 0);
  cb.endText(); 

 }

 private void createContent(PdfContentByte cb, float x, float y, String text, int align, int size){


  cb.beginText();
  cb.setFontAndSize(bf, size);
  cb.showTextAligned(align, text.trim(), x , y, 0);
  cb.endText();

 }

 private void initializeFonts(){


  try {
   bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
   bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

  } catch (DocumentException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  }


 }

}



//I/We hereby certify that my/our registeration certificate under the GST act, 2017is in force of the data on which the
//sale of the goods specified in this "Tax Invoice" is made by me/us and that the transaction of sale covered by
//this "Tax Invoice" has been affected by me/us & it shall be accounted for in the turnover of sales while filing of return
//and the due tax, if any payable on the sale has been paid or shall be paid.