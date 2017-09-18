package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import confdata.Filesgraph;

public class FileInfoDisplay extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public FileInfoDisplay() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	private int contains(List<Filesgraph> list, String folder){
		if(list==null || list.size()<1) return -1;
//		if(folder.contains("\n")) folder=folder.replace("\n", "");
		for(int i=0;i<list.size();i++)
			if(list.get(i).getName().equals(folder)) return i;
		return -1;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out=response.getWriter();
		String sfpath="JSInfoVis/src/confdata/";
		String sfname="sourcefiles.txt";
		Filesgraph graph=null;
		graph=new Filesgraph();
		graph.setId("00");
		graph.setName("cassandra");
		File f=null;
		BufferedReader br=null;
		String line=null;
		String[] linearray=null;
		String[] folders=null;
		List<Filesgraph> fg=null;
		Filesgraph fgraph=null,fcgraph=null;
		f=new File(sfpath+sfname);
		br = new BufferedReader(new FileReader(f));
		int xb=1;
		while((line=br.readLine())!=null){
			linearray=line.split(" ");
			folders=linearray[0].split("/");
			if(folders.length>6){
				fg = graph.getChildren();
				int index=0;
				for(int i=6;i<folders.length;i++){
					index=contains(fg,folders[i]);
					if(fg.size()<1||index<0){
						fgraph=new Filesgraph();
						fgraph.setId(i-5+""+xb);
//						if(folders[i].length()>10)
//						fgraph.setName(folders[i].substring(0, 5)+"\n"+folders[i].substring(5));
//						else
							fgraph.setName(folders[i]);
						fg.add(fgraph);
						xb++;
						index=fg.size()-1;
						//System.out.println("inside:"+index);
					}
					//System.out.println(index);
					fg=fg.get(index).getChildren();
				}
				for(int j=1;j<linearray.length;j++){
					fcgraph=new Filesgraph();
					fcgraph.setId(folders.length-5+""+xb);
					fcgraph.setName(linearray[j].split(":")[0]);
					fg.add(fcgraph);
					xb++;
				}
			}
		}
		br.close();
		Gson g = new Gson();
		String json=g.toJson(graph);
		out.println(json);
		out.flush();
		//System.out.println(json);
	}

}
