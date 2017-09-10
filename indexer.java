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
   public static TreeMap<String, SortedSet<Pair<Integer, String>>> indexer;
   public static String valcategory="$1";
   public static StringBuffer bodyText = new StringBuffer("");;
   public static void main(String[] args) throws Exception {

     try {
         Instant start = Instant.now();
         File inputFile = new File(args[0]);
         SAXParserFactory factory = SAXParserFactory.newInstance();
         indexer = new TreeMap<String, SortedSet<Pair<Integer, String>>>();
         SAXParser saxParser = factory.newSAXParser();
         UserHandler userhandler = new UserHandler();
         userhandler.sample(tf, idf, indexer, bodyText);
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
      File statText = new File(args[1]);
      FileOutputStream is = new FileOutputStream(statText);
      OutputStreamWriter osw = new OutputStreamWriter(is);    
      Writer w = new BufferedWriter(osw);

      //for(Map.Entry m:mat_sorted.entrySet()){  
       //
                  ////System.out.println(m.getKey()+" "+m.getValue());  
      //}
      if(indexer.size()!=0)
            {
              try {
                SPIMI_ALGO.createblock(indexer);
              } catch (IOException e) {
                e.printStackTrace();
              }
                indexer.clear();
            }
        SPIMI_ALGO.mergeblocks();
      String key2 = "%$%";
      boolean firstline = true;
      for(Map.Entry<String, SortedSet<Pair<Integer, String>>> entry : indexer.entrySet()) {
         String key = entry.getKey();
         valcategory = key;
         //if(!firstline && keycheck(key,key2)==false)
          if(!firstline)
            w.write("\n");
         firstline=false; 
         SortedSet<Pair<Integer, String>> pr2 = entry.getValue();
         w.write(valcategory + ":");
         //for(Map.Entry<Integer, SortedSet<Pair<Integer, String>>> entry2 : value.entrySet()) 
         //{
            //Integer doc = entry2.getKey();
            //w.write(doc.toString() + ":");
           // SortedSet<Pair<Integer, String>> pr2 = entry.getValue();
            boolean ft=true;
            String dq="";
            for (Pair<Integer, String> pr:pr2)
            {
               Integer in = pr.getFirst();
               String qr = pr.getSecond();
               if(!ft)
                  dq=",";
               w.write(dq+in.toString()+"-"+qr); 
              ft=false;
            }
            key2=key; 
          //   w.write(";"); 
         //}
              
      }
      w.close();
      Instant end = Instant.now();
      Duration timeElapsed = Duration.between(start, end);
      System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");
      ////System.out.println(indexer);  
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   public static boolean keycheck(String key,String key2){
    String PATTERN_TOKEN = "$";
         //String s1 = s2.replaceAll("[-:()/^[!|=,?._'{}@+\\[\\]]]", " ");
         boolean fd=true;
         String tg=".";
        String fkey=".",fkey2="..";
         HashSet<String> parts2 = new HashSet<String>();
         final StringTokenizer normalTokenizer = new StringTokenizer(key,PATTERN_TOKEN);
            while(normalTokenizer.hasMoreTokens()){
              String sr= normalTokenizer.nextToken().trim();
              if(fd)
              {
                fkey=sr;
              }
              else{
                tg=sr;
              }
              fd=false;
         }
         fd=true;
         final StringTokenizer normalTokenizer2 = new StringTokenizer(key2,PATTERN_TOKEN);
            while(normalTokenizer2.hasMoreTokens()){
             String sr= normalTokenizer2.nextToken().trim();
           
              if(fd)
              {
                fkey2=sr;
              }
              fd=false;
         }
         if(fkey.equalsIgnoreCase(fkey2))
         {
          valcategory="|$" + tg;
          return true;
         }  
         else
         {
          return false;
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
   StringBuffer buffer;
   TreeMap<String, SortedSet<Pair<Integer, String>>> index;
   Stemmer stemf;
   Integer id=-1;
   Integer numberword = 0;
   GenerateStopwords stopw = new GenerateStopwords();
   int counter=1;
   public void sample(HashMap<String,Integer> tf,HashMap<String,Integer> idf,TreeMap<String, SortedSet<Pair<Integer, String>>> indexer, StringBuffer bfr){
      this.tf=tf;
      this.idf=idf;
      this.index=indexer;
      this.buffer=bfr;
      stemf = new Stemmer();
      stopw.generatefromfile("stopwords.txt");
    
   }
   @Override
   public void startElement(String uri, 
   String localName, String qName, Attributes attributes) throws SAXException {
      counter++;
   //      ////System.out.println("Roll No : " + qName);
      if (qName.equalsIgnoreCase("revision")) {
         isRevision = true;
      } else if (qName.equalsIgnoreCase("title")) {
           buffer = new StringBuffer();
         isTitle = true;
      } else if (qName.equalsIgnoreCase("text")) {
           buffer = new StringBuffer();
       
         isBody = true;
         numberword = 0;
      }
      else if (qName.equalsIgnoreCase("id")) {
      //   String rollNo = attributes.getValue("id");
       //  ////System.out.println("Roll No : " + rollNo);
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
         String s2 = buffer.toString();
         s2=s2.toLowerCase();
         extractLinks(s2);
         extractCategories(s2);
         extractReferences(s2);
         extractInfoBox(s2);
         s2=deleteCitation(s2);
         String PATTERN_TOKEN = "\\$%{}[]()`<>='&:,;/.~ ;*\n|\"^_-+!?#\t@";
         //String s1 = s2.replaceAll("[-:()/^[!|=,?._'{}@+\\[\\]]]", " ");
         HashSet<String> parts2 = new HashSet<String>();
         HashMap<String, Integer> countw= new HashMap<String,Integer>();
         final StringTokenizer normalTokenizer = new StringTokenizer(s2,PATTERN_TOKEN);
            while(normalTokenizer.hasMoreTokens()){
              String nk=normalTokenizer.nextToken().trim();
               parts2.add(nk);
               if(countw.get(nk)==null){
                  countw.put(nk,1);
               }
               else{
                  countw.put(nk,countw.get(nk)+1);
               }
            }
         Pattern rp = Pattern.compile("^[a-zA-Z0-9]*$");
         List<String> parts = new ArrayList<String>();
       for(String p:parts2)
         {
            Matcher m = rp.matcher(p);
         if (m.find())
            parts.add(p);
         }

         ////System.out.println(parts.toString());
         for(String p:parts){
            String p2=p;
            stemf.add(p.toCharArray(), p.length());
            stemf.stem();
            p = stemf.toString();
          
            if(p.length()>0 && !stopw.checkStopWord(p) && !stopw.checkStopWord(p2))
            {
              String p3=p;
               p = p + "$T";
              //System.out.println(p+ " ");
               if(!index.containsKey(p)) 
            {
                  Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                  SortedSet<Pair<Integer,String>> ls = new TreeSet<Pair<Integer,String>>();
                  ls.add(r);
                 // index.put(id,ls);
                  index.put(p,ls);
               }
               else{
              
                     SortedSet<Pair<Integer,String>> ls = index.get(p);
                     Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                     
                     ls.add(r);
                  //   f.put(id,ls);
                     index.put(p,ls);
                  }
               }
                numberword++;
                   
            }
            if(index.size()>=500000)
            {
              try {
                SPIMI_ALGO.createblock(index);
              } catch (Exception e) {
                e.printStackTrace();
              }
                index.clear();
            }
      }
      else if(tag.equalsIgnoreCase("text"))
      {
        //System.out.println(String.valueOf(index.size()));
         isBody = false;
         String s2 = buffer.toString();
         s2=s2.toLowerCase();
         extractLinks(s2);
         extractCategories(s2);
         extractReferences(s2);
         extractInfoBox(s2);
         s2=deleteCitation(s2);
         String PATTERN_TOKEN = "\\$%{}[]()`<>='&:,;/.~ ;*\n|\"^_-+!?#\t@";
         //String s1 = s2.replaceAll("[-:()/^[!|=,?._'{}@+\\[\\]]]", " ");
         HashSet<String> parts2 = new HashSet<String>();
         HashMap<String, Integer> countw= new HashMap<String,Integer>();
         final StringTokenizer normalTokenizer = new StringTokenizer(s2,PATTERN_TOKEN);
            while(normalTokenizer.hasMoreTokens()){
              String nk=normalTokenizer.nextToken().trim();
               parts2.add(nk);
               if(countw.get(nk)==null){
                  countw.put(nk,1);
               }
               else{
                  countw.put(nk,countw.get(nk)+1);
               }
            }
         Pattern rp = Pattern.compile("^[a-zA-Z0-9]*$");
         List<String> parts = new ArrayList<String>();
       for(String p:parts2)
         {
            Matcher m = rp.matcher(p);
         if (m.find())
            parts.add(p);
         }

         ////System.out.println(parts.toString());
         for(String p:parts){
            String p2=p;
            stemf.add(p.toCharArray(), p.length());
            stemf.stem();
            p = stemf.toString();
          
            if(p.length()>0 && !stopw.checkStopWord(p) && !stopw.checkStopWord(p2) && p.charAt(0)!='0')
            {
              String p3=p;
               p = p + "$B";
              //System.out.println(p+ " ");
               if(!index.containsKey(p)) 
            {
                  Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                  SortedSet<Pair<Integer,String>> ls = new TreeSet<Pair<Integer,String>>();
                  ls.add(r);
                 // index.put(id,ls);
                  index.put(p,ls);
               }
               else{
              
                     SortedSet<Pair<Integer,String>> ls = index.get(p);
                     Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                     
                     ls.add(r);
                  //   f.put(id,ls);
                     index.put(p,ls);
                  }
               }
                numberword++;
                   
            }
             if(index.size()>=500000)
            {
              try {
                SPIMI_ALGO.createblock(index);
              } catch (Exception e) {
                e.printStackTrace();
              }
                index.clear();
            }
         ////System.out.println(String.valueOf(id));
      }
   }

   @Override
   public void characters(char ch[], int start, int length) throws SAXException {
      set=new HashSet<String>();
      if (isTitle) {
         String s2 = new String(ch, start, length);
         buffer.append(s2);
       //  ////System.out.println("Title: " 
         //   + new String(ch, start, length));
      } else if (isBody) {
          String s2 = new String(ch, start, length);
         buffer.append(s2);
         
      } else if (isRevision) {
     //    ////System.out.println("Revision: " + new String(ch, start, length));
      } else if (isId && !isRevision) {
          String s2 = new String(ch, start, length);
         s2=s2.toLowerCase();
         id=Integer.valueOf(s2);
         //////System.out.println(s2);
        
      }
   }

   public void extractLinks(final String content){
      //Tokenize the link. [[user innovation]]
      final Pattern linkPattern = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE);
      final Matcher matcher = linkPattern.matcher(content);
      while(matcher.find()) {
         final String [] match = matcher.group(1).split("\\|");
         if(match == null || match.length == 0) continue;
         final String link = match[0];
         if(link.contains(":") == false) {
         String s2=link.toLowerCase();
         //System.out.println(s2);
         String PATTERN_TOKEN = "\\$%{}[]()`<>='&:,;/.~ ;*\n|\"^_-+!?#\t@";
         //String s1 = s2.replaceAll("[-:()/^[!|=,?._'{}@+\\[\\]]]", " ");
         HashSet<String> parts2 = new HashSet<String>();
          HashMap<String, Integer> countw= new HashMap<String,Integer>();
         final StringTokenizer normalTokenizer = new StringTokenizer(s2,PATTERN_TOKEN);
            while(normalTokenizer.hasMoreTokens()){
              String nk=normalTokenizer.nextToken().trim();
               parts2.add(nk);
               if(countw.get(nk)==null){
                  countw.put(nk,1);
               }
               else{
                  countw.put(nk,countw.get(nk)+1);
               }
            }
         Pattern rp = Pattern.compile("^[a-zA-Z0-9]*$");
         List<String> parts = new ArrayList<String>();
       for(String p:parts2)
         {
            Matcher m = rp.matcher(p);
         if (m.find())
            parts.add(p);
         }
         ////System.out.println(parts.toString());
         for(String p:parts){
            String p2=p;
            stemf.add(p.toCharArray(), p.length());
            stemf.stem();
            p = stemf.toString();
          
            if(p.length()>0 && !stopw.checkStopWord(p) && !stopw.checkStopWord(p2) && p.charAt(0)!='0')
            {
               String p3=p;
               p = p + "$L";
              //System.out.println(p+ " ");
               if(!index.containsKey(p)) 
            {

                  Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                  SortedSet<Pair<Integer,String>> ls = new TreeSet<Pair<Integer,String>>();
                  ls.add(r);
                 // index.put(id,ls);
                  index.put(p,ls);
               }
               else{
              
                     SortedSet<Pair<Integer,String>> ls = index.get(p);
                     Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                     
                     ls.add(r);
                  //   f.put(id,ls);
                     index.put(p,ls);
                  }
               }
                   
            }
         }
      }
   }
   
   public void extractCategories(final String content){
      //Tokenize the categories
      final Pattern categoryPattern = Pattern.compile("\\[\\[category:(.*?)\\]\\]", Pattern.MULTILINE);
      final Matcher matcher = categoryPattern.matcher(content);
      while(matcher.find()) {
         final String [] match = matcher.group(1).split("\\|");
         String s2=match[0].toLowerCase();
         //System.out.println(s2);
         String PATTERN_TOKEN = "\\$%{}[]()`<>='&:,;/.~ ;*\n|\"^_-+!?#\t@";
         //String s1 = s2.replaceAll("[-:()/^[!|=,?._'{}@+\\[\\]]]", " ");
         HashSet<String> parts2 = new HashSet<String>();
          HashMap<String, Integer> countw= new HashMap<String,Integer>();
         final StringTokenizer normalTokenizer = new StringTokenizer(s2,PATTERN_TOKEN);
            while(normalTokenizer.hasMoreTokens()){
              String nk=normalTokenizer.nextToken().trim();
               parts2.add(nk);
               if(countw.get(nk)==null){
                  countw.put(nk,1);
               }
               else{
                  countw.put(nk,countw.get(nk)+1);
               }
            }
         Pattern rp = Pattern.compile("^[a-zA-Z0-9]*$");
         List<String> parts = new ArrayList<String>();
       for(String p:parts2)
         {
            Matcher m = rp.matcher(p);
         if (m.find())
            parts.add(p);
         }
         ////System.out.println(parts.toString());
         for(String p:parts){
            String p2=p;
            stemf.add(p.toCharArray(), p.length());
            stemf.stem();
            p = stemf.toString();
          
            if(p.length()>0 && !stopw.checkStopWord(p) && !stopw.checkStopWord(p2) && p.charAt(0)!='0')
            {
              String p3=p;
               p = p + "$C";
              //System.out.println(p+ " ");
               if(!index.containsKey(p)) 
            {
                  Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                  SortedSet<Pair<Integer,String>> ls = new TreeSet<Pair<Integer,String>>();
                  ls.add(r);
                 // index.put(id,ls);
                  index.put(p,ls);
               }
               else{
              
                     SortedSet<Pair<Integer,String>> ls = index.get(p);
                     Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                     
                     ls.add(r);
                  //   f.put(id,ls);
                     index.put(p,ls);
                  }
               }
            }
      //   extractToken(match[0],'C');
      }
   }
   
   public void extractInfoBox(final String content){
      //Tokenize the infobox.
      final String infoBoxPatterm = "{{infobox";
      
      //Find the start pos and end pos of info box.
       int startPos = content.indexOf(infoBoxPatterm);
       if(startPos < 0) return ;
       int bracketCount = 2;
       int endPos = startPos + infoBoxPatterm.length();
       for(; endPos < content.length(); endPos++) {
         switch(content.charAt(endPos)) {
           case '}':
             bracketCount--;
             break;
           case '{':
             bracketCount++;
             break;
           default:
         }
         if(bracketCount == 0) break;
       }
       if(endPos+1 >= content.length()) return;

       //Filter the infobox
       String infoBoxText = content.substring(startPos, endPos+1);
       infoBoxText = deleteCitation(infoBoxText);      
       infoBoxText = infoBoxText.replaceAll("&gt;", ">");
       infoBoxText = infoBoxText.replaceAll("&lt;", "<");
       infoBoxText = infoBoxText.replaceAll("<ref.*?>.*?</ref>", " ");
       infoBoxText = infoBoxText.replaceAll("</?.*?>", " ");
      
        String s2=infoBoxText.toLowerCase();
         //System.out.println(s2);
         String PATTERN_TOKEN = "\\$%{}[]()`<>='&:,;/.~ ;*\n|\"^_-+!?#\t@";
         //String s1 = s2.replaceAll("[-:()/^[!|=,?._'{}@+\\[\\]]]", " ");
         HashSet<String> parts2 = new HashSet<String>();
          HashMap<String, Integer> countw= new HashMap<String,Integer>();
         final StringTokenizer normalTokenizer = new StringTokenizer(s2,PATTERN_TOKEN);
            while(normalTokenizer.hasMoreTokens()){
              String nk=normalTokenizer.nextToken().trim();
               parts2.add(nk);
               if(countw.get(nk)==null){
                  countw.put(nk,1);
               }
               else{
                  countw.put(nk,countw.get(nk)+1);
               }
            }
         Pattern rp = Pattern.compile("^[a-zA-Z0-9]*$");
         List<String> parts = new ArrayList<String>();
       for(String p:parts2)
         {
            Matcher m = rp.matcher(p);
         if (m.find())
            parts.add(p);
         }
         ////System.out.println(parts.toString());
         for(String p:parts){
            String p2=p;
            stemf.add(p.toCharArray(), p.length());
            stemf.stem();
            p = stemf.toString();
          
            if(p.length()>0 && !stopw.checkStopWord(p) && !stopw.checkStopWord(p2))
            {
              String p3=p;
               p = p + "$I";
              //System.out.println(p+ " ");
               if(!index.containsKey(p)) 
            {
                  Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                  SortedSet<Pair<Integer,String>> ls = new TreeSet<Pair<Integer,String>>();
                  ls.add(r);
                 // index.put(id,ls);
                  index.put(p,ls);
               }
               else{
              
                     SortedSet<Pair<Integer,String>> ls = index.get(p);
                     Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                     
                     ls.add(r);
                  //   f.put(id,ls);
                     index.put(p,ls);
                  }
               }
            }
   }
   
   public String deleteCitation(final String content) {
      //Deletes the citation from the content.
      final String CITE_PATTERN = "{{cite";
      
      //Find the start pos and end pos of citation.
       int startPos = content.indexOf(CITE_PATTERN);
       if(startPos < 0) return content;
       int bracketCount = 2;
       int endPos = startPos + CITE_PATTERN.length();
       for(; endPos < content.length(); endPos++) {
         switch(content.charAt(endPos)) {
           case '}':
             bracketCount--;
             break;
           case '{':
             bracketCount++;
             break;
           default:
         }
         if(bracketCount == 0) break;
       }
             String soltxt = "";
        if(content!=null && startPos>=1 && endPos>=0)
       {
        final String text = content.substring(0, startPos-1) + content.substring(endPos);
        soltxt = text;
      }
       //Discard the citation and search for remaining citations.
       return deleteCitation(soltxt); 
   }
   
   public String extractReferences(final String content) {
      //Extracts the citation from the content.
      final String CITE_PATTERN = "{{cite";
      
      //Find the start pos and end pos of citation.
       int startPos = content.indexOf(CITE_PATTERN);
       if(startPos < 0) return content;
       int bracketCount = 2;
       int endPos = startPos + CITE_PATTERN.length();
       for(; endPos < content.length(); endPos++) {
         switch(content.charAt(endPos)) {
           case '}':
             bracketCount--;
             break;
           case '{':
             bracketCount++;
             break;
           default:
         }
         if(bracketCount == 0) break;
       }
       
       //Extract the citation and search for remaining citations.
       //extractToken(content.substring(startPos, endPos),'R');
      String s2=content.substring(startPos, endPos);
         //System.out.println(s2);
         String PATTERN_TOKEN = "\\$%{}[]()`<>='&:,;/.~ ;*\n|\"^_-+!?#\t@";
         //String s1 = s2.replaceAll("[-:()/^[!|=,?._'{}@+\\[\\]]]", " ");
         HashSet<String> parts2 = new HashSet<String>();
          HashMap<String, Integer> countw= new HashMap<String,Integer>();
         final StringTokenizer normalTokenizer = new StringTokenizer(s2,PATTERN_TOKEN);
            while(normalTokenizer.hasMoreTokens()){
              String nk=normalTokenizer.nextToken().trim();
               parts2.add(nk);
               if(countw.get(nk)==null){
                  countw.put(nk,1);
               }
               else{
                  countw.put(nk,countw.get(nk)+1);
               }
            }
         Pattern rp = Pattern.compile("^[a-zA-Z0-9]*$");
         List<String> parts = new ArrayList<String>();
       for(String p:parts2)
         {
            Matcher m = rp.matcher(p);
         if (m.find())
            parts.add(p);
         }
         ////System.out.println(parts.toString());
         for(String p:parts){
            String p2=p;
            stemf.add(p.toCharArray(), p.length());
            stemf.stem();
            p = stemf.toString();
          
            if(p.length()>0 && !stopw.checkStopWord(p) && !stopw.checkStopWord(p2) && p.charAt(0)!='0')
            {
              String p3=p;
               p = p + "$R";
              //System.out.println(p+ " ");
               if(!index.containsKey(p)) 
            {
                  Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                  SortedSet<Pair<Integer,String>> ls = new TreeSet<Pair<Integer,String>>();
                  ls.add(r);
                 // index.put(id,ls);
                  index.put(p,ls);
               }
               else{
              
                     SortedSet<Pair<Integer,String>> ls = index.get(p);
                     Pair<Integer,String> r = new Pair<Integer,String>(id,String.valueOf(countw.get(p2)));
                     
                     ls.add(r);
                  //   f.put(id,ls);
                     index.put(p,ls);
                  }
               }
            }
            String soltxt = "";
        if(content!=null && startPos>=1 && endPos>=0)
       {
        final String text = content.substring(0, startPos-1) + content.substring(endPos);
        soltxt = text;
      }
       return extractReferences(soltxt); 
   }

}

