//$(document).ready(
function cc()
{
	//alert("ss");
$.ajax
({
type: "POST",
url: "ReceiveServlet",
dataType: "JSON",
success: function(data)
{
     //alert(data);
//	 $.getJSON("ReceiveServlet", function(data){
//         for(var i = 0; i < data.length; i++)
//         {
//             alert("subject:" + data[i].subject);
//             alert("content��" + data[i].content);
//         }
//    });	
	$("#content").empty();	
if(data == "Login first!"){    
		var cha="";
		cha="<a href=\"index.jsp\">"+data+"</a>";
		$(cha).appendTo("#content");
		//window.location.href="index.jsp";
}
if(data.length)
{
	var jsondata =  eval('('+ data +')');
	//alert(data.subject);


$.each(jsondata, function(i,item)
{
	//alert(item);
var att="";
for(var k=0; k<item.attname.length;k++)
	att += "<a href=Download?filename="+item.attname[k]+
	"&&filepath="+item.attpath[k]+">"+item.attname[k]+"</a>  ";
var msg_data="<div id=\"outside\" onclick=\"isHidden('"+i+"')\">"+"<span id=\"subject\">"+item.subject+"</span>"
              +"<span id=\"time\">"+item.sendDate+"</span>"+"<br></div>";
    msg_data +="<div id=\"inside\"><div id='"+i+"'style=\"display:none\">"+"<div><span>From</span>"+item.from.address+"</div>"
    +"<div><span>Content</span>"+item.content+"</div>"
    +"<div><span id=\"digest\">Digest1</span>"+item.digest1+"</div>"
    +"<div><span id=\"digest\">Digest2</span>"+item.digest2+"</div><div>"+att+"</div></div></div>";
$(msg_data).appendTo("#content");
});
}
else
{	
$("#content").html("No Results");
}
}
});
//$('#UpdateButton').click(function()
//{
//	alert("click");
//// Previous Post
//});

//return false;
}

function isHidden(oDiv){
    var vDiv = document.getElementById(oDiv);
    vDiv.style.display = (vDiv.style.display == 'none')?'block':'none';
  }
function show(){
	$("#content").load("send.jsp"); 
}
function home(){
	window.location.href="receive.jsp";
}
//);