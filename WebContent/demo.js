function upfile(){
//       	   $("#loading")
//              .ajaxStart(function(){
//                  $(this).show();
//              })
//              .ajaxComplete(function(){
//                  $(this).hide();
//              });
$.ajaxFileUpload
({
url:"FileUpload",
secureuri:false,
fileElementId:"upfileId",
dataType: "JSON",
success: function (data, status)
{
	
if(typeof(data.error) != 'undefined')
   {
    if(data.error != '')
      {
    	alert(data.error);
      }else
        {
         alert(data.msg);
        }
    }

$("#success").val(data);
},
error: function (data, status, e)
        {
          alert(e);
        }
});
} 

$(document).ready(function() {


   var $calendar = $('#calendar');
   var id = 10;

   $calendar.weekCalendar({
      timeslotsPerHour : 4,
      allowCalEventOverlap : true,
      overlapEventsSeparate: true,
      firstDayOfWeek : 1,
      businessHours :{start: 8, end: 18, limitDisplay: true },
      daysToShow : 7,
      height : function($calendar) {
         return $(window).height() - $("h1").outerHeight() - 1;
      },
      eventRender : function(calEvent, $event) {
         if (calEvent.end.getTime() < new Date().getTime()) {
            $event.css("backgroundColor", "#aaa");
            $event.find(".wc-time").css({
               "backgroundColor" : "#999",
               "border" : "1px solid #888"
            });
         }
      },
      draggable : function(calEvent, $event) {
         return calEvent.readOnly == true;
      },
      resizable : function(calEvent, $event) {
         return calEvent.readOnly == true;
      },
      eventNew : function(calEvent, $event) {
    	  
    	  
    	  
         var $dialogContent = $("#event_edit_container");
         resetForm($dialogContent);
         var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
         var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
         var titleField = $dialogContent.find("input[name='title']");
         var bodyField = $dialogContent.find("textarea[name='body']");
         

         $("#fileup").empty();
         $("#filedownload").empty();
         var $fileup=$("#fileup");
//         var up="<label for=\"UploadFile\">UploadFile: </label><input type=\"file\" id=\"upfileId\" />"+
//         		"<label for=\"Button\"></label><input type=\"button\" value=\"Upload\" onclick=\"upfile()\"/>";
         //var up="<input type=\"file\" name=\"runfile\"/>";
         var up ="<ul>"+
		"<li><label for=\"UploadFile\"><input type=\"file\" name=\"upf\" id=\"upfileId\" /></li>"+
		"<li><label for=\"Button\"><input type=\"button\" value=\"Upload\" onclick=\"upfile()\"/>" +
		"<li><label for=\"Sign\"><input type=\"text\" name=\"sign\" id=\"success\" /></li>"+
		"</ul>";
  	   //$dialogContent.load("login.jsp"); 
         
  	     $(up).appendTo($fileup);

  	     var fileField= $dialogContent.find("input[name='sign']");
  	     
         $dialogContent.dialog({
            modal: true,
            title: "New Calendar Event",
            close: function() {
               $dialogContent.dialog("destroy");
               $dialogContent.hide();
               $('#calendar').weekCalendar("removeUnsavedEvents");
            },
            buttons: {
               save : function() {
            	  $.getJSON("AddSchedule",{title: titleField.val(), body: bodyField.val(),
            		  begintime: startField.val(), endtime: endField.val(),filename: fileField.val()},
            		  function(data){});
                  calEvent.id = id;
                  //id++;
                  calEvent.start = new Date(startField.val());
                  calEvent.end = new Date(endField.val());
                  calEvent.title = titleField.val();
                  calEvent.body = bodyField.val();
                  
                  $calendar.weekCalendar("removeUnsavedEvents");
                  $calendar.weekCalendar("updateEvent", calEvent);
                  $dialogContent.dialog("close");
               },
               cancel : function() {
                  $dialogContent.dialog("close");
               }
            }
         }).show();

         $dialogContent.find(".date_holder").text($calendar.weekCalendar("formatDate", calEvent.start));
         setupStartAndEndTimeFields(startField, endField, calEvent, $calendar.weekCalendar("getTimeslotTimes", calEvent.start));

      },
      eventDrop : function(calEvent, $event) {
      },
      eventResize : function(calEvent, $event) {
      },
      eventClick : function(calEvent, $event) {

         if (calEvent.readOnly) {
            return;
         }

         var $dialogContent = $("#event_edit_container");
         resetForm($dialogContent);
         var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
         var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
         var titleField = $dialogContent.find("input[name='title']").val(calEvent.title);
         var bodyField = $dialogContent.find("textarea[name='body']");
         bodyField.val(calEvent.body);

         $("#filedownload").empty();
         $("#fileup").empty();
         var $filedown=$("#filedownload");
         
         var down="<label for=\"ResultFile\">Result: </label><a href=Download?filename="+calEvent.id+">ResultFile</a>";
         $(down).appendTo($filedown);
         
         $dialogContent.dialog({
            modal: true,
            title: "Info - " + calEvent.title,
            close: function() {
               $dialogContent.dialog("destroy");
               $dialogContent.hide();
               $('#calendar').weekCalendar("removeUnsavedEvents");
            },
            buttons: {
//               save : function() {
//
//                  calEvent.start = new Date(startField.val());
//                  calEvent.end = new Date(endField.val());
//                  calEvent.title = titleField.val();
//                  calEvent.body = bodyField.val();
//
//                  $calendar.weekCalendar("updateEvent", calEvent);
//                  $dialogContent.dialog("close");
//               },
//               "delete" : function() {
//                  $calendar.weekCalendar("removeEvent", calEvent.id);
//                  $dialogContent.dialog("close");
//               },
//               cancel : function() {
//                  $dialogContent.dialog("close");
//               }
            }
         }).show();

         var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
         var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
         $dialogContent.find(".date_holder").text($calendar.weekCalendar("formatDate", calEvent.start));
         setupStartAndEndTimeFields(startField, endField, calEvent, $calendar.weekCalendar("getTimeslotTimes", calEvent.start));
         $(window).resize().resize(); //fixes a bug in modal overlay size ??

      },
      eventMouseover : function(calEvent, $event) {
      },
      eventMouseout : function(calEvent, $event) {
      },
      noEvents : function() {

      },
      data : function(start, end, callback) {
         //callback(getEventData());
    	  $.ajax
          ({
          type: "POST",
          url: "ShowSchedule",
          dataType: "JSON",
          success: function(data)
          {
        	  var year = new Date().getFullYear();
              var month = new Date().getMonth();
              var day = new Date().getDate();
        	  var result =[];      	  
        	  //result=eval('('+result+')');
        	  var jsondata =  eval('('+ data +')');
        	  $.each(jsondata, function(i,item){
        		  var d= new Object();
        		  //item.start=new Date("2013", "11", "06", "9");
        		  //item.end=new Date("2013", "11", "06", "9", "15");
                d.id=item.id;
                d.start= new Date(item.startyear,item.startmonth,item.startday,item.starthour,item.startmin);
                d.end = new Date(item.endyear,item.endmonth,item.endday,item.endhour,item.endmin);
                d.title=item.title;
                d.body=item.body;
                d.readOnly=false;
                  result.push(d);
        		  //alert(item.start);
        	  });
        	  //alert(data);
          //return data;
        	  callback(result);
          }
          
          
          });
      }
   });

   function resetForm($dialogContent) {
      $dialogContent.find("input").val("");
      $dialogContent.find("textarea").val("");
   }

   function getEventData() {
      var year = new Date().getFullYear();
      var month = new Date().getMonth();
      var day = new Date().getDate();
      var ch="[{"+"\"id\":1,\"start\":"+new Date(year,month,day,12)+","+"\"end\":"
                     +new Date(year,month,day,13)+","+"\"title\":\"ddd\""+"}]";
      //$.getJSON("ShowSchedule",{},function (data){alert(data);return data;});
      
      $.ajax
      ({
      type: "POST",
      url: "ShowSchedule",
      dataType: "JSON",
      success: function(data)
      {
    	  var result =[];
    	  var d= new Object();
                  d.id=1;
                  d.start= new Date(year, month, day, 12);
                  d.end = new Date(year, month, day, 13, 30);
                  d.title="Lunch with Mike";
                  d.body="dddd";
                  d.readOnly=false;
    	  result.push(d);
    	  //result=eval('('+result+')');
    	  var jsondata =  eval('('+ data +')');
    	  $.each(jsondata, function(i,item){
    		  item.start=new Date(year, month, day+i, 9);
    		  item.end=new Date(year, month, day+i, 9, 15);
    		  //alert(item.start);
    	  });
    	  //alert(data);
      //return data;
      }
      
      
      });
      return {
    	  
    	  //events: eval('('+ data +')')
    	  events : [{"id":1,"start": new Date(year, month, day, 12),"end": new Date(year, month, day, 13, 30),"title":"Lunch with Mike"}]
    	  };
//      return {
//         events : [
//            {
//               "id":1,
//               "start": new Date(year, month, day, 12),
//               "end": new Date(year, month, day, 13, 30),
//               "title":"Lunch with Mike"
//            },
//            {
//               "id":2,
//               "start": new Date(year, month, day, 14),
//               "end": new Date(year, month, day, 14, 45),
//               "title":"Dev Meeting"
//            },
//            {
//               "id":3,
//               "start": new Date(year, month, day + 1, 17),
//               "end": new Date(year, month, day + 1, 17, 45),
//               "title":"Hair cut"
//            },
//            {
//               "id":4,
//               "start": new Date(year, month, day - 1, 8),
//               "end": new Date(year, month, day - 1, 9, 30),
//               "title":"Team breakfast"
//            },
//            {
//               "id":5,
//               "start": new Date(year, month, day + 1, 14),
//               "end": new Date(year, month, day + 1, 15),
//               "title":"Product showcase"
//            },
//            {
//               "id":6,
//               "start": new Date(year, month, day, 10),
//               "end": new Date(year, month, day, 11),
//               "title":"I'm read-only",
//               readOnly : true
//            },
//            {   "id":100,
//            	"start": new Date(2013, 11, 4, 10),
//                "end": new Date(2013, 11, 4, 11),
//                "title":"GGGGG",
//            }
//         ]     
//      };
   }


   /*
    * Sets up the start and end time fields in the calendar event
    * form for editing based on the calendar event being edited
    */
   function setupStartAndEndTimeFields($startTimeField, $endTimeField, calEvent, timeslotTimes) {

	  $startTimeField.empty();
	  $endTimeField.empty();
      for (var i = 0; i < timeslotTimes.length; i++) {
         var startTime = timeslotTimes[i].start;
         var endTime = timeslotTimes[i].end;
         var startSelected = "";
         if (startTime.getTime() === calEvent.start.getTime()) {
            startSelected = "selected=\"selected\"";
         }
         var endSelected = "";
         if (endTime.getTime() === calEvent.end.getTime()) {
            endSelected = "selected=\"selected\"";
         }
         $startTimeField.append("<option value=\"" + startTime + "\" " + startSelected + ">" + timeslotTimes[i].startFormatted + "</option>");
         $endTimeField.append("<option value=\"" + endTime + "\" " + endSelected + ">" + timeslotTimes[i].endFormatted + "</option>");

      }
      $endTimeOptions = $endTimeField.find("option");
      $startTimeField.trigger("change");
   }

   var $endTimeField = $("select[name='end']");
   var $endTimeOptions = $endTimeField.find("option");

   //reduces the end time options to be only after the start time options.
   $("select[name='start']").change(function() {
      var startTime = $(this).find(":selected").val();
      var currentEndTime = $endTimeField.find("option:selected").val();
      $endTimeField.html(
            $endTimeOptions.filter(function() {
               return startTime < $(this).val();
            })
            );

      var endTimeSelected = false;
      $endTimeField.find("option").each(function() {
         if ($(this).val() === currentEndTime) {
            $(this).attr("selected", "selected");
            endTimeSelected = true;
            return false;
         }
      });

      if (!endTimeSelected) {
         //automatically select an end date 2 slots away.
         $endTimeField.find("option:eq(1)").attr("selected", "selected");
      }

   });


   var $about = $("#about");

   $("#about_button").click(function() {
      $about.dialog({
         title: "About this calendar demo",
         width: 600,
         close: function() {
            $about.dialog("destroy");
            $about.hide();
         },
         buttons: {
            close : function() {
               $about.dialog("close");
            }
         }
      }).show();
   });


});