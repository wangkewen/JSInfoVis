package confdata;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
private String name;
private String type;
private Integer group;
private String method;
private List<String> javafilenames; // :/src/java/org/apache.../*.java
private Map<String,ArrayList<Integer>> javalinenums;
private Boolean isInDatabaseDescriptor; // DatabaseDescriptor.java
private Boolean isInyaml; // cassandra.yaml
private Boolean isCommentedInyaml;
private String defaultValue; // default value in cassandra.yaml

public Config(){
	name=null;
	type=null;
	group=0;
	method=null;
	javafilenames= new ArrayList<String>();
	javalinenums=new HashMap<String,ArrayList<Integer>>();
	isInDatabaseDescriptor=false;
	isInyaml=false;
	isCommentedInyaml=false;
	defaultValue=null;
}
public static void main(String[] args){

}
public String getName(){
	return name;
}
public void setName(String name){
	this.name=name;
}
public String getType(){
	return type;
}
public void setType(String type){
	this.type=type;
}
public Integer getGroup(){
	return group;
}
public void setGroup(Integer group){
	this.group=group;
}
public String getMethod(){
	return method;
}
public void setMethod(String method){
	this.method=method;
}
public List<String> getJavafilenames(){
	return javafilenames;
}
public void setJavafilenames(List<String> javafilenames){
	this.javafilenames=javafilenames;
}
public Map<String,ArrayList<Integer>> getJavalinenums(){
	return javalinenums;
}
public void setJavalinenums(Map<String,ArrayList<Integer>> javalinenums){
	this.javalinenums=javalinenums;
}
public Boolean getIsInDatabaseDescriptor(){
	return isInDatabaseDescriptor;
}
public void setIsinDatabaseDescriptor(Boolean isInDatabaseDescriptor){
	this.isInDatabaseDescriptor=isInDatabaseDescriptor;
}
public Boolean getIsInyaml(){
	return isInyaml;
}
public void setIsInyaml(Boolean isInyaml){
	this.isInyaml=isInyaml;
}
public Boolean getIsCommentedInyaml(){
	return isCommentedInyaml;
}
public void setIsCommentedInyaml(Boolean isCommentedInyaml){
	this.isCommentedInyaml=isCommentedInyaml;
}
public String getDefaultValue(){
	return defaultValue;
}
public void setDefaultValue(String defaultValue){
	this.defaultValue=defaultValue;
}
}
