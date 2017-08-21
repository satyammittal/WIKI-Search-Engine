import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.regex.Pattern;
import org.xml.sax.helpers.DefaultHandler;
import java.nio.charset.Charset;
class values{

}   
public class GenerateStopwords {
   public static HashMap<String,Integer> tf=new HashMap<String,Integer>();
   public static HashMap<String,Integer> idf=new HashMap<String,Integer>(); 
   public static HashMap<String,Integer> mat=new HashMap<String,Integer>(); 
   private Set<String> stopwords;
   public void GenerateStopwords(){

   }
   public static void main(String[] args){

   }
   public void Generatefromxml(String[] args) {

      try {
         File inputFile = new File("wiki-search-small.xml");
         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         UserHandler2 userhandler = new UserHandler2();
         userhandler.sample(tf, idf);
         saxParser.parse(inputFile, userhandler);    
         Map<String,Integer> idf_sorted = sortByValue(idf);
         Map<String,Integer> tf_sorted = sortByValue(tf);
         for(Map.Entry m:idf.entrySet()){  
         Integer s1=(Integer) tf.get(m.getKey());
         Integer s2 =(Integer) m.getValue();
         Integer s3=s1*s2;
            mat.put((String)m.getKey(),s3);
      }  
      Map<String,Integer> mat_sorted = sortByValue(mat);
      
      for(Map.Entry m:mat_sorted.entrySet()){  
       System.out.println(m.getKey()+" "+m.getValue());  
      }  
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
       private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }


        return sortedMap;
    }
    public void generatefromfile(String file){
      stopwords = new HashSet<>();
      try{
         FileInputStream fis = new FileInputStream(file);
         BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
         String line;
         while ((line = br.readLine()) != null) {
        //System.out.println(line);
          stopwords.add(line);
        }
        br.close();
        }
        catch( Exception e)
        {
          e.printStackTrace();
        }
    }
    public boolean checkStopWord(StringBuilder str){    
      return (stopwords.contains(str));   
     }
      public boolean checkStopWord(String str){   
        return (stopwords.contains(str));   
    }
    public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey()
                    + " Value : " + entry.getValue());
        }
    }   
}
class UserHandler2 extends DefaultHandler {

   boolean isTitle = false;
   boolean isBody = false;
   boolean isRevision = false;
   boolean isId = false;
   Pattern ptn = Pattern.compile("(d+|[w]+)");
   HashSet<String> set=new HashSet<String>();  
   HashMap<String,Integer> tf;
   HashMap<String,Integer> idf;
        
   int counter=1;
   public void sample(HashMap<String,Integer> tf,HashMap<String,Integer> idf){
      this.tf=tf;
      this.idf=idf;
   }
   @Override
   public void startElement(String uri, 
   String localName, String qName, Attributes attributes) throws SAXException {
      counter++;
      if(counter==100) {
            // TODO: Whatever should happen when condition is reached
         return;
        }
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
      set=new HashSet<String>();
      if (isTitle) {
       //  System.out.println("Title: " 
         //   + new String(ch, start, length));
         isTitle = false;
      } else if (isBody) {
         String s2 = new String(ch, start, length);
         s2=s2.toLowerCase();
            System.out.println(s2);
         String s1 = s2.replaceAll("[-:()^[!|=,?._'{}@+\\[\\]]]", " ");
         String[] parts = s1.split("\\s+");
         for(String p:parts){
            set.add(p);
            if(tf.get(p)==null)
            {
               tf.put(p,1);
            }
            else{
               Integer r = tf.get(p)+1;
               tf.put(p,r);
            }
            System.out.println(p);
        }
         Iterator<String> itr=set.iterator();  
         System.out.println(set.toString());  
         while(itr.hasNext()){ 
            String y = itr.next();
         if(idf.get(y)==null)
            {
               idf.put(y,1);
            }
            else{
               Integer r = tf.get(y)+1;
               idf.put(y,r);
            }

       //  System.out.println(itr.next());  
         }

         isBody = false;
      } else if (isRevision) {
     //    System.out.println("Revision: " + new String(ch, start, length));
         isRevision = false;
      } else if (isId) {
         isId = false;
      }
   }
}
