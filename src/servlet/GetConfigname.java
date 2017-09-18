package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import confdata.Config;
import confdata.ConfigInfo;


public class GetConfigname extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetConfigname() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String type=null;
		Integer max=0;
		type=request.getParameter("type");
		//System.out.println(type);
		List<Config> conflist = new ArrayList<Config>();
		ConfigInfo cfi = new ConfigInfo();
		conflist=cfi.constructConfigs(type);
		if(type.equals("relatedS")){
			List<Config> sortedlist = new ArrayList<Config>();
			for(int i=0;i<conflist.size();i++)
				if(conflist.get(i).getGroup()>max) max=conflist.get(i).getGroup();
			List<ArrayList<Config>> sorted = new ArrayList<ArrayList<Config>>();
			for(int i=0;i<max;i++)
				sorted.add(new ArrayList<Config>());
			for(int j=0;j<conflist.size();j++)
				sorted.get(conflist.get(j).getGroup()-1).add(conflist.get(j));
			for(int i=0;i<sorted.size();i++)
				for(int j=0;j<sorted.get(i).size();j++)
					sortedlist.add(sorted.get(i).get(j));
			conflist=sortedlist;
			//for(int i=0;i<conflist.size();i++) System.out.print(conflist.get(i).getName()+" ");
		}
		
	    Gson gsondata = new Gson();
	    String jsondata= null;
//		String jsondata="[{\"Name\":\"start_rpc\", \"Type\": \"Boolean\"}"
//				+ ",{\"Name\": \"listen_address\", \"Type\":\"String\", \"Commented\": \"Yes\"}"
//				+ ",{\"Name\": \"inter_dc_tcp_nodelay\", \"Type\":\"Boolean\", \"Commented\": \"No\"}"
//				+ "]";
	    jsondata = gsondata.toJson(conflist);
	    //System.out.println(jsondata);
		out.println(jsondata);
		out.flush();
	}

}
