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

public class GetCodefile extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public GetCodefile() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String pfilename=null,filename=null;
		String rootpath="cassandra/apache-cassandra-2.0.7-src";
		String partialpath="/src/java/org/apache/cassandra/";
		String sfpath="JSInfoVis/src/confdata/";
		String sfname="sourcefiles.txt";
		Boolean conflist=false;
		Boolean call=false;
		String lineN=null;
		List<String> confs=null;
		pfilename=request.getParameter("filename");
		
		confs=new ArrayList<String>();
		if(pfilename.contains("______")) {conflist=true;pfilename=partialpath+(pfilename.split("______")[0])+".java";}
		
		if(pfilename.contains("__LINE")){
			//for callgraph
			call=true;
			lineN=pfilename.substring(pfilename.indexOf("__LINE")+6);
			filename="/src/java/"+pfilename.split("__LINE")[0].replace("_", "/");
			System.out.println(filename);
		}else if(!pfilename.contains("_")&&pfilename.contains("(")){
			filename="/src/java/org/apache/cassandra/config/DatabaseDescriptor.java";
			File s=new File(rootpath+filename);
			int n=0;
			BufferedReader sbr=new BufferedReader(new FileReader(s));
			String sline=null;
			while((sline=sbr.readLine())!=null){
				n++;
				if(sline.contains(pfilename)){
					lineN=n+"";
					call=true;break;
				}
			}
			sbr.close();
		}else filename=pfilename.replace("_", "/");
		//System.out.println(filename);  
		File source=null,source1=null;
		BufferedReader br =null;
		String[] linelist=null;
		String line=null;
		String linesnum=null;
		String titles=null;
		if(!conflist){
			source1=new File(rootpath+filename);
			titles=filename;
			if(!source1.exists()) 
				{source1=new File(rootpath+filename.substring(0, 
						filename.lastIndexOf("/"))+".java");
				titles=filename.substring(0, 
						filename.lastIndexOf("/"))+".java$"
						+filename.split("/")[filename.split("/").length-1];
				}
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
	    		+ " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
	    out.println("<html  xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
	    out.println("<head>");
	    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
	    out.println("<script type=\"text/javascript\" src=\"jquery-2.1.1.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\"show.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\"scripts/shCore.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\"scripts/shBrushJava.js\"></script>");
	    out.println("<link href=\"styles/shCore.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
	    out.println("<link href=\"styles/shThemeEclipse.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
	    out.println("<script type=\"text/javascript\">SyntaxHighlighter.all();</script>");
	    out.println("<title>"+titles+"   </title>");
	    out.println("</head>");
	    out.println("<body>");
	    out.println("<div onclick=\"expandcode(null)\" style=\"margin-top:10px; color:#00f;  cursor:pointer;\">");
		out.println(filename.split("/")[filename.split("/").length-1]);
		out.println("</div>");
		}
		if(!call){
		source = new File(sfpath+sfname);
		br=new BufferedReader(new FileReader(source));
		while((line=br.readLine())!=null)
		if(line.contains(filename)){
			linesnum="";
			linelist=line.split(" ");
			String[] linenum =null;
			for(int k=1;k<linelist.length;k++){
				linenum=linelist[k].split(":");
				confs.add(linenum[0]);
				if(!conflist){
				out.println(k+"."+linenum[0]+"<br>&nbsp;&nbsp;&nbsp;LineNum:");
				for(int y=1;y<linenum.length;y++){
					out.println("&nbsp;&nbsp"+linenum[y]);
					linesnum+=","+linenum[y];
				}
				out.println("<br>");
				}
			}
			if(!conflist) linesnum=linesnum.substring(1);
		}
		br.close();
		}else{
			linesnum=lineN;
			out.println("<br>LineNum:"+lineN);
			out.println("<br>");
		}
		if(!conflist){
		
		br=new BufferedReader(new FileReader(source1));
		out.println("<div id=codes style=\"display:none; border-top:solid 2px #0f0; border-bottom:solid 2px #0f0; height:400px; overflow:auto\">");
		out.println("<pre  class=\"brush: java; collapse: false; highlight: ["+linesnum+"];\">");
		while((line=br.readLine())!=null)
			out.println(line);
		br.close();
		out.println("</pre>");
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
		}else{
			Gson g =new Gson();
			String json=g.toJson(confs);
			out.println(json);
			out.flush();
		}
		
	}
}
