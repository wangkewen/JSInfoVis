package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import confdata.ConfigInfo;
import confdata.Source;

public class GetCodeslist extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public GetCodeslist() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String codes=null;
		List<Source> codeslist=null;
		List<String> folderlist=null;
		String sfpath="JSInfoVis/src/confdata/";
		String sfname="sourcefiles.txt";
		File f=null;
		BufferedReader br=null;
		String line=null;
		String[] linearray=null;
		ConfigInfo cf=null;
		String folder=null;  
		cf=new ConfigInfo();
		Gson gs = new Gson();
		folder=request.getParameter("folder");
		if(folder.equals("_")){
			folderlist=new ArrayList<String>();
			f=new File(sfpath+sfname);
			br=new BufferedReader(new FileReader(f));
			while((line=br.readLine())!=null){
				linearray=line.split("/");
				if(linearray.length>6) 
					if(!folderlist.contains(linearray[6]))
						folderlist.add(linearray[6]);
			}
			br.close();
			codes=gs.toJson(folderlist);
		}else{
		codeslist=cf.constructSourceFiles(folder);
		codes=gs.toJson(codeslist);
		//System.out.println(folder);
		}
		out.println(codes);
		out.flush();
	}

}
