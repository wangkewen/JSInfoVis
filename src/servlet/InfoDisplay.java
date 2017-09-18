package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import confdata.CallGraph;
import confdata.ConfigInfo;
import confdata.Configsgraph;


public class InfoDisplay extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public InfoDisplay() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		Gson info = new Gson();
		String scale= null;
		String jsonInfo=null;
		scale=request.getParameter("scale");
		String in= "[{\"adjacencies\": [],"
				+ "\"data\": "
				+ "{\"$color\": \"#EBB056\","
				+ "\"$type\": \"triangle\","
				+ "\"$dim\": 12},\"id\": "
				+ "\"graphnode7\","
				+ "\"name\": \"graphnode7\"}]";
		//System.out.println("$$"+scale);
		ConfigInfo g = new ConfigInfo();
		if(scale.contains("CALLGRAPH_")){
			List<CallGraph> graphs = null;
			graphs=g.getCallGraphs(scale.substring(10));
			jsonInfo= info.toJson(graphs);
			//System.out.println(jsonInfo);
		}else{
			List<Configsgraph> graphs = null;
		if(scale.contains("ISO_")){
			//System.out.println(scale.substring(4));
			graphs=g.getIsolatedgraph(scale.substring(4));
		} 
		else if(scale==null||scale.equals("null")||scale.contains("null_")){
			g.getVirtualGraphs(scale);
			graphs = g.getVirgraphs();
		}
		else{
			g.getConfigsgraph(scale);
			graphs = g.getGraphs();
			if(graphs.size()==0)
				graphs=g.getIsolatedgraph(scale);
		}
		jsonInfo= info.toJson(graphs);
		}
		
		//System.out.println(jsonInfo);
		out.println(jsonInfo);
		out.flush();
	}
}
