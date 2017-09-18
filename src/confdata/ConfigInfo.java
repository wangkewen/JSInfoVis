package confdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import confdata.Configsgraph.adjacency;
import confdata.Configsgraph.data;

public class ConfigInfo {
	public static String[] colors={"#006400","#800000","#FF0000","#0000FF",
		"#800080","#FF1493","#00BFFF","#D2691E;",
		"#9ACD32","#FF8C00","#FFD700","#00FF7F"};
	private Map<String,Config> configs; // <configname,Config>
	private Map<String,Javafile> javafiles; // <javafilename,Javafile>
	private Map<String,String> configmethods; // <configmethod,configname>
	private String configjava;
	private String yamlfile;
	private String descriptorfile;
	private String javafilesname;
	private List<Configsgraph> graphs;
	private List<Configsgraph> virgraphs;
	private Map<String,Configsgraph> wholeGraph; // <node name,Configsgraph>
	public ConfigInfo(){
		configs=new TreeMap<String,Config>();
		javafiles=new TreeMap<String,Javafile>();
		configmethods=new TreeMap<String,String>();
		graphs=new ArrayList<Configsgraph>();
		virgraphs=new ArrayList<Configsgraph>();
		wholeGraph=new HashMap<String,Configsgraph>();
	}
	public void init(String configjava,String yamlfile,String descriptorfile,String javafilesname){
		setConfigjava(configjava);
		setYamlfile(yamlfile);
		setDescriptorfile(descriptorfile);
		setJavafilesname(javafilesname);
		configsGenerate(configjava, yamlfile, descriptorfile);
		javafilesGenerate(javafilesname);
	}
	public String getConfigjava(){
		return configjava;
	}
	public void setConfigjava(String configjava){
		this.configjava=configjava;
	}
	public String getYamlfile(){
		return yamlfile;
	}
	public void setYamlfile(String yamlfile){
		this.yamlfile=yamlfile;
	}
	public String getDescriptorfile(){
		return descriptorfile;
	}
	public void setDescriptorfile(String descriptorfile){
		this.descriptorfile=descriptorfile;
	}
	public String getJavafilesname(){
		return javafilesname;
	}
	public void setJavafilesname(String javafilesname){
		this.javafilesname=javafilesname;
	}
	public Map<String,Config> getConfigs(){
		return configs;
	}
	public void setConfigs(Map<String,Config> configs){
		this.configs=configs;
	}
	public Map<String,Javafile> getJavafiles(){
		return javafiles;
	}
	public void setJavafiles(Map<String,Javafile> javafiles){
		this.javafiles=javafiles;
	}
	public Map<String,String> getConfigmethods(){
		return configmethods;
	}
	public void setConfigmethods(Map<String,String> configmethods){
		this.configmethods=configmethods;
	}
	public List<Configsgraph> getVirgraphs(){
		return virgraphs;
	}
	public void setVirgraphs(List<Configsgraph> virgraphs){
		this.virgraphs=virgraphs;
	}
	public List<Configsgraph> getGraphs(){
		return graphs;
	}
	public void setGraphs(List<Configsgraph> graphs){
		this.graphs=graphs;
	}
	public Map<String,Configsgraph> getWholeGraph(){
		return wholeGraph;
	}
	public void setWholeGraph(Map<String,Configsgraph> wholeGraph){
		this.wholeGraph=wholeGraph;
	}
	public static void main(String[] args){
		String configjava="cassandra/apache-cassandra-2.0.7-src/src/java"
				+ "/org/apache/cassandra/config/Config.java";
		String yamlfile="cassandra/apache-cassandra-2.0.7-src/conf"
				+ "/cassandra.yaml";
		String descriptorfile = "configs_cassandra/Descriptor.out";
		String javafilesname="configs_cassandra/javafiles.out";
		ConfigInfo configInfo;
		configInfo=new ConfigInfo();
		configInfo.init(configjava, yamlfile, descriptorfile, javafilesname);
		configInfo.initAbstractGraph();
		configInfo.getVirtualGraphs("null");
		configInfo.constructSourceFiles("db");
		//configInfo.outputJavafiles();
		//configInfo.printConfigs();
		//configInfo.printJavafiles();
	}
	private void visit(Map<String,Integer> visited,String node,Integer group){
		if(visited.containsKey(node)) return;
		else {
			visited.put(node, group);
			for(int i=0;i<wholeGraph.get(node).getAdjacencies().size();i++){
				visit(visited,wholeGraph.get(node).getAdjacencies().get(i).getNodeTo(),group);
			}
		}
		
	}
    public Map<String,Integer> initAbstractGraph(){
		getConfigsgraph("null");
		Map<String,Integer> visited;
		Integer group=0;
		visited=new HashMap<String,Integer>();
		//private Map<String,Configsgraph> wholeGraph; // <node name,Configsgraph>
		for(int i=0;i<graphs.size();i++)
		if(!visited.containsKey(graphs.get(i).getName())){
		group++;
		visit(visited,graphs.get(i).getName(),group);
		}
		return visited;
	}
    
