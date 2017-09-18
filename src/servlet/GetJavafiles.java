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

import confdata.Config;
import confdata.ConfigInfo;

public class GetJavafiles extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetJavafiles() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pnode=request.getParameter("node");
		PrintWriter out=response.getWriter();
		String rootpath="cassandra/apache-cassandra-2.0.7-src";
		Config conf = new Config();
		ConfigInfo cfi = new ConfigInfo();
		conf=cfi.constructConf(pnode);
	    out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""+ 
	    		"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
	    out.println("<html  xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
	    out.println("<head>");
	    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
	    out.println("<script type=\"text/javascript\" src=\"jquery-2.1.1.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\"show.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\"scripts/shCore.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\"scripts/shBrushJava.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\"scripts/shBrushJScript.js\"></script>");
	    out.println("<link href=\"styles/shCore.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
	    out.println("<link href=\"styles/shThemeEclipse.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
	    out.println("<script type=\"text/javascript\">SyntaxHighlighter.all();</script>");
	    out.println("<title>"+pnode+"   </title>");
	    out.println("</head>");
	    out.println("<body>");
	    
	    
	    File f=null;
	    BufferedReader br =null;
	    String ch=null;
	    String lines="";
	    String path=null;
	    for(int i=0;i<conf.getJavafilenames().size();i++){
	    	br=null;
	    	//System.out.println(rootpath+conf.getJavafilenames().get(i));
	    f=new File(rootpath+conf.getJavafilenames().get(i));
	    path=rootpath+conf.getJavafilenames().get(i);
	    if(!f.exists()) 
	    	{f=new File(rootpath+conf.getJavafilenames().get(i).substring(0, 
	    			conf.getJavafilenames().get(i).lastIndexOf("/"))+".java");
	    	path=rootpath+conf.getJavafilenames().get(i).substring(0, 
	    			conf.getJavafilenames().get(i).lastIndexOf("/"))+".java$"
	    			+conf.getJavafilenames().get(i).split("/")
	    			[conf.getJavafilenames().get(i).split("/").length-1];
	    	}
	    lines="";
	    br=new BufferedReader(new FileReader(f));
	    ch="";
	    for(int k=0;k<conf.getJavalinenums().get(conf.getJavafilenames().get(i)).size();k++)
	    	if(k==0)
	    	lines+=conf.getJavalinenums().get(conf.getJavafilenames().get(i)).get(k);
	    	else
	    		lines+=","+conf.getJavalinenums().get(conf.getJavafilenames().get(i)).get(k);
	    out.println("<div onclick=\"expandcode("+i+")\" style=\"margin-top:10px; color:#00f;  cursor:pointer;\">");
	    out.println(conf.getJavafilenames().get(i).split("/")[conf.getJavafilenames().get(i).split("/").length-1]+"<br/>");
	    out.println("</div >");
	    out.println("<div>");
	    out.println("   Path:  "+path+"<br/>");
	    out.println("   Lines: "+lines);
	    out.println("</div >");
	    //System.out.println(lines);
	    
	    out.println("<div id=\"codes"+i+"\" style=\"display:none; border-top:solid 2px #0f0; border-bottom:solid 2px #0f0; height:400px; overflow:auto;\">");
	    int k=0;
	    
	    out.println("<pre id=\"pre"+i+"\" class=\"brush: java; highlight: ["+lines+"];\">");
	    out.println("<textarea>");
	    while((ch=br.readLine())!=null){
	    	//out.println(ch);
	    	//System.out.println(ch);
	    	out.println(ch);
	    	k++;
	    }
	    br.close();
	    out.println("</textarea>");
	    out.println("</pre>");
	   
	  
	    out.println("</div>");
	    }
	    
	    out.println("</body>");
	    out.println("</html>");
	    out.flush();
	}

}
