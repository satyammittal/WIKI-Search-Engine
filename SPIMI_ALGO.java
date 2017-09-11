import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.Attributes;
import java.time.Duration;
import java.time.Instant;
import java.io.FileReader;
import java.io.FileWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.regex.Pattern;

public class SPIMI_ALGO {
	public static String valcategory="$1";
   	public static int subfilenumber = 0;
   	public static int tempnumber=0;
   	static String word1 ;
    static String word2 ;
    static String value1 ;
	static String value2 ;
   public static void main(String[] args) throws Exception {
    mergeblocks();
  }
	public static void createblock (TreeMap<String, SortedSet<Pair<Integer, String>>> indexer) throws Exception
	{
		long curTime=System.currentTimeMillis();

	  File statText = new File("temp/block-"+subfilenumber+".txt");
      statText.getParentFile().mkdirs();
      statText.createNewFile();
      FileOutputStream is = new FileOutputStream(statText);
      OutputStreamWriter osw = new OutputStreamWriter(is);    
      Writer w = new BufferedWriter(osw);
      subfilenumber++;
      String key2 = "%$%";
      boolean firstline = true;
      for(Map.Entry<String, SortedSet<Pair<Integer, String>>> entry : indexer.entrySet()) {
         String key = entry.getKey();
         valcategory = key;
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
      System.out.println(subfilenumber + " Block created in "+((System.currentTimeMillis()-curTime)/1000)+" seconds.");

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
   public static void setValues(String input1 ,String input2){
                word1=input1.substring(0,input1.indexOf(":"));
                value1=input1.substring(input1.indexOf(":")+1);
                word2=input2.substring(0,input2.indexOf(":"));
                value2=input2.substring(input2.indexOf(":")+1);
	}
   public static void mergeblocks() throws Exception
   {
   		File folder = new File("temp/");
   		File[] listOfFiles = folder.listFiles();
   		
      Arrays.sort(listOfFiles, new Comparator<File>(){
        @Override
        public int compare(File f1, File f2) {
            String s1 = f1.getName().substring(f1.getName().indexOf("-")+1,f1.getName().indexOf("."));
            String s2 = f2.getName().substring(f2.getName().indexOf("-")+1,f2.getName().indexOf("."));
            return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));  
        }
      });
           for(File t:listOfFiles)
          System.out.println(t.getName());
   
      String gh=""; 
   		while(listOfFiles.length>=2)
   		{
   			gh+="0";
   			int r=listOfFiles.length/2;
   			tempnumber = subfilenumber%1;
   			for(int i=0;i<r;i++)
   			{
   						File filepath1=listOfFiles[2*i] ;
                        File filepath2=listOfFiles[2*i+1] ;
                        System.out.println(filepath1 +" "+filepath2);
                        try {
                                FileWriter ostream = new FileWriter("temp/block-"+gh+i+".txt");
                                BufferedWriter out = new BufferedWriter(ostream);
                                BufferedReader br1 = new BufferedReader(new FileReader(filepath1.getAbsolutePath()),10*1024);
                                BufferedReader br2 = new BufferedReader(new FileReader(filepath2.getAbsolutePath()),10*1024);
                                String line1=null ;
                                String line2=null ;
                                line1 = br1.readLine();
                                line2 = br2.readLine();
                                while(true){
                                        setValues(line1,line2);
                                        if(word1.compareTo(word2) < 0){
                                                out.write(line1);
                                                out.newLine();
                                                line1=br1.readLine();
                                                if(line1==null)
                                                        break ;
                                        }
                                        if(word1.compareTo(word2) > 0){
                                                out.write(line2);
                                                out.newLine();
                                                line2=br2.readLine();
                                                if(line2==null)
                                                        break ;
                                        }

                                        if(word1.compareTo(word2) == 0){
                                                String line = line1+ "," + value2;
                                                out.write(line);
                                                out.newLine();
                                                line1=br1.readLine();
                                                line2=br2.readLine();
                                                if(line2==null || line1==null)
                                                        break ;
                                        }
                                }

                                if(line1==null){
                                        while(line2!=null){
                                                out.write(line2);
                                                out.newLine();
                                                line2=br2.readLine();
                                        }
                                }

                                if(line2==null){
                                        while(line1!=null){
                                                out.write(line1);
                                                out.newLine();
                                                line1=br1.readLine();
                                        }
                                }
                                br1.close();
                                br2.close();
                                out.close();
                                ostream.close();
                        }catch(IOException e){
                                e.printStackTrace();
                                System.exit(0) ;
                        }
                        filepath1.delete();
                        filepath2.delete();
   			}
   			listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles, new Comparator<File>(){
        @Override
        public int compare(File f1, File f2) {
            String s1 = f1.getName().substring(f1.getName().indexOf("-")+1,f1.getName().indexOf("."));
            String s2 = f2.getName().substring(f2.getName().indexOf("-")+1,f2.getName().indexOf("."));
            return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));  
        }
        });
        for(File t:listOfFiles)
          System.out.println(t.getName());
   		}
   }
}