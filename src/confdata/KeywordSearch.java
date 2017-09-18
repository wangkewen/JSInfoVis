package confdata;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class KeywordSearch {
	//*.java file <filename,<config name, line number>>
	private HashMap<String,HashMap<String,List<String>>> javafiles;
	// config information <config name, config type "#:commented !:not occur in cassandra.yaml">
	private HashMap<String,String> configsinfo;
	// config full information <config name, config type "#:commented !:not occur in cassandra.yaml
	// @: occur in *.java file &: related with other config" >
	private HashMap<String,String> configsfullinfo;
	private TreeMap<String,String> sortedConfigsfullinfo;
	// config full relation <configname#configtype,List<configname#configtype>>
	private HashMap<String,List<String>> configsfullrelation;
	private TreeMap<String,List<String>> sortedConfigsfullrelation;
	private String filepath; // cassandra source file path directory
	private String filename; // cassandra Config.java file sub-path and name
	private String configfile; // config file path and name cassandra.yaml
	private long searchTime; // analysis time cost
	private String resultFile;
	public KeywordSearch(String filepath,String filename,String configfile,String resultFile){
		javafiles = new HashMap<String,HashMap<String,List<String>>>();
		configsinfo = new HashMap<String,String>();
		configsfullinfo = new HashMap<String,String>();
		sortedConfigsfullinfo = new TreeMap<String,String>();
		configsfullrelation = new HashMap<String,List<String>>();
		sortedConfigsfullrelation = new TreeMap<String,List<String>>();
		setFilepath(filepath);
		setFilename(filename);
		setConfigfile(configfile);
		setResultFile(resultFile);
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args){
		String filepath="cassandra/apache-cassandra-2.0.10-src/src/java/";
		String filename="org/apache/cassandra/config/Config.java";
		String configfile="cassandra/apache-cassandra-2.0.10-src/conf/cassandra.yaml";
		String resultFile="out.txt";
		KeywordSearch ks = new KeywordSearch(filepath,filename,configfile,resultFile);
		ks.search();
		ks.printresult();
		ks.printConfigInfo();
		String javaname,confname;
		javaname="org/apache/cassandra/io/sstable/SSTable.java";
		confname="isClientMode";
		System.out.println("Configuration name:"+confname+"\n"+"JavaFileName:"+javaname);
		List<Integer> r = ks.getConfigLineNumInJavafile(javaname, confname);
		if(r!=null)
		for(int k=0;k<r.size();k++)
			System.out.println(r.get(k));
		System.out.println(ks.getConfigType(confname));
		System.out.println(ks.isConfigCommented(confname));
		System.out.println(ks.isConfigOccurInDefaultConfigfile(confname));
		List<String> ls = ks.getJavafilesWithConfig(confname);
		for(int k=0;k<ls.size();k++)
			System.out.println(ls.get(k));
		
		ks.outputConfigfullInfo();
		ks.outputConfigfullRelation();
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
public List<Integer> getConfigLineNumInJavafile(String javafilename,String configname){
	List<Integer> result = new ArrayList<Integer>();
	int n=0;
	List<String> tmp= new ArrayList<String>();
	tmp=javafiles.get(javafilename).get(configname);
	if(tmp!=null){
		n=tmp.size();
	for(int i=0;i<n;i++)
		result.add(Integer.parseInt(javafiles.get(javafilename).get(configname).get(i)));
	return result;
	}
	else return null;
}
public List<String> getJavafilesWithConfig(String configname){
	List<String> filenames=new ArrayList<String>();
	for(String filename: javafiles.keySet()){
		if(javafiles.get(filename).get(configname)!=null)
			filenames.add(filename);
	}
	return filenames;
}
public String getConfigType(String configname){
	String ch,type;
	ch=configsinfo.get(configname);
	if(ch.contains("#")||ch.contains("!"))
		type=ch.substring(ch.indexOf("$")+2,ch.lastIndexOf(" $"));
	else
		type=ch.substring(ch.indexOf("$")+2,ch.length());
	return type;
}
public boolean isConfigCommented(String configname){
    String ch;
    ch=configsinfo.get(configname);
    if(ch.contains("#"))
    	return true;
    else return false;
}
public boolean isConfigOccurInDefaultConfigfile(String configname){
	String ch;
	ch=configsinfo.get(configname);
	if(ch.contains("!"))
		return false;
	else return true;
}

public void printConfigInfo(){
	System.out.println("---------All Configuration Parameters---------");
	for(String key: configsinfo.keySet()){
		System.out.println(key+" "+configsinfo.get(key));
	}
	System.out.println("----- Total: "+ configsinfo.size() + "-----");
    System.out.println("--------------------------------------------------------");
    System.out.println("----------------------------------");
}
public void outputConfigfullRelation(){
	// config full information <config name, config type "#:commented !:not occur in cassandra.yaml
		// @: occur in *.java file &: related with other config" >
	//*.java file <filename,<config name, line number>> 
		//HashMap<String,HashMap<String,List<String>>> javafiles
	// config full relation <configname#configtype,List<configname#configtype>>
	int n=0;
	List<String> configtemp;
	List<String> configrelat;
	for(String keyfile: javafiles.keySet()){
		if(!keyfile.contains("org/apache/cassandra/config")){//ignore Config.java,etc. files
			n=javafiles.get(keyfile).size();
			if(n>1){
				configtemp=new ArrayList<String>();
				for(String keyconfig : javafiles.get(keyfile).keySet())
					configtemp.add(keyconfig+"#"+configsinfo.get(keyconfig).split("\\$")[1].trim());
				for(int i=0; i<configtemp.size();i++){
					for(int j=i+1;j<configtemp.size();j++){
						if(configsfullrelation.containsKey(configtemp.get(i).split("#")[0])){
							configsfullrelation.get(configtemp.get(i)).add(configtemp.get(j));
						}
						else{
							configrelat = new ArrayList<String>();
							configrelat.add(configtemp.get(j));
							configsfullrelation.put(configtemp.get(i), configrelat);
						}
						if(configsfullrelation.containsKey(configtemp.get(j).split("#")[0])){
							configsfullrelation.get(configtemp.get(j)).add(configtemp.get(i));
						}
						else{
							configrelat = new ArrayList<String>();
							configrelat.add(configtemp.get(i));
							configsfullrelation.put(configtemp.get(j), configrelat);
						}
					}
					}
			}
		}
	}
	sortedConfigsfullrelation = new TreeMap(configsfullrelation);
	String path="JSInfoVis/src/data/";
	String file="fullconfigrelation.txt";
	File output=null;
	BufferedWriter bw=null;
	output=new File(path+file);
	try{
	bw=new BufferedWriter(new FileWriter(output));
	//System.out.println("********************************************");
	for(String node : sortedConfigsfullrelation.keySet())
		for(int i=0;i<sortedConfigsfullrelation.get(node).size();i++){
			bw.write(node+"->"+sortedConfigsfullrelation.get(node).get(i));
			bw.newLine();
		}
	}catch(IOException e){
		e.printStackTrace();
	}finally{
		try{
		bw.flush();
		bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
public void outputConfigfullInfo(){
	// config full information <config name, config type "#:commented !:not occur in cassandra.yaml
		// @: occur in *.java file &: related with other config" >
	for(String key: configsinfo.keySet())
		configsfullinfo.put(key, configsinfo.get(key));
	//*.java file <filename,<config name, line number>> 
	//HashMap<String,HashMap<String,List<String>>> javafiles
	int n=0;
	String cvalue=null;
	for(String keyfile: javafiles.keySet()){
		if(!keyfile.contains("org/apache/cassandra/config")){//ignore the config files
			n = javafiles.get(keyfile).size();
			if(n>1){
				for(String keyconfig: javafiles.get(keyfile).keySet()){
					cvalue=configsfullinfo.get(keyconfig);
				    if(cvalue.contains("$ @")&& !cvalue.contains("$ &")){
				    	configsfullinfo.remove(keyconfig);
					configsfullinfo.put(keyconfig, cvalue+"$ & ");
					}
				    else if(!cvalue.contains("$ @")){
				    	configsfullinfo.remove(keyconfig);
				    	configsfullinfo.put(keyconfig, cvalue+"$ @ $ & ");
				    }
				    }
			}
			else{
				for(String keyconfig: javafiles.get(keyfile).keySet()){
					cvalue=configsfullinfo.get(keyconfig);
					if(!cvalue.contains("$ @")){
						configsfullinfo.remove(keyconfig);
						configsfullinfo.put(keyconfig, cvalue+"$ @ ");
					}
				}
			}
			}
		}
	System.out.println("*************************************");
	sortedConfigsfullinfo = new TreeMap(configsfullinfo);
	for(String key: sortedConfigsfullinfo.keySet()){
		System.out.println(key+" "+sortedConfigsfullinfo.get(key));
	}
	// config full information <config name, config type "#:commented !:not occur in cassandra.yaml
			// @: occur in *.java file &: related with other config" >
	String fullconfig,relatconfig,isolatconfig,nooccurconfig;
	String path="JSInfoVis/src/data/";
	BufferedWriter bwf=null,bwr=null,bwi=null,bwn=null;
	fullconfig="fullconfig.txt";
	relatconfig="relatconfig.txt";
	isolatconfig="isolatconfig.txt";
	nooccurconfig="nooccurconfig.txt";
	File outf,outr,outi,outn;
	outf=new File(path+fullconfig);
	outr=new File(path+relatconfig);
	outi=new File(path+isolatconfig);
	outn=new File(path+nooccurconfig);
	try{
		outf.createNewFile();
		outr.createNewFile();
		outi.createNewFile();
		outn.createNewFile();
	if(outf.exists())
	bwf=new BufferedWriter(new FileWriter(outf));
	if(outr.exists())
	bwr=new BufferedWriter(new FileWriter(outr));
	if(outi.exists())
	bwi=new BufferedWriter(new FileWriter(outi));
	if(outn.exists())
	bwn=new BufferedWriter(new FileWriter(outn));
	for(String key: sortedConfigsfullinfo.keySet()){
		bwf.write(key+" "+sortedConfigsfullinfo.get(key));
		bwf.newLine();
		if(sortedConfigsfullinfo.get(key).contains("&")){
			bwr.write(key+" "+sortedConfigsfullinfo.get(key));
			bwr.newLine();	
		}
		if(sortedConfigsfullinfo.get(key).contains("@")&&!sortedConfigsfullinfo.get(key).contains("&")){
			bwi.write(key+" "+sortedConfigsfullinfo.get(key));
			bwi.newLine();
		}
		if(!sortedConfigsfullinfo.get(key).contains("@")){
			bwn.write(key+" "+sortedConfigsfullinfo.get(key));
			bwn.newLine();
		}
	}
	}catch(IOException e){
		e.printStackTrace();
	}finally{
		try{
		bwf.flush();
		bwf.close();
		bwr.flush();
		bwr.close();
		bwi.flush();
		bwi.close();
		bwn.flush();
		bwn.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
public void printresult(){
    
	for(String key : javafiles.keySet()){
		int k=1;
		System.out.println(key);
		for(String key1 : javafiles.get(key).keySet()){
			System.out.println(k+++". ");
			for(int i=0;i<javafiles.get(key).get(key1).size(); i++)
		System.out.println(key1+" "+javafiles.get(key).get(key1).get(i));
			}
	}
	System.out.println("-------------------------------------");
	System.out.println("Time Cost : "+getSearchTime()+"ms");
	System.out.println("-------------------------------------");
}
public void search(){
	long start=System.currentTimeMillis();
	String ch=null;
	String chtype=null;
	 
	List<String> type = new ArrayList<String>();
	String otype=null;
	BufferedReader bin=null;
	File f = null;
	String conf=null;
	List<String> configs = new ArrayList<String>();
	int count=0;
	type.add(" Integer ");
	type.add(" Boolean ");
	type.add(" String ");
	type.add(" Double ");
	type.add(" Long ");
	type.add(" int ");
	type.add(" boolean ");
	type.add(" double ");
	type.add(" long ");
	f =new File(filepath+filename);
	// picking up configuration names
	boolean flag = false;
	if(f.exists()){
	try{
	bin = new BufferedReader(new FileReader(f));
	while((ch=bin.readLine())!=null){
		flag=false;
		for(int i=0; i<type.size();i++)
		if(ch.contains(type.get(i))){
			chtype=type.get(i);
			if(ch.contains(" = "))
				{
				conf = ch.substring(ch.indexOf(chtype)+chtype.length(), ch.indexOf(" = "));
				configsinfo.put(conf, "$" + type.get(i));
				configs.add(conf);
				count++;
				}
			else if(ch.contains(";"))
				{
				conf = ch.substring(ch.indexOf(chtype)+chtype.length(), ch.indexOf(";"));
				configsinfo.put(conf, "$" + type.get(i));
				configs.add(conf);
				count++;
				}		
			flag=true;
		}
		if(!flag&&ch.contains("public")&&ch.contains(";")){
			String str=null;
			String[] buf=null;
			if(ch.contains(" = ")){
				str=ch.substring(ch.indexOf("public ")+7, ch.indexOf(" = "));
				buf=str.split(" ");
			    otype=buf[0];
			    conf=buf[1];
			    configsinfo.put(conf, "$ "+buf[0]+" ");
			    configs.add(conf);
				count++;
			}
			else if(ch.contains(";")){
				str=ch.substring(ch.indexOf("public ")+7, ch.indexOf(";"));
				buf=str.split(" ");
			    otype=buf[0];
			    conf=buf[1];
			    configsinfo.put(conf, "$ "+buf[0]+" ");
				configs.add(conf);
				count++;
			}
		}
	}
    bin.close();
    
	}catch(FileNotFoundException e){
		e.printStackTrace();
	}catch(IOException e){
		e.printStackTrace();
	}
	}else{
		System.out.println("File not found.");
	}
	// check the configuration name in configure file "cassandra.yaml"
	File confyaml = null;
	BufferedReader bfyaml = null;
	confyaml = new File(configfile);
	String yamline = null;
	String temp=null;
	int[] visited = new int[count];
	for(int k=0;k<count;k++)
		visited[k]=0;
    if(confyaml.exists()){
    try{
	bfyaml = new BufferedReader(new FileReader(confyaml));
	while((yamline=bfyaml.readLine())!=null){
		for(int i=0;i<configs.size();i++){
			if(yamline.contains(configs.get(i))){
				if(yamline.contains("#"))
					if(yamline.indexOf("#")<yamline.indexOf(configs.get(i)))
						{
						
						temp=configsinfo.get(configs.get(i));
						if(!temp.contains("$ #"))
							{
							String c = configsinfo.get(configs.get(i));
							configsinfo.remove(configs.get(i));
							configsinfo.put(configs.get(i), c+"$ # ");
							}
						}
				visited[i]=1;
			}
		}
	}
	bfyaml.close();
    }catch(FileNotFoundException e){
    	e.printStackTrace();
    }catch(IOException e1){
    	e1.printStackTrace();
    }
    }
    String tp =null;
    for(int j=0;j<count;j++)
    	if(visited[j]==0) {
    		tp = configsinfo.get(j);
    		String c = configsinfo.get(configs.get(j));
    		configsinfo.remove(configs.get(j));
    		configsinfo.put(configs.get(j), c+"$ ! ");
    	}
	// search the configuration name in the source codes
	//String command = "find "+filepath+" -name *.java | xargs grep -n memtable_flush_queue_size";
	//String command = "grep 'a' v";
	String command = null;
	Process p = null;
	InputStreamReader instream = null;
	BufferedReader result = null;
	String chr=null;
	String r=null;
	String jfilename=null;
	File fout = null;
	BufferedWriter bw = null;
	String[] strs=null;
	String confignamekey=null;
	String context=null;
	String[] keycontext=null;
	boolean isRealConfig=false;
	fout = new File(resultFile);
	try{
	fout.createNewFile();
	if(fout.exists()) 
		bw=new BufferedWriter(new FileWriter(fout));
	}catch(IOException e){
		e.printStackTrace();
	}
	for(int i=0;i<configs.size();i++)
	try{
	confignamekey=configs.get(i);
	command = "sh c.sh " + confignamekey; 
	/* content of "c.sh"
	 * #!/bin/bash
       find cassandra/apache-cassandra-2.0.10-src/src/java/org/ -name *.java | xargs grep -n $1
	 */
	p = Runtime.getRuntime().exec(command);
	instream = new InputStreamReader(p.getInputStream());
	result = new BufferedReader(instream);
	while((chr=result.readLine())!=null){
		isRealConfig=false;
		//System.out.println(chr);
		strs=chr.split(":");
		r=strs[0].substring(strs[0].indexOf("org"))+":"+strs[1]+":"+" "+confignamekey+" "+configsinfo.get(confignamekey);
		context=chr.substring(chr.indexOf(strs[1])+strs[1].length()+1);
		//System.out.println("---"+context);
		keycontext=context.split(confignamekey);
		//System.out.println(keycontext.length);
		for(int t=0;t<keycontext.length-1;t++){
			char before,after;
			before=keycontext[t].charAt(keycontext[t].length()-1);
			after=keycontext[t+1].charAt(0);
			if(before>='a'&&before<='z'||before>='A'&&before<='Z'
					||after>='a'&&after<='z'||after>='A'&&after<='Z'
					||before=='_'||after=='_')
			{   isRealConfig=false;
				//System.out.println("B-"+before+"-A-"+after);
			}
			else if(before=='.'){isRealConfig=true;break;}
		}
		if(isRealConfig){
		jfilename=r.substring(0, r.indexOf(":"));
		if(!javafiles.containsKey(jfilename))
			{HashMap<String,List<String>> configname = new HashMap<String,List<String>>();
			List<String> ls = new ArrayList<String>();
			ls.add(r.substring(r.indexOf(":")+1, r.lastIndexOf(":")));
			configname.put(confignamekey, ls);
			javafiles.put(jfilename, configname);
			}
		else{
			//System.out.println(r);
			if(javafiles.get(jfilename).get(confignamekey)==null)
			{	List<String> ls = new ArrayList<String>();
			    ls.add(r.substring(r.indexOf(":")+1, r.lastIndexOf(":")));
			    javafiles.get(jfilename).put(confignamekey, ls);
			}else 
			javafiles.get(jfilename).
					get(confignamekey).add(r.substring(r.indexOf(":")+1, r.lastIndexOf(":")));
			//javafiles.get(jfilename).put(configs.get(i), r.substring(r.indexOf(":")+1, r.lastIndexOf(":")));
		}
		//System.out.println(r);
		bw.write(r);
		bw.newLine();
	}}//end of if
	}catch(IOException e){
		e.printStackTrace();
	}
	try{
	bw.flush();
	bw.close();
	}catch(IOException e){
		e.printStackTrace();
	}
	
	long end=System.currentTimeMillis();
	setSearchTime(end-start);
	//System.out.println("////////////////////////////// Time Cost : "+(end-start)+"ms");
}
public long getSearchTime(){
	return searchTime;
}
public void setSearchTime(long searchTime){
	this.searchTime=searchTime;
}
public void setFilepath(String filepath){
	this.filepath=filepath;
}
public void setFilename(String filename){
	this.filename=filename;
}
public void setConfigfile(String configfile){
	this.configfile=configfile;
}
public void setResultFile(String resultFile){
	this.resultFile=resultFile;
}
}
