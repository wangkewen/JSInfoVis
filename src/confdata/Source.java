package confdata;

import java.util.ArrayList;
import java.util.List;

public class Source {
	private String name; //partial path+name:/src/java/org/apache.../*.java
	private Boolean isFolder; // it is folder or *.java file
	private List<Source> files;
	public Source(){
		name=null;
		isFolder=false;
		files=new ArrayList<Source>();
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name=name;
	}
	public Boolean getIsFolder(){
		return isFolder;
	}
	public void setIsFolder(Boolean isFolder){
		this.isFolder=isFolder;
	}
	public List<Source> getFiles(){
		return files;
	}
	public void setFiles(List<Source> files){
		this.files=files;
	}
}
