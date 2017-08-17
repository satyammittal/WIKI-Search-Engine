import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.regex.Pattern;

public class indexer {

   public static void main(String[] args) {

      try {
         File inputFile = new File("wiki-search-small.xml");
         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         UserHandler userhandler = new UserHandler();
         saxParser.parse(inputFile, userhandler);     
      } catch (Exception e) {
         e.printStackTrace();
      }
   }   
}

class UserHandler extends DefaultHandler {

   boolean isTitle = false;
   boolean isBody = false;
   boolean isRevision = false;
   boolean isId = false;
   Pattern ptn = Pattern.compile("([|]|.)");
   @Override
   public void startElement(String uri, 
   String localName, String qName, Attributes attributes) throws SAXException {
   //      System.out.println("Roll No : " + qName);
      if (qName.equalsIgnoreCase("revision")) {
         isRevision = true;
      } else if (qName.equalsIgnoreCase("title")) {
         isTitle = true;
      } else if (qName.equalsIgnoreCase("text")) {
         isBody = true;
      } else if (qName.equalsIgnoreCase("nickname")) {
         isRevision = true;
      }
      else if (qName.equalsIgnoreCase("id")) {
      //   String rollNo = attributes.getValue("id");
       //  System.out.println("Roll No : " + rollNo);
         isId = true;
      }
   }

   @Override
   public void endElement(String uri, 
   String localName, String qName) throws SAXException {
      if (qName.equalsIgnoreCase("student")) {
         System.out.println("End Element :" + qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) throws SAXException {
      
      if (isTitle) {
       //  System.out.println("Title: " 
         //   + new String(ch, start, length));
         isTitle = false;
      } else if (isBody) {
         String[] parts = ptn.split(new String(ch, start, length));
         for(String p:parts){
            System.out.println(p);
        }
         isBody = false;
      } else if (isRevision) {
     //    System.out.println("Revision: " + new String(ch, start, length));
         isRevision = false;
      } else if (isId) {
       //  System.out.println("ID: " + new String(ch, start, length));
         isId = false;
      }
   }
}

