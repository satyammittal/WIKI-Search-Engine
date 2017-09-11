import java.io.*;
import java.util.*;
public class fetchPostingList{
	private String file;
	private String word;
	public fetchPostingList(String file)
	{
		this.file = file;
	}
	public TreeMap<String,String> run(HashSet<String> word) throws IOException
	{
		String ans = "";
		TreeMap<String,String> newt = new TreeMap<String,String>();
		for(String s1:word){

		String a2f = "$.txt";
		if(s1.length()!=1)
			a2f = s1.substring(0,2);
		String a1f = "" + s1.charAt(0);
		String file1 = "index/"+a1f+".txt";
		String file2 = "index/"+a2f+".txt";
		File f1 = new File(file1);
		File f2 = new File(file2);
		if(f2.exists() && !f2.isDirectory())
		{
			file = file2;
		}
		else if(f1.exists() && !f1.isDirectory())
		{
			file = file1;
		}
		else
		{
			word.remove(s1);
			continue;
		}
 		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
	    for(String line; (line = br.readLine()) != null; ) {
	    	String line_head = line.substring(0,line.indexOf(':'));
	    	String line_foot = line.substring(line.indexOf(':')+1);
	    	if(word.contains(line_head))
	    	{
	    		newt.put(line_head, line_foot);
	    	}
	        // process the line.
	    }
	    // line is not visible here.
		}
	}
		//System.out.println(newt.toString());
		return newt;
	}
	public TreeMap<String,String> fetchDocTitle(List<String> word) throws IOException
	{
		String ans = "";
		TreeMap<String,String> newt = new TreeMap<String,String>();
		try(BufferedReader br = new BufferedReader(new FileReader("temp/mappings.txt"))) {
	    for(String line; (line = br.readLine()) != null; ) {
	    	String line_head = line.substring(0,line.indexOf(' '));
	    	String line_foot = line.substring(line.indexOf(' ')+1);
	    	if(word.contains(line_head))
	    	{
	    		newt.put(line_head, line_foot);
	    	}
	        // process the line.
	    }
	    // line is not visible here.
		}
		//System.out.println(newt.toString());
		return newt;
	}
	public static String readFromFile(final File file, long byteStart,
			int length) throws IOException {
		final FileInputStream fis = new FileInputStream(file);
		fis.skip(byteStart);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = br.readLine();
		line =line.substring(0,line.indexOf("$"));
		br.close();
		fis.close();
		return line;
	}
}