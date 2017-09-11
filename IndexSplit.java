
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.*;
import java.util.*;
import java.lang.*;

public class IndexSplit {
	
	public static void main(final String[] argv) throws Exception {
	String index = "temp/block.txt";
	String chf = "$";
	
		boolean str = false;
		File statText = new File("index/"+"$.txt");
    statText.getParentFile().mkdirs();
    statText.createNewFile();
     FileOutputStream is = new FileOutputStream(statText);
     OutputStreamWriter osw = new OutputStreamWriter(is);    
     Writer w = new BufferedWriter(osw);
    
		try(BufferedReader br = new BufferedReader(new FileReader(index))) {
	    for(String line; (line = br.readLine()) != null; ) {
	    	String line_head = line.substring(0,line.indexOf(':'));
	    	String bla = "$";
	    	if(line_head.length()==1)
	    		bla = ""+line_head.charAt(0);
	    	else
	    		bla = line.substring(0,2);
	    	if(line_head != null && !bla.equals(chf))
	    	{
	    		chf = bla;
	    		w.close();
	    		statText = new File("index/"+chf+".txt");
	    		statText.createNewFile();
                is = new FileOutputStream(statText);
     			osw = new OutputStreamWriter(is);
     			w = new BufferedWriter(osw);
     			str = false; 
	    	}
	    	if(str)
	    		w.write('\n');
	    	str=true;
	    	w.write(line);
	    	// process the line.
	    }
	    // line is not visible here.
		}		
	}
}
