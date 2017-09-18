package confdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Javafile {
private String name; //partial path+name:/src/java/org/apache.../*.java
private List<String> configname;
private Map<String,ArrayList<Integer>> configlinenums;

public Javafile(){
	name=null;
	configname=new ArrayList<String>();
	configlinenums=new TreeMap<String,ArrayList<Integer>>();
}
public String getName(){
	return name;
}
public void setName(String name){
	this.name=name;
}
public List<String> getConfigname(){
	return configname;
}
public void setConfigname(List<String> configname){
	this.configname=configname;
}
public Map<String,ArrayList<Integer>> getConfiglinenums(){
	return configlinenums;
}
public void setConfiglinenums(Map<String,ArrayList<Integer>> configlinenums){
	this.configlinenums=configlinenums;
}
}
