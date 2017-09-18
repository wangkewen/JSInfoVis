var count=0,countclick=0,fcount=0;
function getConfigname(obj,urltype){
	var vobj = null;
	if(obj!='S')
	{vobj=document.getElementById(obj);
	vobj.style.display=(vobj.style.display=='none')? 'block':'none';
	vobj='';
	}else vobj='S';
$.ajax({
	type:"POST",
	url: "GetConfigname?type="+urltype+vobj,
	dataType: "JSON",
	success: function(data){
		//alert(JSON.stringify(data));
		var colors=["#006400","#800000","#FF0000","#0000FF",
				"#800080","#FF1493","#00BFFF","#D2691E;",
				"#9ACD32","#FF8C00","#FFD700","#00FF7F"];
		$.each(data, function(i,item){
			var nametext;
//			if(item.group>0)
//			if(item.group <10)
//				nametext='0'+item.group+"&nbsp;&nbsp;&nbsp;"+item.name;
//			else nametext=item.group+"&nbsp;&nbsp;&nbsp;"+item.name;
//			else 
				nametext=item.name;
			if(i<9)
				nametext='0'+(i+1)+".&nbsp;&nbsp;&nbsp;"+nametext;
			else
				nametext=(i+1)+".&nbsp;&nbsp;&nbsp;"+nametext;
			
			if(count==0||urltype!="related"){
			var eachrow=$("#"+urltype+"cname").clone();
			
			eachrow.find("#"+urltype+"name").html(nametext);
			//eachrow.find("#"+urltype+"name").attr("id","related"+item.name);
			eachrow.find("#"+urltype+"type").text("Type : "+item.type+".");
			eachrow.find("#"+urltype+"descriptor").text("In DatabaseDescriptor.java : "+item.isInDatabaseDescriptor+".");
			if(item.isInDatabaseDescriptor==true)
				eachrow.find("#"+urltype+"method").text("Getting method : "+item.method+".");
			eachrow.find("#"+urltype+"yaml").text("In cassandra.yaml : "+item.isInyaml+".");
			if(item.isInyaml==true)
				eachrow.find("#"+urltype+"yamlcomment").text("Is commented in cassandra.yaml : "+item.isCommentedInyaml+".");
			if(item.defaultValue!=null&&item.defaultValue!="null")
				eachrow.find("#"+urltype+"default").text("Default value in cassandra.yaml : "+item.defaultValue+".");
			eachrow.find("#"+urltype+"name").attr("id",urltype+"name"+i);
			eachrow.appendTo("#"+urltype+"ulcname");
			eachrow.find("#nodecall").attr("id",item.name);
			eachrow.find("#nodename").attr("id",item.name);
			eachrow.find("#groupname").html("Group "+item.group);
			eachrow.find("#groupname").attr("id",item.group);
			eachrow.find("#"+urltype+"type").attr("id",urltype+"type"+i);
			eachrow.find("#"+urltype+"descriptor").attr("id",urltype+"descriptor"+i);
			eachrow.find("#"+urltype+"method").attr("id",urltype+"method"+i);
			eachrow.find("#"+urltype+"yaml").attr("id",urltype+"yaml"+i);
			eachrow.find("#"+urltype+"yamlcomment").attr("id",urltype+"yamlcomment"+i);
			eachrow.find("#"+urltype+"default").attr("id",urltype+"default"+i);
			eachrow.attr("id","#"+urltype+"cname"+item.name);
			}else {
					$("#"+urltype+"name"+i).html(nametext);
					$("#"+urltype+"type"+i).html("Type: "+item.type+".");
					$("#"+urltype+"descriptor"+i).html("In DatabaseDescriptor.java : "+item.isInDatabaseDescriptor+".");
					if(item.isInDatabaseDescriptor==true)
						$("#"+urltype+"method"+i).html("Getting method :  "+item.method+".");
					$("#"+urltype+"yaml"+i).html("In cassandra.yaml : "+item.isInyaml+".");
					if(item.isInyaml==true)
						$("#"+urltype+"yamlcomment"+i).html("Is commented in cassandra.yaml : "+item.isCommentedInyaml+".");
					if(item.defaultValue!=null&&item.defaultValue!="null")
						$("#"+urltype+"default"+i).html("Default value in cassandra.yaml : "+item.defaultValue+".");
					$("#"+urltype+"default"+i).next().attr("id",item.name);
					$("#"+urltype+"default"+i).next().next().attr("id",item.name);
					$("#"+urltype+"default"+i).next().next().next().attr("id",item.group);
					$("#"+urltype+"default"+i).next().next().next().html("Group "+item.group);
			}
		});
		if(urltype=="related") count++;
		$("#"+urltype+"cname").html("");
		for(var k=0;k<data.length;k++)
			$("#"+urltype+"name"+k).css({"color":colors[data[k].group-1]});
		
	},
	error: function(){
		alert("ERROR....");
	}  
});
}