    public void getVirtualGraphs(String scale){
		Map<String,Integer> configroup;
		List<ArrayList<String>> configgroups;
		String[] groupids=null;
		Integer max=0,mnum,maxGroup=1;
		configgroups = new ArrayList<ArrayList<String>>();
		configroup=initAbstractGraph();
		if(scale!=null&&scale.contains("_")) groupids=scale.split("_");
		for(String key: configroup.keySet())
			if(configroup.get(key)>max) max=configroup.get(key);
		for(int i=0;i<max;i++)
			configgroups.add(new ArrayList<String>());
		for(String key: configroup.keySet())
			configgroups.get(configroup.get(key)-1).add(key);
		mnum=0;
		for(int j=0;j<configgroups.size();j++)
			if(configgroups.get(j).size()>mnum){
				mnum=configgroups.get(j).size();
				maxGroup=j+1;
			}
		Configsgraph graph=null;
		data d=null;
		List<adjacency> adjs=null;
		adjacency adj =null;
		String name=null,maxname=null;;
		for(int k=1;k<=max;k++){
			graph=new Configsgraph();
			if(k<10) {name="0"+k+"_"+configgroups.get(k-1).size()+" Configs";
			maxname="0"+k+"_"+configgroups.get(maxGroup-1).size()+" Configs";
			}else{
				name=k+"_"+configgroups.get(k-1).size()+" Configs";
				maxname=k+"_"+configgroups.get(maxGroup-1).size()+" Configs";
			}
			graph.setId(name);
			graph.setName(name);
			d = graph.new data();
			d.set$color(colors[k-1]);
			d.set$type("circle");
			d.set$dim(8+configgroups.get(k-1).size());
			graph.setData(d);
			adjs=new ArrayList<adjacency>();
			graph.setAdjacencies(adjs);
			virgraphs.add(graph);
		}
		for(int k=1;k<=max;k++){
			if(k==maxGroup){
				for(int l=1;l<=max;l++){
					adj = graph.new adjacency();
					d = graph.new data();
					d.set$color("#ddd");
					adj.setNodeFrom(virgraphs.get(k-1).getName());
					adj.setNodeTo(virgraphs.get(l-1).getName());
					adj.setData(d);
					virgraphs.get(k-1).getAdjacencies().add(adj);
					}
			}else{
				adj = graph.new adjacency();
				d = graph.new data();
				d.set$color("#ddd");
				adj.setNodeFrom(virgraphs.get(k-1).getName());
				adj.setNodeTo(virgraphs.get(maxGroup-1).getName());
				adj.setData(d);
				virgraphs.get(k-1).getAdjacencies().add(adj);
			}
		}
		if(groupids!=null && groupids.length>1){
			for(int i=1;i<groupids.length;i++){
				graphs=new ArrayList<Configsgraph>();
				wholeGraph=new HashMap<String,Configsgraph>();
				getConfigsgraph(groupids[i]);
				for(int k=0;k<graphs.size();k++){
					graphs.get(k).getData().set$color(colors[Integer.parseInt(groupids[i])-1]);
					for(int g=0;g<graphs.get(k).getAdjacencies().size();g++)
						graphs.get(k).getAdjacencies().get(g).getData()
						.set$color(colors[Integer.parseInt(groupids[i])-1]);
					if(k==0){
					adjacency ad=graphs.get(k).new adjacency();
					data dd=graphs.get(k).new data();
					dd.set$color(colors[Integer.parseInt(groupids[i])-1]);
					ad.setData(dd);
					ad.setNodeTo(groupids[i]+
							"_"+configgroups.get(Integer.parseInt(groupids[i])-1).size()+" Configs");
					ad.setNodeFrom(graphs.get(k).getName());
					for(int j=0;j<virgraphs.size();j++)
						if(virgraphs.get(j).getName().equals(Integer.parseInt(groupids[i])+
							"_"+configgroups.get(Integer.parseInt(groupids[i])-1).size()+" Configs")){
					}
					graphs.get(k).getAdjacencies().add(ad);
					}
					virgraphs.add(graphs.get(k));
					}
			}
		}
	}
    public List<Configsgraph> getIsolatedgraph(String scale){
    	String path;
		String file;
		File input=null;
		BufferedReader br=null;
		Configsgraph graph=null,graphb=null;
		List<Configsgraph> igraphs=null;
		data td = null,d=null;
		List<adjacency> tadjs =null;
		adjacency tadj= null;
		Boolean f=false;
		String line,preA=null,nodeA=null,aType=null,nodeB=null,bType=null;
		String[] tmp;
		Map<String,Integer> visited=null;
		igraphs=new ArrayList<Configsgraph>();
		Integer groupId=0;
		int degree=0,num=0;
		path=JSInfoVis/src/confdata/";
		file="isolatconf.txt";
		input=new File(path+file);
		
		if(input.exists()){
			try{
			br=new BufferedReader(new FileReader(input));
			while((line=br.readLine())!=null){
				degree=0;
				tmp=line.split(" ");
				nodeA=tmp[0];
				aType=tmp[1];
				nodeB=tmp[0];
				bType=tmp[1];
				f=nodeA.equals(scale);
				//A->B
				if(f){
				
					graph=new Configsgraph();
					graph.setId(nodeA);
					graph.setName(nodeA);
					td = graph.new data();
					if(aType.contains("Integer")||aType.contains("int"))
					{
						 td.set$type("square");
						 td.set$color("#EBB056");
					}else if(aType.contains("Double")||aType.contains("double"))
					{
						td.set$type("square");
						td.set$color("#416D9C");
					}else if(aType.contains("Long")||aType.contains("long")){
						td.set$type("square");
						td.set$color("#C74243");
					}else if(aType.contains("String")||aType.contains("string")){
						td.set$type("circle");
						td.set$color("#70A35E");
					}else if(aType.contains("Boolean")||aType.contains("boolean")){
						td.set$type("triangle");
						td.set$color("#83548B");
					}else {
						td.set$type("star");
						td.set$color("#ff4500");
					}
				td.set$dim(8);	
				graph.setData(td);
//				tadjs = new ArrayList<adjacency>();	
//				tadj = graph.new adjacency();
//				d = graph.new data();
//				d.set$color("#557EAA");
//				tadj.setData(d);
//				tadj.setNodeFrom(nodeA);
//				tadj.setNodeTo(nodeB);
//				tadjs.add(tadj);
//				graph.setAdjacencies(tadjs);
				
				igraphs.add(graph);
				
				
					
				}
			}//while
			
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return igraphs;
    }
	public void getConfigsgraph(String scale){
		String path;
		String file;
		File input=null;
		BufferedReader br=null;
		Configsgraph graph=null,graphb=null;
		data td = null,d=null;
		List<adjacency> tadjs =null;
		adjacency tadj= null;
		Boolean f=false;
		String line,preA=null,nodeA=null,aType=null,nodeB=null,bType=null;
		String[] tmp;
		Map<String,Integer> visited=null;
		Integer groupId=0;
		int degree=0,num=0;
		path="JSInfoVis/src/confdata/";
		file="fullconfgraphs.txt";
		input=new File(path+file);
		if(scale.matches("^[0-9]*$")){
			visited=initAbstractGraph();
			groupId=Integer.parseInt(scale);
			//System.out.println(groupId);
			graphs=new ArrayList<Configsgraph>();
			wholeGraph=new HashMap<String,Configsgraph>();
		}
		if(input.exists()){
			try{
			br=new BufferedReader(new FileReader(input));
			while((line=br.readLine())!=null){
				degree=0;
				tmp=line.split("->");
				nodeA=tmp[0].split("#")[0];
				aType=tmp[0].split("#")[1];
				nodeB=tmp[1].split("#")[0];
				bType=tmp[1].split("#")[1];
				if(scale.equals("null")||scale==null||scale.equals("full")) f=true;
				else if(scale.matches("^[0-9]*$")){
					if(visited.containsKey(nodeA)||visited.containsKey(nodeB))
					f=visited.get(nodeA)==groupId||visited.get(nodeB)==groupId;
				}else f=nodeA.equals(scale)||nodeB.equals(scale);
				//A->B
				if(f){
				if(!wholeGraph.containsKey(nodeA)){
					graph=new Configsgraph();
					graph.setId(nodeA);
					graph.setName(nodeA);
					td = graph.new data();
					if(aType.contains("Integer")||aType.contains("int"))
					{
						 td.set$type("square");
						 td.set$color("#EBB056");
					}else if(aType.contains("Double")||aType.contains("double"))
					{
						td.set$type("square");
						td.set$color("#416D9C");
					}else if(aType.contains("Long")||aType.contains("long")){
						td.set$type("square");
						td.set$color("#C74243");
					}else if(aType.contains("String")||aType.contains("string")){
						td.set$type("circle");
						td.set$color("#70A35E");
					}else if(aType.contains("Boolean")||aType.contains("boolean")){
						td.set$type("triangle");
						td.set$color("#83548B");
					}else {
						td.set$type("star");
						td.set$color("#ff4500");
					}
				td.set$dim(8);	
				graph.setData(td);
				tadjs = new ArrayList<adjacency>();	
				tadj = graph.new adjacency();
				d = graph.new data();
				d.set$color("#557EAA");
				tadj.setData(d);
				tadj.setNodeFrom(nodeA);
				tadj.setNodeTo(nodeB);
				tadjs.add(tadj);
				graph.setAdjacencies(tadjs);
				wholeGraph.put(nodeA, graph);
				}else{
					graph=wholeGraph.get(nodeA);
				tadjs = graph.getAdjacencies();	
				tadj = graph.new adjacency();
				d = graph.new data();
				d.set$color("#557EAA");
				tadj.setData(d);
				tadj.setNodeFrom(nodeA);
				tadj.setNodeTo(nodeB);
				tadjs.add(tadj);
				}
				
				//B->A
				if(!wholeGraph.containsKey(nodeB)){
				graph=new Configsgraph();
				graph.setId(nodeB);
				graph.setName(nodeB);
				td = graph.new data();
				if(bType.contains("Integer")||bType.contains("int"))
				{
					 td.set$type("square");
					 td.set$color("#EBB056");
				}else if(bType.contains("Double")||bType.contains("double"))
				{
					td.set$type("square");
					td.set$color("#416D9C");
				}else if(bType.contains("Long")||bType.contains("long")){
					td.set$type("square");
					td.set$color("#C74243");
				}else if(bType.contains("String")||bType.contains("string")){
					td.set$type("circle");
					td.set$color("#70A35E");
				}else if(bType.contains("Boolean")||bType.contains("boolean")){
					td.set$type("triangle");
					td.set$color("#83548B");
				}else {
					td.set$type("star");
					td.set$color("#ff4500");
				}
				td.set$dim(8);	
				graph.setData(td);
				tadjs = new ArrayList<adjacency>();
				tadj = graph.new adjacency();
				d = graph.new data();
				d.set$color("#557EAA");
				tadj.setData(d);
				tadj.setNodeFrom(nodeB);
				tadj.setNodeTo(nodeA);
				tadjs.add(tadj);
				
				graph.setAdjacencies(tadjs);
				wholeGraph.put(nodeB, graph);
				}else{
					graph=wholeGraph.get(nodeB);	
			    tadjs = graph.getAdjacencies();
			    tadj = graph.new adjacency();
			    d = graph.new data();
			    d.set$color("#557EAA");
			    tadj.setData(d);
			    tadj.setNodeFrom(nodeB);
			    tadj.setNodeTo(nodeA);
			    tadjs.add(tadj);
			    }
				}
			}//while
			for(String key: wholeGraph.keySet())
				graphs.add(wholeGraph.get(key));
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	public List<CallGraph> getCallGraphs(String config){
		List<CallGraph> callgraphs=null;
		Map<String,CallGraph> graphlists=null;
		callgraphs=new ArrayList<CallGraph>();
		String path="JSInfoVis/src/confdata/";
		String name="callgraphs.txt";
		File fr=null;
		BufferedReader br=null;
		Boolean inProcess=false;
		String line=null;
		String[] nodes=null;
		String[] nodeinfo=null;
		String nodename=null;
		String nodeid=null;
		Map<String,String> nodeslist=null;
		CallGraph graph=null;
		data d=null,ad=null;
		List<adjacency> tadjs =null;
		adjacency tadj= null;
		String to=null;
		String method=null;
		graphlists=new HashMap<String,CallGraph>();
		nodeslist=new HashMap<String,String>();
		fr=new File(path+name);
		if(fr.exists()){
		try{
		br=new BufferedReader(new FileReader(fr));
		while((line=br.readLine())!=null){
			if(line.contains("->"+config)) inProcess=true;
			if(inProcess){
			if(line.contains("->")) nodes=line.split("->");
			for(int n=nodes.length-1;n>=0;n--)
			if(line.contains("->"+config)||nodeslist.size()>0&&!nodeslist.containsKey(nodes[n]))
			{
				//System.out.println(nodes[n]);
				graph = new CallGraph();
				d=graph.new data();
				tadjs=new ArrayList<adjacency>();
				tadj=graph.new adjacency();
				ad=graph.new data();
				ad.set$color("#006400");
				ad.set$dim(12);
				tadj.setData(ad);
				d.set$dim(8);
				if(nodes[n].contains("#")){
					nodeinfo=nodes[n].split("#");
					if(nodeinfo.length>3){
						if(!nodeinfo[3].contains("("))
							method=nodeinfo[3]+"()";
						else method=nodeinfo[3];
						//System.out.println(nodeinfo[0]);
						nodename=method+"_"+nodeinfo[0].split("/")[nodeinfo[0].split("/").length-1]
								+"_line"+nodeinfo[1];
						nodeid=nodeinfo[0].substring(nodeinfo[0].indexOf("org/apache/cassandra")).replace("/", "_")
								+"__LINE"+nodeinfo[1];
						if(nodeinfo[2].equals("entry_point")&&nodeinfo[3].equals("main")){
							d.set$type("star");
						    d.set$dim(12);
							d.set$color("#800080");
							}else{
								d.set$type("square");
								d.set$color("#416D9C");
							}
					}else{
						nodename=nodeinfo[0].split("/")[nodeinfo[0].split("/").length-1]+"_line"+nodeinfo[1];
						nodeid=nodeinfo[0].substring(nodeinfo[0].indexOf("org/apache/cassandra")).replace("/", "_")
								+"__LINE"+nodeinfo[1];
						d.set$type("square");
						d.set$color("#416D9C");
					}
				}else{
					if(n==0){
						if(!nodes[n].contains("("))
						nodename=nodes[n]+"()";
						else nodename=nodes[n];
						d.set$type("circle");
						d.set$color("#FF4500");
						d.set$dim(10);
					}else if(n==1){
						nodename=nodes[n];
						d.set$type("circle");
						d.set$color("#C74243");
						d.set$dim(10);
					}
					nodeid=nodename;
				}
				if(n==1) to=nodeid;
				else if(n==0){
					if(nodeslist.containsKey(nodes[1]))
						tadj.setNodeTo(nodeslist.get(nodes[1]));
					else tadj.setNodeTo(to);
					tadj.setNodeFrom(nodeid);
					tadjs.add(tadj);
					}
				
				graph.setId(nodeid);
				graph.setName(nodename);
				graph.setData(d);
				graph.setAdjacencies(tadjs);
				callgraphs.add(graph);
				graphlists.put(nodeid, graph);
				nodeslist.put(nodes[n],nodeid);
			}/*
			else if(nodeslist.containsKey(nodes[n])){
				tadj=graph.new adjacency();
				ad=graph.new data();
				ad.set$color("#006400");
				ad.set$dim(12);
				tadj.setData(ad);
				if(n==0) to=nodeslist.get(nodes[0]);
				else if(n==1){
					if(nodeslist.containsKey(nodes[0]))
						tadj.setNodeTo(nodeslist.get(nodes[0]));
					else tadj.setNodeTo(to);
					tadj.setNodeFrom(nodeslist.get(nodes[1]));
					graphlists.get(nodeslist.get(nodes[1])).getAdjacencies().add(tadj);
					}
			}*/
			if(line.equals("----------------------------"))
				break;
			}
		}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		}
		//System.out.println(callgraphs.size());
		return callgraphs;
	}
	public void outputConfigfullRelation(){
		String path="JSInfoVis/src/confdata/";
		String file="fullconfgraphs.txt";
		List<String> edges;
		File output=null;
		BufferedWriter bw=null;
		edges=new ArrayList<String>();
		String nodeA,nodeB;
		output=new File(path+file);
		try{
		bw=new BufferedWriter(new FileWriter(output));
		for(String node : javafiles.keySet())
			for(int i=0;i<javafiles.get(node).getConfigname().size();i++)
				for(int j=i+1;j<javafiles.get(node).getConfigname().size();j++){
				nodeA=javafiles.get(node).getConfigname().get(i)
						+"#"+configs.get(javafiles.get(node).getConfigname().get(i)).getType();
				nodeB=javafiles.get(node).getConfigname().get(j)
						+"#"+configs.get(javafiles.get(node).getConfigname().get(j)).getType();
				if(!edges.contains(nodeA+"->"+nodeB)&&!edges.contains(nodeB+"->"+nodeA)){
				edges.add(nodeA+"->"+nodeB);
				bw.write(nodeA+"->"+nodeB);
				bw.newLine();
				}
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
public Config constructConf(String confname){
	Config conf=null;
	String path="JSInfoVis/src/confdata/";
	String fullconfig="fullconfs.txt";
	BufferedReader br=null;
	File f=null;
	String ch=null;
	String[] confinfo=null;
	conf=new Config();
	f=new File(path+fullconfig);
	if(f.exists()){
		try{
		br=new BufferedReader(new FileReader(f));
		while((ch=br.readLine())!=null){
			confinfo=ch.split(" ");
			if(confname.equals(confinfo[0])){
			conf.setName(confinfo[0]);
			conf.setType(confinfo[1]);
			if(!confinfo[2].equals("null")||confinfo[2]!=null) conf.setMethod(confinfo[2]);
			conf.setIsinDatabaseDescriptor(Boolean.parseBoolean(confinfo[3]));
			conf.setIsInyaml(Boolean.parseBoolean(confinfo[4]));
			conf.setIsCommentedInyaml(Boolean.parseBoolean(confinfo[5]));
			if(confinfo[6].contains("@"))
				conf.setDefaultValue(confinfo[6].replaceAll("@", " "));
			else conf.setDefaultValue(confinfo[6]);
			if(ch.contains("javafiles:")||confinfo.length>7){
				String[] jfiles=confinfo[7].split(":");
				for(int p=1;p<jfiles.length;p++){
					String[] jline=jfiles[p].split("#");
					conf.getJavafilenames().add(jline[0]);
					//System.out.println(jline[0]);
					ArrayList<Integer> lnums = new ArrayList<Integer>();
					for(int g=1;g<jline.length;g++)
						lnums.add(Integer.parseInt(jline[g]));
					conf.getJavalinenums().put(jline[0], lnums);
					}
			}
			}
		}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
			br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}else{
		System.out.println("File not found...");
	}
	return conf;
}
public List<Config> constructConfigs(String type){
	String configjava="cassandra/apache-cassandra-2.0.7-src/src/java"
			+ "/org/apache/cassandra/config/Config.java";
	String yamlfile="cassandra/apache-cassandra-2.0.7-src/conf"
			+ "/cassandra.yaml";
	String descriptorfile = "configs_cassandra/Descriptor.out";
	String javafilesname="configs_cassandra/javafiles.out";
	String path="JSInfoVis/src/confdata/";
	String fullconfig,relatconfig,isolatconfig,systemconfig;
	File outf,outr,outi,outn,outs;
	fullconfig="fullconfs.txt";
 	relatconfig="relatconf.txt";
 	isolatconfig="isolatconf.txt";
 	systemconfig="systemconf.txt";
 	outf=new File(path+fullconfig);
 	outr=new File(path+relatconfig);
 	outi=new File(path+isolatconfig);
 	outs=new File(path+systemconfig);
 	if(!outf.exists()||!outr.exists()||!outi.exists()
 			||!outs.exists())
 		this.init(configjava, yamlfile, descriptorfile, javafilesname);
 	List<Config> configlist = new ArrayList<Config>();
	String configinfo=null;
	File configfile = null;
	BufferedReader br = null;
	String ch=null;
	String[] chinfo= null;
	String[] jfiles=null;
	Map<String,Integer> configGroup = null;
	Config cf = null;
	if(type.equals("related")||type.equals("relatedS")){
		configinfo=relatconfig;
		configGroup=this.initAbstractGraph();	
	}
	else if(type.equals("isolated"))
		configinfo=isolatconfig;
	else if(type.equals("system"))
		configinfo=systemconfig;
		configfile=new File(path+configinfo);
		if(configfile.exists()){
			try{
			br = new BufferedReader(new FileReader(configfile));
			while((ch=br.readLine())!=null){
				chinfo=ch.split(" ");
				cf = new Config();
				cf.setName(chinfo[0]);
				cf.setType(chinfo[1]);
				if(type.equals("related")||type.equals("relatedS"))
					cf.setGroup(configGroup.get(chinfo[0]));
				//if(chinfo[2].equals("null"))
					cf.setMethod(chinfo[2]);
				cf.setIsinDatabaseDescriptor(Boolean.parseBoolean(chinfo[3]));
				cf.setIsInyaml(Boolean.parseBoolean(chinfo[4]));
				cf.setIsCommentedInyaml(Boolean.parseBoolean(chinfo[5]));
				if(chinfo[6].contains("@"))
				cf.setDefaultValue(chinfo[6].replaceAll("@", " "));
				else cf.setDefaultValue(chinfo[6]);
				//System.out.println(cf.getDefaultValue());
				if(ch.contains("javafiles:")||chinfo.length>7){
					jfiles=chinfo[7].split(":");
					for(int p=1;p<jfiles.length;p++){
						String[] jline=jfiles[p].split("#");
						cf.getJavafilenames().add(jline[0]);
						ArrayList<Integer> lnums = new ArrayList<Integer>();
						for(int g=1;g<jline.length;g++)
							lnums.add(Integer.parseInt(jline[g]));
						cf.getJavalinenums().put(jline[0], lnums);
						}
				}
				configlist.add(cf);
			}
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		else return null;
	
	return configlist;
}
public void outputConfigs(){
	 // configinfo: name,type,method,javafiles(:),isInDatabaseDescriptor,isInyaml,isCommentedInyaml
 	String fullconfig,relatconfig,isolatconfig,systemconfig;
 	String path="JSInfoVis/src/confdata/";
 	BufferedWriter bwf=null,bwr=null,bwi=null,bws=null;
 	File outf,outr,outi,outn,outs;
 	List<String> javanames;
 	Boolean related=false;
 	fullconfig="fullconfs.txt";
 	relatconfig="relatconf.txt";
 	isolatconfig="isolatconf.txt";
 	systemconfig="systemconf.txt";
 	outf=new File(path+fullconfig);
 	outr=new File(path+relatconfig);
 	outi=new File(path+isolatconfig);
 	outs=new File(path+systemconfig);
 	try{
 		outf.createNewFile();
 		outr.createNewFile();
 		outi.createNewFile();
 		outs.createNewFile();
 	if(outf.exists())
 	bwf=new BufferedWriter(new FileWriter(outf));
 	if(outr.exists())
 	bwr=new BufferedWriter(new FileWriter(outr));
 	if(outi.exists())
 	bwi=new BufferedWriter(new FileWriter(outi));
 	if(outs.exists())
 	bws=new BufferedWriter(new FileWriter(outs));
 	for(String key: configs.keySet()){
 		String defaultvalue=configs.get(key).getDefaultValue();
 		//System.out.println(defaultvalue);
 		if(defaultvalue!=null&&defaultvalue.contains(" "))
 			defaultvalue=configs.get(key).getDefaultValue().replaceAll(" ","@");
 		bwf.write(key+" "+configs.get(key).getType()
				+ " " + configs.get(key).getMethod()
				+ " " + configs.get(key).getIsInDatabaseDescriptor()
				+ " "+configs.get(key).getIsInyaml()
				+ " "+configs.get(key).getIsCommentedInyaml()
				+ " "+defaultvalue
				);
		for(int i=0;i<configs.get(key).getJavafilenames().size();i++){
			String linenums="";
				for(int p=0;p<javafiles.get(configs.get(key).getJavafilenames().get(i))
						.getConfiglinenums().get(key).size();p++)
					linenums+="#"+javafiles.get(configs.get(key).getJavafilenames().get(i))
					.getConfiglinenums().get(key).get(p);
			if(i==0) bwf.write(" javafiles:"+configs.get(key).getJavafilenames().get(i)+linenums);
			else bwf.write(":"+configs.get(key).getJavafilenames().get(i)+linenums);
			}
 		bwf.newLine();
 		if(!configs.get(key).getIsInDatabaseDescriptor()){
 			bws.write(key+" "+configs.get(key).getType()
 					+ " " + configs.get(key).getMethod()
 					+ " " + configs.get(key).getIsInDatabaseDescriptor()
 					+ " "+configs.get(key).getIsInyaml()
 					+ " "+configs.get(key).getIsCommentedInyaml()
 					+ " "+defaultvalue
 					);
 			for(int i=0;i<configs.get(key).getJavafilenames().size();i++)
 				if(i==0) bws.write(" javafiles:"+configs.get(key).getJavafilenames().get(i));
 				else bws.write(":"+configs.get(key).getJavafilenames().get(i));
 	 		bws.newLine();
 		}else if(configs.get(key).getJavafilenames().size()>0){
 			javanames=new ArrayList<String>();
 			related=false;
 			javanames=configs.get(key).getJavafilenames();
 			for(int i=0;i<javanames.size();i++){
 				if(javafiles.get(javanames.get(i)).getConfigname().size()>1){
 					bwr.write(key+" "+configs.get(key).getType()
 							+ " " + configs.get(key).getMethod()
 							+ " " + configs.get(key).getIsInDatabaseDescriptor()
 							+ " "+configs.get(key).getIsInyaml()
 							+ " "+configs.get(key).getIsCommentedInyaml()
 							+ " "+defaultvalue
 							);
 					for(int j=0;j<javanames.size();j++){
 						String linenums="";
 						for(int p=0;p<javafiles.get(javanames.get(j)).getConfiglinenums().get(key).size();p++)
 							linenums+="#"+javafiles.get(javanames.get(j)).getConfiglinenums().get(key).get(p);
 						if(j==0) bwr.write(" javafiles:"+javanames.get(j)+linenums);
 						else bwr.write(":"+javanames.get(j)+linenums);
 						}
 			 		bwr.newLine();
 					related=true;
 					break;
 				}
 			}
 			if(!related){
 				bwi.write(key+" "+configs.get(key).getType()
 						+ " " + configs.get(key).getMethod()
 						+ " " + configs.get(key).getIsInDatabaseDescriptor()
 						+ " "+configs.get(key).getIsInyaml()
 						+ " "+configs.get(key).getIsCommentedInyaml()
 						+ " "+defaultvalue
 						);
 				for(int k=0;k<javanames.size();k++){
 					String linenums="";
						for(int p=0;p<javafiles.get(javanames.get(k)).getConfiglinenums().get(key).size();p++)
							linenums+="#"+javafiles.get(javanames.get(k)).getConfiglinenums().get(key).get(p);
 					if(k==0) bwi.write(" javafiles:"+javanames.get(k)+linenums);
 					else bwi.write(":"+javanames.get(k)+linenums);
 					}
 		 		bwi.newLine();
 			}
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
 		bws.flush();
 		bws.close();
 		}catch(IOException e){
 			e.printStackTrace();
 		}
 	}
}
public void printConfigs(){
	System.out.println("**********************************");
	if(configs!=null){
	for(String key: configs.keySet()){
		System.out.print(key+" "+configs.get(key).getType()
				+ " " + configs.get(key).getMethod()
				+ " " + configs.get(key).getIsInDatabaseDescriptor()
				+ " "+configs.get(key).getIsInyaml()
				+ " "+configs.get(key).getIsCommentedInyaml()
				+ " "+configs.get(key).getDefaultValue()
				);
		for(int i=0;i<configs.get(key).getJavafilenames().size();i++)
			if(i==0) System.out.print(" javafiles:"+configs.get(key).getJavafilenames().get(i));
			else System.out.print(":"+configs.get(key).getJavafilenames().get(i));
		System.out.println();
	}
	System.out.println("*****total:"+configs.size()+"**********");
	}
	else System.out.println("config is null...");
}
public void printJavafiles(){
	System.out.println("---------------------------------");
	if(javafiles!=null)
	for(String key : javafiles.keySet()){
		System.out.print(key);
		for(String configkey : javafiles.get(key).getConfiglinenums().keySet()){
			System.out.print(" "+configkey);
			ArrayList c = javafiles.get(key).getConfiglinenums().get(configkey);
			for(int i=0;i<c.size();i++)
			System.out.print(":"+c.get(i));
		}
		System.out.println();
	}
	else System.out.println("javafile is null...");
}
public void configsGenerate(String configjava, String yamlfile, String descriptorfile){
	/*
	   cassandra/apache-cassandra-2.0.7-src/src/java
	   /org/apache/cassandra/config/Config.java
	*/
	String ch=null;
	String chtype=null;	
	List<String> type = new ArrayList<String>();
	String otype=null;
	BufferedReader bin=null;
	File f = null;
	String conf=null;
	List<String> confignames = new ArrayList<String>();
	Config config;
	type.add(" Integer ");
	type.add(" Boolean ");
	type.add(" String ");
	type.add(" Double ");
	type.add(" Long ");
	type.add(" int ");
	type.add(" boolean ");
	type.add(" double ");
	type.add(" long ");
	f =new File(configjava);
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
				confignames.add(conf);
				config = new Config();
				config.setName(conf);
				config.setType(chtype.trim());
				configs.put(conf, config);
				}
			else if(ch.contains(";"))
				{
				conf = ch.substring(ch.indexOf(chtype)+chtype.length(), ch.indexOf(";"));
				confignames.add(conf);
				config = new Config();
				config.setName(conf);
				config.setType(chtype.trim());
				configs.put(conf, config);
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
			    confignames.add(conf);
			    config = new Config();
				config.setName(conf);
				config.setType(otype.trim());
				configs.put(conf, config);
			}
			else if(ch.contains(";")){
				str=ch.substring(ch.indexOf("public ")+7, ch.indexOf(";"));
				buf=str.split(" ");
			    otype=buf[0];
			    conf=buf[1];
			    confignames.add(conf);
			    config = new Config();
				config.setName(conf);
				config.setType(otype.trim());
				configs.put(conf, config);
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
	/*  check the configuration name in configure file "cassandra.yaml"
	   cassandra/apache-cassandra-2.0.7-src/conf/cassandra.yaml
	 */
	File confyaml = null;
	BufferedReader bfyaml = null;
	confyaml = new File(yamlfile);
	String yamline = null;
    if(confyaml.exists()){
    try{
	bfyaml = new BufferedReader(new FileReader(confyaml));
	while((yamline=bfyaml.readLine())!=null){
		for(int i=0;i<confignames.size();i++){
			if(yamline.contains(confignames.get(i))){
				if(yamline.contains("#")&&yamline.indexOf("#")<yamline.indexOf(confignames.get(i)))
						configs.get(confignames.get(i)).setIsCommentedInyaml(true);
				else { configs.get(confignames.get(i)).setIsCommentedInyaml(false);
						}
				if(yamline.contains(": ")&&yamline.split(": ").length>1){
					configs.get(confignames.get(i)).setDefaultValue(yamline.split(": ")[1]);
					//System.out.println(yamline.split(": ")[1]);
				}
				configs.get(confignames.get(i)).setIsInyaml(true);
			}
		}
	}
	bfyaml.close();
    }catch(FileNotFoundException e){
    	e.printStackTrace();
    }catch(IOException e1){
    	e1.printStackTrace();
    }
    }else{
    	System.out.println("File not found.");
    }
    /*
      configs_cassandra/Descriptor.java
     */
    File desf = null;
    BufferedReader brf = null;
    String chline = null;
    String[] infobufs = null;
    String confname=null;
    String methodname=null;
    desf = new File(descriptorfile);
    if(desf.exists()){
    	try{
    	brf=new BufferedReader(new FileReader(desf));
    	while((chline=brf.readLine())!=null){
    		infobufs=chline.split(":::");
    		confname=infobufs[0].substring(5,infobufs[0].length());
    		methodname=infobufs[1];
    		if(confignames.contains(confname)){
    			configs.get(confname).setIsinDatabaseDescriptor(true);
    			configs.get(confname).setMethod(methodname);
    			configmethods.put(methodname, confname);
    		}else System.out.println("^^^"+confname);
    	}
    	}catch(FileNotFoundException e){
    		e.printStackTrace();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }else{
    	System.out.println("File not found.");
    }

}

public List<Source> constructSourceFiles(String folder){
	String jpath=null;
	Map<String,Source> mapsources=null;
	List<Source> sources=null;
	Source jsource=null;
	String path="JSInfoVis/src/confdata/";
	String name="sourcefiles.txt";
	File jf=null;
	BufferedReader br =null;
	String ch=null;
	String[] lineinfo=null;
	String[] linenums=null;
	String filename=null;
	String[] files=null;
	String subfolder=null;
	mapsources=new HashMap<String,Source>();  //<filename,file>
	jpath="/src/java/org/apache/cassandra/"+folder+"/";
	//System.out.println("path:"+jpath);
	jf=new File(path+name);
	if(!jf.exists()) this.outputJavafiles();
	sources = new ArrayList<Source>();
	try{
	br=new BufferedReader(new FileReader(jf));
	while((ch=br.readLine())!=null)
	if(ch.contains(jpath)){
		
		lineinfo=ch.split(" ");
		filename=lineinfo[0].substring(jpath.length());
		if(!filename.contains("/")){
			jsource=new Source();
			jsource.setName(lineinfo[0]);
			sources.add(jsource);
			mapsources.put(lineinfo[0], jsource);
		}else{
			files=filename.split("/");
			if(files.length>2) subfolder=files[files.length-2]; //to simplify several sub folders
			else subfolder=files[0];
			Source jfile=new Source();
			jfile.setName(lineinfo[0]);
			if(mapsources.containsKey(jpath+subfolder+"/")){
				jsource=mapsources.get(jpath+subfolder+"/");
				jsource.getFiles().add(jfile);
			}else{
			jsource=new Source();
			jsource.setName(jpath+subfolder+"/");
			jsource.setIsFolder(true);
			jsource.getFiles().add(jfile);
			sources.add(jsource);
			mapsources.put(jpath+subfolder+"/", jsource);
			}
		}
		
	}
	}catch(IOException e){
		e.printStackTrace();
	}finally{
		try{
		br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	return sources;
}

public void outputJavafiles(){
	//private Map<String,Javafile> javafiles; // <javafilename,Javafile>
	String path="JSInfoVis/src/confdata/";
	String name="sourcefiles.txt";
	File jfile=null;
	BufferedWriter bw =null;
	String line=null;
	jfile=new File(path+name);
	try{
	bw=new BufferedWriter(new FileWriter(jfile));
	for(String key : javafiles.keySet())
	if(javafiles.get(key).getConfigname().size()>0)
	{
		line=key;
		for(String configname : javafiles.get(key).getConfiglinenums().keySet()){
			line+=" "+configname;
			for(int i=0; i<javafiles.get(key).getConfiglinenums().get(configname).size();i++)
				line+=":"+javafiles.get(key).getConfiglinenums().get(configname).get(i);
		}
		bw.write(line);
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

public void javafilesGenerate(String javafilesname){
	String path="JSInfoVis/src/confdata/";
	String name="callgraphs.txt";
	File f,fcall = null;
	BufferedReader brf = null;
	BufferedWriter bw=null;
	String line = null;
	Boolean inCodes = false;
	String method=null;
	String confname = null;
	String[] codeinfo = null;
	String javanamet=null;
	String samefile=null;
	Boolean insamefile=true;
	Map<Integer,String> nodes=null;
	String nodesvalue=null;
	int count=0;
	f = new File(javafilesname);
	fcall=new File(path+name);
	
	//javafilesname="configs_cassandra/javafiles.out"
	// problems in javafiles.out DatabaseDescriptor.getBroadcastAddress,DatabaseDescriptor.getPartitioner
	if(f.exists()){
		try{
			if(!fcall.exists()) fcall.createNewFile();
			bw=new BufferedWriter(new FileWriter(fcall));
		brf = new BufferedReader(new FileReader(f));
		while((line=brf.readLine())!=null){
			if(line.equals("BEGIN")){
				if((line=brf.readLine())!=null){
					method=line.substring(line.indexOf(".")+1);
					if(configmethods.containsKey(method))
						{
						nodes=new HashMap<Integer,String>();
						confname=configmethods.get(method);
						inCodes=true;
						nodes.put(-1, method);
						//System.out.println(method);
						bw.write(method+"->"+confname);
						bw.newLine();
						//System.out.println(method+"->"+confname);
						}
				}
			}else if(line.equals("END")){
				if(inCodes==true)  
				{bw.write("----------------------------");
			    bw.newLine();
			    count++;
			    }
				inCodes=false;
			    method=null;
			    confname=null;
			}
			else if(inCodes==true){
				/*
				//analysis of the source codes location and context
				//Server:::143:::2:::statement:::run:::EncryptionOptions.ClientEncryptionOptions
				if(line.contains(":::")&&line.contains("/src/java/")){
					codeinfo=line.split(":::");
					javanamet=codeinfo[4].substring(codeinfo[4].indexOf("/src/java/"));
					if(!configs.get(confname).getJavafilenames().contains(javanamet))
					//Map<String,Config> configs; // <configname,Config>
					configs.get(confname).getJavafilenames().add(javanamet);
					if(!javafiles.containsKey(javanamet)){
					//Map<String,Javafile> javafiles; // <javafilename,Javafile>
					Javafile jf = new Javafile();
					jf.setName(javanamet);
					ArrayList<Integer> lines = new ArrayList<Integer>();
					lines.add(Integer.parseInt(codeinfo[5]));
					Map<String,ArrayList<Integer>> conflines = new TreeMap<String,ArrayList<Integer>>();
					conflines.put(confname, lines);
					jf.setConfiglinenums(conflines);
					List<String> confnames = new ArrayList<String>();
					confnames.add(confname);
					jf.setConfigname(confnames);
					javafiles.put(javanamet, jf);
					}else {
						if(!javafiles.get(javanamet).getConfiglinenums().containsKey(confname))
							javafiles.get(javanamet).getConfiglinenums().put(confname, new ArrayList<Integer>());
						if(!javafiles.get(javanamet).getConfigname().contains(confname))
							javafiles.get(javanamet).getConfigname().add(confname);
						javafiles.get(javanamet).getConfiglinenums()
						.get(confname).add(Integer.parseInt(codeinfo[5]));
					}
				}
				*/
				if(line.contains(":::")&&(line.contains("org/apache/cassandra/")
						||line.contains("org.apache.cassandra.")))
				{
					//CallGraph analysis
					codeinfo=line.split(":::");
					if(codeinfo[5].contains("org.apache.cassandra.")){
						System.out.println("CC:"+method);
						
					}
					
						int parent = Integer.parseInt(codeinfo[2]);
						int id = Integer.parseInt(codeinfo[0]);
						String jname=null,jline=null;
						if(!nodes.containsKey(id))
						{
						  if(codeinfo[4].contains("org/apache/cassandra/"))
						  { nodesvalue=codeinfo[4]+"#"+codeinfo[5];
						    jname="/src/java/"+codeinfo[4].substring(codeinfo[4].indexOf("org/apache/cassandra/"));
						    jline=codeinfo[5];
						    if(codeinfo.length>8&&codeinfo[6].contains("2"))
								//"2:null" problem in source file line#87
							nodesvalue+="#"+codeinfo[7]+"#"+codeinfo[8];
						  }else if(codeinfo[5].contains("org.apache.cassandra.")){
							  nodesvalue=codeinfo[5].replace(".", "/")+".java#"
									  +codeinfo[7]+"#"+codeinfo[9]+"#"+codeinfo[6];
							  jname="/src/java/"+codeinfo[5].replace(".", "/")+".java";
							  jline=codeinfo[7];
						  }/*
						  else if(codeinfo[3].contains("org.apache.cassandra."))
							  if(codeinfo[4].matches("^[0-9]*$")){
								  nodesvalue=codeinfo[3].replace(".", "/")+".java#"
								  +codeinfo[4]+"#"+codeinfo[5]+"#"+codeinfo[6];
							  System.out.println(nodesvalue);
							  }
							 */
						if(nodes.containsKey(parent))
							bw.write(nodesvalue+"->"+nodes.get(parent));
						else bw.write(nodesvalue+"->"+nodes.get(-1));
							bw.newLine();
						nodes.put(id, nodesvalue);
						
						//analysis of the source codes location and context
						//javanamet=codeinfo[4].substring(codeinfo[4].indexOf("/src/java/"));
						if(jname!=null){
						javanamet=jname;
						System.out.println(javanamet);
						if(!configs.get(confname).getJavafilenames().contains(javanamet))
						//Map<String,Config> configs; // <configname,Config>
						configs.get(confname).getJavafilenames().add(javanamet);
						if(!javafiles.containsKey(javanamet)){
						//Map<String,Javafile> javafiles; // <javafilename,Javafile>
						Javafile jf = new Javafile();
						jf.setName(javanamet);
						ArrayList<Integer> lines = new ArrayList<Integer>();
						lines.add(Integer.parseInt(jline));
						Map<String,ArrayList<Integer>> conflines = new TreeMap<String,ArrayList<Integer>>();
						conflines.put(confname, lines);
						jf.setConfiglinenums(conflines);
						List<String> confnames = new ArrayList<String>();
						confnames.add(confname);
						jf.setConfigname(confnames);
						javafiles.put(javanamet, jf);
						}else {
							if(!javafiles.get(javanamet).getConfiglinenums().containsKey(confname))
								javafiles.get(javanamet).getConfiglinenums().put(confname, new ArrayList<Integer>());
							if(!javafiles.get(javanamet).getConfigname().contains(confname))
								javafiles.get(javanamet).getConfigname().add(confname);
							javafiles.get(javanamet).getConfiglinenums()
							.get(confname).add(Integer.parseInt(jline));
						}
						}
						}
				}
				
			}
		}
		//System.out.println(count);
		for(String key : javafiles.keySet()){
			Collections.sort(javafiles.get(key).getConfigname());
			for(String cof : javafiles.get(key).getConfiglinenums().keySet())
				Collections.sort(javafiles.get(key).getConfiglinenums().get(cof));
		}
		for(String key : configs.keySet())
    		Collections.sort(configs.get(key).getJavafilenames());
		bw.close();
		brf.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}else
		System.out.println("File not found.");
	
	this.outputConfigs();
	this.outputConfigfullRelation();
	this.outputJavafiles();
}
}
