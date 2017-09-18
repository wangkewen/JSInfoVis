package confdata;

import java.util.ArrayList;
import java.util.List;

public class Configsgraph {
	private String id;
	private String name;
	private data data;
	private List<adjacency> adjacencies; 
	public Configsgraph(){
		id=null;
		name=null;
		data = new data();
		adjacencies=new ArrayList<adjacency>();
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
	public data getData(){
		return data;
	}
	public void setData(data data){
		this.data = data;
	}
	public List<adjacency> getAdjacencies(){
		return adjacencies;
	}
	public void setAdjacencies(List<adjacency> adjacencies){
		this.adjacencies=adjacencies;
	}
	class data{
		private String $color;
		private String $type;
		private Integer $dim;
		public data(){
			$color=null;
			$type=null;
			$dim=null;
		}
		public String get$color(){
			return $color;
		}
		public void set$color(String $color){
			this.$color=$color;
		}
		public String get$type(){
			return $type;
		}
		public void set$type(String $type){
			this.$type=$type;
		}
		public Integer get$dim(){
			return $dim;
		}
		public void set$dim(Integer $dim){
			this.$dim=$dim;
		}
	}
	class adjacency{
		private String nodeTo;
		private String nodeFrom;
		private data data;
		public adjacency(){
			nodeTo=null;
			nodeFrom=null;
			data=null;
		}
		public String getNodeTo(){
			return nodeTo;
		}
		public void setNodeTo(String nodeTo){
			this.nodeTo=nodeTo;
		}
		public String getNodeFrom(){
			return nodeFrom;
		}
		public void setNodeFrom(String nodeFrom){
			this.nodeFrom=nodeFrom;
		}
		public data getData(){
			return data;
		}
		public void setData(data data){
			this.data=data;
		}
	}
}