function getCodeslist(folder,showid){
	var show=null;
	//alert(showid);
	//show=document.getElementById(showid);
	show=$("#"+showid).css("display");
//	if(show.style.display == "none" || show.style.display == null){
//		$("#"+folder+"img").attr("src","folder-open-128.png");
//		show.style.display = "block";
//	}else if(show.style.display == "block"){
//		$("#"+folder+"img").attr("src","icon-folder-128.png");
//		show.style.display = "none";
//	}
	if(show == "none" || show == null){
		$("#"+folder+"img").attr("src","folder-open-128.png");
		$("#"+showid).css({"display":"block"});
	}else if(show == "block"){
		$("#"+folder+"img").attr("src","icon-folder-128.png");
		$("#"+showid).css({"display":"none"});
	}
	//$("#codecname").empty();
	$.ajax({
		type: "POST",
		url: "GetCodeslist?folder="+folder,
		dataType: "JSON",
		success: function(data){
			//alert(data);
			var folderli=$("#ccode_"+folder).find("#codeulcname");
			var fli=folderli.find("#codecname");
			//alert(fli.length);
			$.each(data, function(i,item){
				//if(count1==0){
				var jname=item.name;
				var filename=jname.split("/"+folder+"/");
				var eachone=fli.clone();
				var subname=null;
				eachone.appendTo(folderli);
				
				//alert(eachone.length);
				
				if(filename[1].contains("/")){
					subname=filename[1].split("/")[0];
					eachone.find("#imgl").attr("width","25px");
					eachone.find("#imgl").attr("height","25px");
					eachone.find("#imgl").attr("src","icon-folder-128.png");
					eachone.find("#codefilename").css({"font-size":"18px"});
					eachone.find("#codefilename").css({"color":"#00f"});
					
					eachone.find("#codefilename").html(subname);
					eachone.find("#codefilename").attr("id","codefilename_"+subname);
					eachone.find("#codefile").next().remove();
					//eachone.find("#codefile").attr("onclick","isSHidden('subcodeulcname')");
					//alert(name+item.files.length);
					var ul=null;
					ul=eachone.find("#subcodeulcname");
//					for(var k=0;k<item.files.length;k++){
//						var eachsub=null;
//						//alert(item.files[k].name);
//						eachsub=ul.find("#subcodecname").clone();
//						eachsub.find("#subcodefilename").html(item.files[k].name);
//						
//						
//						eachsub.find("#subcodefile").attr("id",folder+"_"+name+"_"+k);
//						eachsub.appendTo(ul);
//					}
					ul.attr("id",folder+"_"+subname+"_ul");
					ul.find("#subcodecname").attr("id",folder+"_"+subname+"_li");
					eachone.find("#codefile").attr("id",folder+"-"+subname);
					eachone.attr("id","#codecname_"+subname);
					//ul.find("#subcodecname").html("");
				}else{
					//alert(filename[1]);
					subname=filename[1];
					eachone.find("#imgl").attr("width","20px");
					eachone.find("#imgl").attr("height","20px");
					eachone.find("#imgl").attr("src","editor_document_file_2-128.png");
					//alert(eachone.find("#codefilename").length);
					//alert(subname);
					eachone.find("#codefilename").html(subname);
					eachone.find("#codefilename").attr("id","codefilename_"+subname.split(".")[0]);
					eachone.find("#codefile").next().next().remove();
					eachone.find("#codefile").attr("id",folder+"_"+subname.split(".")[0]);

					eachone.find("#codelocat").attr("id",item.name.replace(/\//g,"_"));
					eachone.attr("id","#codecname_"+subname);
				}
				
				
			//}
			});
			fli.html("");
			
			for(var i=0;i<data.length;i++){
				var fname=data[i].name;
				var skname=fname.split("/"+folder+"/");
				if(skname[1].contains("/")){
				var sname=skname[1].split("/")[0];
				var sul=folderli.find("#"+folder+"_"+sname+"_ul");
				for(var k=0;k<data[i].files.length;k++){
					var eachsub=null;
					var fullname=null;
					fullname=data[i].files[k].name.split("/");
					//alert(data[i].files[k].name);
					eachsub=sul.find("#"+folder+"_"+sname+"_li").clone();
					eachsub.find("#subimgl").attr("width","20px");
					eachsub.find("#subimgl").attr("height","20px");
					eachsub.find("#subimgl").attr("src","editor_document_file_2-128.png");
					eachsub.find("#subcodefilename").html(fullname[fullname.length-1]);
					eachsub.find("#subcodefile")
					.attr("id",folder+"_"+sname+"_"+fullname[fullname.length-1].split(".")[0]);
					
					eachsub.find("#subcodelocat").attr("id",data[i].files[k].name.replace(/\//g,"_"));
					
					eachsub.appendTo(sul);
				}
				sul.find("#"+folder+"_"+sname+"_li").html("");
				}
			}
		},
		error: function(){
			alert("ERROR...");
		}
	});
}
function showcodes(filename){
	//alert(filename);
	window.open ("GetCodefile?filename="+filename, "newwindow"+countclick, "height=500, width=800, " +
	"top=300, left=300, toolbar=no, menubar=yes, scrollbars=yes, resizable=no,location=no, status=no");
	countclick++;
}
function expandcode(id){
	//alert(id);
	if(id==null||id=="null") id="";
	show=$("#codes"+id).css("display");
	if(show == "none" || show == null){
		$("#codes"+id).css({"display":"block"});
	}else if(show == "block"){
		$("#codes"+id).css({"display":"none"});
	}
}
function loadfolder(){
	//alert("fkkkkkkkk");
	$.ajax({
		type: "POST",
		url:  "GetCodeslist?folder=_",
		dataType: "JSON",
		success: function(data){
			//alert(data);
			var root=$("#grouplist");
			var rli=root.find("#groupfolder");
			$.each(data, function(i,item){
				var eachfolder=rli.clone();
				eachfolder.find("#foldername").html(item);
				eachfolder.appendTo(root);
				eachfolder.find("#img").attr("src","icon-folder-128.png");
				eachfolder.find("#img").attr("height","25px");
				eachfolder.find("#img").attr("width","25px");
				eachfolder.find("#img").attr("id",item+"img");
				eachfolder.find("#folder").attr("id","folder_"+item);
				eachfolder.find("#groupfoldername").attr("id",item);
				eachfolder.find("#foldername").attr("id","foldername_"+item);
				eachfolder.find("#ccode").attr("id","ccode_"+item);
				eachfolder.attr("id","#groupfolder_"+item);
			});
			rli.html("");
		},
		error: function(){
			alert("ERROR...");
		}
	});
}

function sort(){
	var k=document.getElementById("#relatedulcname");
	var len=k.getElementsByTagName("li");
	alert(len);
}

function conflist(filename){
	$.ajax({
		type: "POST",
		url: "GetCodefile?filename="+filename+"______",
		dataType:"JSON",
		success: function(data){
			//alert(data);
			var file=$("#"+filename).next();
			var node=file.find("#nodename");
			if(node==null || node.length<1) node=file.find("#subnodename");
			//alert(data.length);
			$.each(data, function(i, item){
				var conf=node.clone();
				conf.html(item);
				conf.attr("id",item);
				conf.appendTo(file);
			});
			node.remove();
		},
		error: function(){
			alert("Error......");
		}
	});
}

function isHidden(obj){
	//alert($("#"+obj).next().css("display"));
	//var vobj = document.getElementById(obj);
	//alert(obj);
	var odiv=$("#"+obj);
	var vobj=$("#"+obj).next().css("display");
	//alert(odiv.text());
	if(vobj == null || vobj== "none"){
		//alert(odiv.text().split(".")[1].trim());
		if(odiv.text().contains("."))
		init(odiv.text().split(".")[1].trim());
		$("#"+obj).next().css({"display":"block"});
		
		}
	else if(vobj == "block"){
		$("#"+obj).next().css({"display":"none"});
		
		}
}
function iscHidden(obj){
	//alert($("#"+obj).next().css("display"));
	//var vobj = document.getElementById(obj);
	//alert(obj);
	var odiv=$("#"+obj);
	var vobj=$("#"+obj).next().css("display");
	//alert(odiv.text());
	if(vobj == null || vobj== "none"){
		$("#"+obj).next().css({"display":"block"});
		if(obj.contains("-")) odiv.find("#imgl").attr("src","folder-open-128.png");
		else conflist(obj);
		}
	else if(vobj == "block"){
		$("#"+obj).next().css({"display":"none"});
		if(obj.contains("-")) odiv.find("#imgl").attr("src","icon-folder-128.png");
		}
}
function getJava(){
	var link=window.location.href;
	var node=link.substring(link.indexOf("=",0)+1,link.length);
	$.ajax({
		type:"POST",
		url:"GetJavafiles?node="+node,
		dataType:"text",
		success: function(data){
			//document.write(data);
			var codes="";
			//$("#sourcefile").html(codes);
			//$(codes).appendTo("#sourcefile");
			//document.write(codes);
		},
		error: function(){
			alert("Error...");
		}
	});
}