package confdata;

import java.util.ArrayList;
import java.util.List;

public class Filesgraph {
	private String id;
	private String name;
	private List<Filesgraph> children;
	public Filesgraph(){
		id=null;
		name=null;
		children=new ArrayList<Filesgraph>();
	}
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name=name;
	}
	public List<Filesgraph> getChildren(){
		return children;
	}
	public void setChildren(List<Filesgraph> children){
		this.children=children;
	}
}
