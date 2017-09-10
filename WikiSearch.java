
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.*;
import java.util.*;
import java.lang.*;

public class WikiSearch {
	public static Integer NUMBERDOC = 722237;
	public static double cosineSimilarity(ArrayList<Double> vectorA, ArrayList<Double> vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    if(vectorA.size()==1)
	    	return vectorA.get(0);
	    for (int i = 0; i < vectorA.size(); i++) {
	        dotProduct += vectorA.get(i) * vectorB.get(i);
	        normA += Math.pow(vectorA.get(i), 2);
	        normB += Math.pow(vectorB.get(i), 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
	private static HashMap<String, Double> sortByfValue(HashMap<String, Double> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Double>> list =
                new LinkedList<HashMap.Entry<String, Double>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        HashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }


        return sortedMap;
    }  
	public static void main(final String[] argv) throws Exception {
		//Main class that takes the file name as a command line argument.
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
		fetchPostingList g = new fetchPostingList("temp/block-000.txt");
		while(true){
			String str=reader.readLine();
			if(str.equals("0")) break;
			long start=System.currentTimeMillis();
			String g1="";
			String tr="";
			TreeMap<String, List<String>> query = new TreeMap<String,List<String>>();
			int htest=0;
			List<String> fr = new LinkedList<String>();
			HashSet<String> set = new HashSet<String>();
			String fd="";
			HashMap<String, Integer> vset = new HashMap<String, Integer>();
			Stemmer stemf = new Stemmer();
			for(char g2: str.toCharArray())
			{
						
				if(g2!=':' && htest == 1)
				{

					g1=g1+""+g2;
				}
				if(g2==':')
				{
					if(g1 != "")
					{
						g1=g1.substring(0,g1.length()-1);
						g1=g1.toLowerCase();
						String[] yt = g1.split("\\s+");
						List<String> tfr = new LinkedList<String>();
						for(String yr: yt)
						{
							stemf.add(yr.toCharArray(), yr.length());
            				stemf.stem();
            				yr = stemf.toString();
            				stemf.clear();
							set.add(yr+"$"+tr.toUpperCase());
							if(!vset.containsKey(yr+"$"+tr.toUpperCase()))
								vset.put(yr+"$"+tr.toUpperCase(),1);
							else
								vset.put(yr+"$"+tr.toUpperCase(),vset.get(yr+"$"+tr.toUpperCase())+1);
							tfr.add(yr);
						}
						query.put(tr,tfr);
					}
					tr = ""+fd;
					g1="";
					htest=1;
				}
				fd=""+g2;
			}
			if(g1 != "")
					{
							//System.out.println(tr);
			
						g1=g1.substring(0,g1.length());
						g1=g1.toLowerCase();
						stemf.add(g1.toCharArray(), g1.length());
            			stemf.stem();

            			g1 = stemf.toString();
						stemf.clear();
						String[] yt = g1.split("\\s+");
						List<String> tfr = new LinkedList<String>();
						for(String yr: yt)
						{
							stemf.add(yr.toCharArray(), yr.length());
            				stemf.stem();
            				yr = stemf.toString();
            				stemf.clear();
							set.add(yr+"$"+tr.toUpperCase());
							if(!vset.containsKey(yr+"$"+tr.toUpperCase()))
								vset.put(yr+"$"+tr.toUpperCase(),1);
							else
								vset.put(yr+"$"+tr.toUpperCase(),vset.get(yr+"$"+tr.toUpperCase())+1);
					
							tfr.add(yr);
						}
						query.put(tr,tfr);
					}
			HashMap<String,Integer> hp = new HashMap<String,Integer>();
			int co=0;
			for(String s:set)
			{
				System.out.println(s);
				hp.put(s,co);
				co++;
			}
			TreeMap<String,String> tre = g.run(set);
			ArrayList<Double> parr1 = new ArrayList<Double>(set.size());
			for (int i = 0; i < set.size(); i++) {
  							parr1.add(0.0);
			}	
			TreeMap<String,ArrayList<Double>> vect = new TreeMap<String,ArrayList<Double>>();
			for(Map.Entry<String, String> r: tre.entrySet())
			{
				String r1=r.getKey();
				String r2=r.getValue();
				String[] sr2= r2.split(",");
				int ndoc = sr2.length+1;
				for(String sr3:sr2)
				{
					String[] split_doc = sr3.split("-");
					String doc_id = split_doc[0];
					Integer number = Integer.valueOf(split_doc[1]);
					Integer index = hp.get(r1);
							//System.out.println(String.valueOf(index));
					ArrayList<Double> arr1 = new ArrayList<Double>(set.size());
						
					if(vect.containsKey(doc_id))
						arr1 = vect.get(doc_id);
					else
					{
						for (int i = 0; i < set.size(); i++) {
  							arr1.add(0.0);
						}
					}
					arr1.set(index,Math.log(1.0+(NUMBERDOC*1.0)/(ndoc*1.0))*(1.0+Math.log(number)));
					vect.put(doc_id,arr1);
				}
				parr1.set(hp.get(r1),Math.log(1.0+(NUMBERDOC*1.0)/(ndoc*1.0))*(1.0+Math.log(vset.get(r1))));
				//	System.out.println(vect.toString());
				//	System.out.println(parr1.toString());
				
			}
			HashMap<String,Double> finalv = new HashMap<String,Double>(); 
			for(Map.Entry<String, ArrayList<Double>> r: vect.entrySet())
			{
				String doc_id = r.getKey();
			//	System.out.println(r.toString());
			//	System.out.println(parr1.toString());
	
				Double cosinevalue = cosineSimilarity(r.getValue(),parr1);
				finalv.put(doc_id,cosinevalue);
			//	System.out.println(cosinevalue.toString());
			}
			HashMap<String,Double> sfinal = sortByfValue(finalv);
			int countg=15;
			for(Map.Entry<String, Double> r: sfinal.entrySet())
			{
				if(countg>0)
				System.out.println(r.getKey());
			countg-=1;
			}
			System.out.println("total "+((System.currentTimeMillis()-start))+" ms");
		}
		reader.close();
	}
}