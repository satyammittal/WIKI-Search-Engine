import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.Attributes;
import java.time.Duration;
import java.time.Instant;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.regex.Pattern;

public class indexer {
   public static HashMap<String,Integer> tf=new HashMap<String,Integer>();
   public static HashMap<String,Integer> idf=new HashMap<String,Integer>(); 
   public static HashMap<String,Integer> mat=new HashMap<String,Integer>(); 
   public static TreeMap<String, TreeMap<Integer, List<Pair<Integer, String>>>> indexer;
   private StringBuffer bodyText = new StringBuffer("");
   public static void main(String[] args) {

     try {
         Instant start = Instant.now();
         File inputFile = new File("wiki-search-small.xml");
         SAXParserFactory factory = SAXParserFactory.newInstance();
         indexer = new TreeMap<String, TreeMap<Integer, List<Pair<Integer, String>>>>();
         SAXParser saxParser = factory.newSAXParser();
         UserHandler userhandler = new UserHandler();
         userhandler.sample(tf, idf, indexer);
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
      File statText = new File("indexfile3");
      FileOutputStream is = new FileOutputStream(statText);
      OutputStreamWriter osw = new OutputStreamWriter(is);    
      Writer w = new BufferedWriter(osw);

      //for(Map.Entry m:mat_sorted.entrySet()){  
       //
                  //System.out.println(m.getKey()+" "+m.getValue());  
      //}

      for(Map.Entry<String, TreeMap<Integer, List<Pair<Integer, String>>>> entry : indexer.entrySet()) {
         String key = entry.getKey();
         TreeMap<Integer, List<Pair<Integer, String>>> value = entry.getValue();
         w.write(key + "|");
         for(Map.Entry<Integer, List<Pair<Integer, String>>> entry2 : value.entrySet()) 
         {
            Integer doc = entry2.getKey();
            w.write(doc.toString() + ":");
            List<Pair<Integer, String>> pr2 = entry2.getValue();
            for (Pair<Integer, String> pr:pr2)
            {
               Integer in = pr.getFirst();
               String qr = pr.getSecond();
               w.write(in.toString() + "$" + qr + ","); 
            }
             w.write(";"); 
         }
               w.write("\n"); 
      }
      w.close();
      Instant end = Instant.now();
      Duration timeElapsed = Duration.between(start, end);
      System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");
      //System.out.println(indexer);  
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
}

class UserHandler extends DefaultHandler {

   boolean isTitle = false;
   boolean isBody = false;
   boolean isRevision = false;
   boolean isId = false;
   Pattern ptn = Pattern.compile("(d+|[w]+)");
   HashSet<String> set=new HashSet<String>();  
   HashMap<String,Integer> tf;
   HashMap<String,Integer> idf;
   TreeMap<String, TreeMap<Integer, List<Pair<Integer, String>>>> index;
   Stemmer stemf;
   Integer id=-1;
   Integer numberword = 0;
   GenerateStopwords stopw = new GenerateStopwords();
   int counter=1;
   public void sample(HashMap<String,Integer> tf,HashMap<String,Integer> idf,TreeMap<String, TreeMap<Integer, List<Pair<Integer, String>>>> indexer){
      this.tf=tf;
      this.idf=idf;
      this.index=indexer;
      stemf = new Stemmer();
      stopw.generatefromfile("english.txt");
   }
   @Override
   public void startElement(String uri, 
   String localName, String qName, Attributes attributes) throws SAXException {
      counter++;
      if(counter==2) {
            // TODO: Whatever should happen when condition is reached
         return;
        }
   //      //System.out.println("Roll No : " + qName);
      if (qName.equalsIgnoreCase("revision")) {
         isRevision = true;
      } else if (qName.equalsIgnoreCase("title")) {
         isTitle = true;
      } else if (qName.equalsIgnoreCase("text")) {
         isBody = true;
         numberword = 0;
      }
      else if (qName.equalsIgnoreCase("id")) {
      //   String rollNo = attributes.getValue("id");
       //  //System.out.println("Roll No : " + rollNo);
         isId = true;
      }
   }

   @Override
   public void endElement(String uri, 
   String localName, String qName) throws SAXException {
      final String tag = qName.toLowerCase();
      if(tag.equalsIgnoreCase("revision"))
      {
            isRevision = false;
      }
      else if(tag.equalsIgnoreCase("id"))
      {
         isId = false;
      }
      else if(tag.equalsIgnoreCase("title"))
      {
         isTitle = false;
      }
      else if(tag.equalsIgnoreCase("text"))
      {
         isBody = false;
         //System.out.println(String.valueOf(id));
      }
   }

   @Override
   public void characters(char ch[], int start, int length) throws SAXException {
      set=new HashSet<String>();
      if (isTitle) {
       //  //System.out.println("Title: " 
         //   + new String(ch, start, length));
      } else if (isBody) {
         String s2 = new String(ch, start, length);
         s2=s2.toLowerCase();
        // System.out.println(s2);
         String PATTERN_TOKEN = "\\$%{}[]()`<>='&:,;/.~ ;*\n|\"^_-+!?#\t@";
         //String s1 = s2.replaceAll("[-:()/^[!|=,?._'{}@+\\[\\]]]", " ");
         List<String> parts2 = new ArrayList<String>();
         final StringTokenizer normalTokenizer = new StringTokenizer(s2,PATTERN_TOKEN);
            while(normalTokenizer.hasMoreTokens()){
               parts2.add(normalTokenizer.nextToken().trim());
            }
        System.out.println(parts2);
        // String[] parts2 = s1.split("\\s+");
         Pattern rp = Pattern.compile("^[a-zA-Z0-9]*$");
         List<String> parts = new ArrayList<String>();
       for(String p:parts2)
         {
            Matcher m = rp.matcher(p);
         if (m.find())
            parts.add(p);
         }
         //System.out.println(parts.toString());
         for(String p:parts){
            String p2=p;
            stemf.add(p.toCharArray(), p.length());
            stemf.stem();
            p = stemf.toString();
          System.out.println(p+ " ");
          
            if(p.length()>0 && !stopw.checkStopWord(p) && !stopw.checkStopWord(p2))
            {
               if(!index.containsKey(p)) 
            {
                  TreeMap<Integer, List<Pair<Integer, String>>> f = new TreeMap<Integer, List<Pair<Integer, String>>>();  
                  Pair<Integer,String> r = new Pair<Integer,String>(numberword,"B");
                  List<Pair<Integer,String>> ls = new LinkedList<Pair<Integer,String>>();
                  ls.add(r);
                  f.put(id,ls);
                  index.put(p,f);
               }
               else{
                  TreeMap<Integer, List<Pair<Integer, String>>> f = index.get(p);
                  if(!f.containsKey(id))
                  {
                     Pair<Integer,String> r = new Pair<Integer,String>(numberword,"B");
                     List<Pair<Integer,String>> ls = new LinkedList<Pair<Integer,String>>();
                     ls.add(r);
                     f.put(id,ls);
                     index.put(p,f);
                  }
                  else{
                     List<Pair<Integer,String>> ls = f.get(id);
                     Pair<Integer,String> r = new Pair<Integer,String>(numberword,"B");
                     
                     ls.add(r);
                     f.put(id,ls);
                     index.put(p,f);
                  }
               }
                numberword++;
                   
            }
   
            
        }

      } else if (isRevision) {
     //    //System.out.println("Revision: " + new String(ch, start, length));
      } else if (isId && !isRevision) {
          String s2 = new String(ch, start, length);
         s2=s2.toLowerCase();
         id=Integer.valueOf(s2);
         ////System.out.println(s2);
        
      }
   }
}

