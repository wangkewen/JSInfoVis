var labelType, useGradients, nativeTextSupport, animate,countclick=0;

(function() {
  var ua = navigator.userAgent,
      iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
      typeOfCanvas = typeof HTMLCanvasElement,
      nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
      textSupport = nativeCanvasSupport 
        && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
  //I'm setting this based on the fact that ExCanvas provides text support for IE
  //and that as of today iPhone/iPad current text support is lame
  labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
  nativeTextSupport = labelType == 'Native';
  useGradients = nativeCanvasSupport;
  animate = !(iStuff || !nativeCanvasSupport);
})();

var Log = {
  elem: false,
  write: function(text){
    if (!this.elem) 
      this.elem = document.getElementById('log');
    this.elem.innerHTML = text;
    this.elem.style.left = (500 - this.elem.offsetWidth / 2) + 'px';
  }
};

function init(scale){
  var date=new Date();
  var begin,end;
 
  $.ajax({
	 type:"POST",
	 url:"InfoDisplay?scale="+scale,
	 dataType:"JSON",
	 success: function(data){
	     if(scale!="null")
	    	 $("#infovis").empty();
  // init data
  var json = data;
  // end
  // init ForceDirected
  var fd = new $jit.ForceDirected({
    //id of the visualization container
    injectInto: 'infovis',
    //Enable zooming and panning
    //by scrolling and DnD
   
    Navigation: {
      enable: true,
      //Enable panning events only if we're dragging the empty
      //canvas (and not a node).
      panning: 'avoid nodes',
      zooming: 10 //zoom speed. higher is more sensible
    },
    // Change node and edge styles such as
    // color and width.
    // These properties are also set per node
    // with dollar prefixed data-properties in the
    // JSON structure.
    Node: {
      overridable: true
    },
    Edge: {
      overridable: true,
      color: '#23A4FF',
      lineWidth: 0.4
    },
    //Native canvas text styling
    Label: {
      type: labelType, //Native or HTML
      size: 10,
      style: 'bold'
    },
    //Add Tips
    Tips: {
      enable: true,
      onShow: function(tip, node) {
        //count connections
        var count = 0;
        node.eachAdjacency(function() { count++; });
        //display node info in tooltip
        tip.innerHTML = "<div class=\"tip-title\">" + node.name + "</div>"
          + "<div class=\"tip-text\"><b>connections:</b> " + count + "</div>";
      }
    },
    // Add node events
    Events: {
      enable: true,
      type: 'Native',
      //Change cursor style when hovering a node
      onMouseEnter: function() {
        fd.canvas.getElement().style.cursor = 'move';
      },
      onMouseLeave: function() {
        fd.canvas.getElement().style.cursor = '';
      },
      //Update node positions when dragged
      onDragMove: function(node, eventInfo, e) {
          var pos = eventInfo.getPos();
          node.pos.setc(pos.x, pos.y);
          fd.plot();
      },
      //Implement the same handler for touchscreens
      onTouchMove: function(node, eventInfo, e) {
        $jit.util.event.stop(e); //stop default touchmove event
        this.onDragMove(node, eventInfo, e);
      },
      //Add also a click handler to nodes
      onClick: function(node) {
        if(!node) return;
        // Build the right column relations list.
        // This is done by traversing the clicked node connections.
        var html = "<h4>" + node.name + "</h4><b> connections:</b><ul><li>",
            list = [];
        node.eachAdjacency(function(adj){
          list.push(adj.nodeTo.name);
        });
        //alert(node.name);
        countclick++;
        window.open ("GetJavafiles?node="+node.name, "newwindow"+countclick, "height=500, width=800, " +
        		"top=300, left=300, toolbar=no, menubar=yes, scrollbars=yes, resizable=no,location=no, status=no");
        return;
        //append connections information
        $jit.id('inner-details').innerHTML = html + list.join("</li><li>") + "</li></ul>";
       
      }
    },
    //Number of iterations for the FD algorithm
    iterations: 200,
    //Edge length
    levelDistance: 130,
    // Add text to the labels. This method is only triggered
    // on label creation and only for DOM labels (not native canvas ones).
    onCreateLabel: function(domElement, node){
      domElement.innerHTML = node.name;
      var style = domElement.style;
      style.fontSize = "0.8em";
      style.color = "#ddd";
    },
    // Change node styles when DOM labels are placed
    // or moved.
    onPlaceLabel: function(domElement, node){
      var style = domElement.style;
      var left = parseInt(style.left);
      var top = parseInt(style.top);
      var w = domElement.offsetWidth;
//      style.left = (left - w / 2) + 'px';
//      style.top = (top + 10) + 'px';
//      style.display = '';
      style.left = (left - w / 2) + 'px';
      style.top = (top + 10) + 'px';
      style.display = '';
    }
  });
  
  // load JSON data.
  fd.loadJSON(json);
  // compute positions incrementally and animate.
  fd.computeIncremental({
    iter: 40,
    property: 'end',
    onStep: function(perc){
      Log.write(perc + '% loaded...');
    },
    onComplete: function(){
      Log.write('done');
      fd.animate({
        modes: ['linear'],
        transition: $jit.Trans.Elastic.easeOut,
        duration: 2500
      });
    }
    
  });
	 },
  // end
  error: function(){
 	 alert("error");
  }
  });
}

var groupids="";
function display(scale){
	
	//alert(scale);
	if(scale=="null"||scale==null)
	groupids+=scale;
	else groupids="null_"+scale;
	  $.ajax({
		 type:"POST",
		 url:"InfoDisplay?scale="+groupids,
		 dataType:"JSON",
		 success: function(data){
		     if(scale!="null")
		    	 $("#infovis").empty();
	  // init data
	  //alert(groupids);
	  var json = data;
	  // end
	  // init ForceDirected
	  var fd = new $jit.ForceDirected({
	    //id of the visualization container
	    injectInto: 'infovis',
	    //Enable zooming and panning
	    //by scrolling and DnD
	   
	    Navigation: {
	      enable: true,
	      //Enable panning events only if we're dragging the empty
	      //canvas (and not a node).
	      panning: 'avoid nodes',
	      zooming: 10 //zoom speed. higher is more sensible
	    },
	    // Change node and edge styles such as
	    // color and width.
	    // These properties are also set per node
	    // with dollar prefixed data-properties in the
	    // JSON structure.
	    Node: {
	      overridable: true
	    },
	    Edge: {
	      overridable: true,
	      color: '#ddd',
	      lineWidth: 0.2
	    },
	    //Native canvas text styling
	    Label: {
	      type: labelType, //Native or HTML
	      size: 10,
	      style: 'bold'
	    },
	    //Add Tips
	    Tips: {
	      enable: true,
	      onShow: function(tip, node) {
	        //count connections
	        var count = 0;
	        var info=(node.name).split("_");
	        node.eachAdjacency(function() { count++; });
	        //display node info in tooltip
	        if(info[0].match("^[0-9]*$"))
	        tip.innerHTML = "<div class=\"tip-title\">Group NO. " + info[0] + "</div>"
	        + "<div class=\"tip-text\"> Contain " + info[1] + "</div>";
	        
	        else
	        //display node info in tooltip
	        tip.innerHTML = "<div class=\"tip-title\">" + node.name + "</div>"
	          + "<div class=\"tip-text\"><b>connections:</b> " + count + "</div>";
	      }
	    },
	    // Add node events
	    Events: {
	      enable: true,
	      type: 'Native',
	      //Change cursor style when hovering a node
	      onMouseEnter: function() {
	        fd.canvas.getElement().style.cursor = 'move';
	      },
	      onMouseLeave: function() {
	        fd.canvas.getElement().style.cursor = '';
	      },
	      //Update node positions when dragged
	      onDragMove: function(node, eventInfo, e) {
	          var pos = eventInfo.getPos();
	          node.pos.setc(pos.x, pos.y);
	          fd.plot();
	      },
	      //Implement the same handler for touchscreens
	      onTouchMove: function(node, eventInfo, e) {
	        $jit.util.event.stop(e); //stop default touchmove event
	        this.onDragMove(node, eventInfo, e);
	      },
	      //Add also a click handler to nodes
	      onClick: function(node) {
	    	  var i=0;
	        if(!node) return;
	        // Build the right column relations list.
	        // This is done by traversing the clicked node connections.
	        var html = "<h4>" + node.name + "</h4><b> connections:</b><ul><li>",
	            list = [];
	        node.eachAdjacency(function(adj){
	          list.push(adj.nodeTo.name);
	        });
	        //alert(node.name);
	       countclick++;
	       var gid=node.name.split("_")[0];
	        //alert(gid);
	        //$("#infovis").empty();
	       //alert(node.name);
	        if(gid.match("^[0-9]*$"))
	        display(gid);
	        else
	        	{window.open ("GetJavafiles?node="+node.name, "newwindow"+countclick, "height=500, width=800, " +
        		"top=300, left=300, toolbar=no, menubar=yes, scrollbars=yes, resizable=no,location=no, status=no");
	        	return;}
	        //append connections information
	        $jit.id('inner-details').innerHTML = html + list.join("</li><li>") + "</li></ul>";
	      }
	    },
	    //Number of iterations for the FD algorithm
	    iterations: 200,
	    //Edge length
	    levelDistance: 130,
	    // Add text to the labels. This method is only triggered
	    // on label creation and only for DOM labels (not native canvas ones).
	    onCreateLabel: function(domElement, node){
	      domElement.innerHTML = node.name;
	      var style = domElement.style;
	      style.fontSize = "0.8em";
	      style.color = "#ddd";
	    },
	    // Change node styles when DOM labels are placed
	    // or moved.
	    onPlaceLabel: function(domElement, node){
	      var style = domElement.style;
	      var left = parseInt(style.left);
	      var top = parseInt(style.top);
	      var w = domElement.offsetWidth;
//	      style.left = (left - w / 2) + 'px';
//	      style.top = (top + 10) + 'px';
//	      style.display = '';
	      style.left = (left - w / 2) + 'px';
	      style.top = (top + 10) + 'px';
	      style.display = '';
	    }
	  });
	  // load JSON data.
	  fd.loadJSON(json);
	  // compute positions incrementally and animate.
	  fd.computeIncremental({
	    iter: 40,
	    property: 'end',
	    onStep: function(perc){
	      Log.write(perc + '% loaded...');
	    },
	    onComplete: function(){
	      Log.write('done');
	      fd.animate({
	        modes: ['linear'],
	        transition: $jit.Trans.Elastic.easeOut,
	        duration: 2500
	      });
	    }
	  });
		 },
	  // end
	  error: function(){
	 	 alert("error");
	  }
	  });
	}

function igraph(scale){
	  $.ajax({
		 type:"POST",
		 url:"InfoDisplay?scale="+"ISO_"+scale,
		 dataType:"JSON",
		 success: function(data){
		     if(scale!="null")
		    	 $("#infovis").empty();
	  // init data
	  var json = data;
	  
	  
	  // end
	  // init ForceDirected
	  var fd = new $jit.ForceDirected({
	    //id of the visualization container
	    injectInto: 'infovis',
	    //Enable zooming and panning
	    //by scrolling and DnD
	   
	    Navigation: {
	      enable: true,
	      //Enable panning events only if we're dragging the empty
	      //canvas (and not a node).
	      panning: 'avoid nodes',
	      zooming: 10 //zoom speed. higher is more sensible
	    },
	    // Change node and edge styles such as
	    // color and width.
	    // These properties are also set per node
	    // with dollar prefixed data-properties in the
	    // JSON structure.
	    Node: {
	      overridable: true
	    },
	    Edge: {
	      overridable: true,
	      color: '#23A4FF',
	      lineWidth: 0.4
	    },
	    //Native canvas text styling
	    Label: {
	      type: labelType, //Native or HTML
	      size: 10,
	      style: 'bold'
	    },
	    //Add Tips
	    Tips: {
	      enable: true,
	      onShow: function(tip, node) {
	        //count connections
	        var count = 0;
	        node.eachAdjacency(function() { count++; });
	        //display node info in tooltip
	        tip.innerHTML = "<div class=\"tip-title\">" + node.name + "</div>"
	          + "<div class=\"tip-text\"><b>connections:</b> " + count + "</div>";
	      }
	    },
	    // Add node events
	    Events: {
	      enable: true,
	      type: 'Native',
	      //Change cursor style when hovering a node
	      onMouseEnter: function() {
	        fd.canvas.getElement().style.cursor = 'move';
	      },
	      onMouseLeave: function() {
	        fd.canvas.getElement().style.cursor = '';
	      },
	      //Update node positions when dragged
	      onDragMove: function(node, eventInfo, e) {
	          var pos = eventInfo.getPos();
	          node.pos.setc(pos.x, pos.y);
	          fd.plot();
	      },
	      //Implement the same handler for touchscreens
	      onTouchMove: function(node, eventInfo, e) {
	        $jit.util.event.stop(e); //stop default touchmove event
	        this.onDragMove(node, eventInfo, e);
	      },
	      //Add also a click handler to nodes
	      onClick: function(node) {
	        if(!node) return;
	        // Build the right column relations list.
	        // This is done by traversing the clicked node connections.
	        var html = "<h4>" + node.name + "</h4><b> connections:</b><ul><li>",
	            list = [];
	        node.eachAdjacency(function(adj){
	          list.push(adj.nodeTo.name);
	        });
	        //alert(node.name);
	        countclick++;
	        window.open ("GetJavafiles?node="+node.name, "newwindow"+countclick, "height=500, width=800, " +
	        		"top=300, left=300, toolbar=no, menubar=yes, scrollbars=yes, resizable=no,location=no, status=no");
	        return;
	        //append connections information
	        $jit.id('inner-details').innerHTML = html + list.join("</li><li>") + "</li></ul>";
	      }
	    },
	    //Number of iterations for the FD algorithm
	    iterations: 200,
	    //Edge length
	    levelDistance: 130,
	    // Add text to the labels. This method is only triggered
	    // on label creation and only for DOM labels (not native canvas ones).
	    onCreateLabel: function(domElement, node){
	      domElement.innerHTML = node.name;
	      var style = domElement.style;
	      style.fontSize = "0.8em";
	      style.color = "#ddd";
	    },
	    // Change node styles when DOM labels are placed
	    // or moved.
	    onPlaceLabel: function(domElement, node){
	      var style = domElement.style;
	      var left = parseInt(style.left);
	      var top = parseInt(style.top);
	      var w = domElement.offsetWidth;
//	      style.left = (left - w / 2) + 'px';
//	      style.top = (top + 10) + 'px';
//	      style.display = '';
	      style.left = (left - w / 2) + 'px';
	      style.top = (top + 10) + 'px';
	      style.display = '';
	    }
	  });
	  // load JSON data.
	  fd.loadJSON(json);
	  // compute positions incrementally and animate.
	  fd.computeIncremental({
	    iter: 40,
	    property: 'end',
	    onStep: function(perc){
	      Log.write(perc + '% loaded...');
	    },
	    onComplete: function(){
	      Log.write('done');
	      fd.animate({
	        modes: ['linear'],
	        transition: $jit.Trans.Elastic.easeOut,
	        duration: 2500
	      });
	    }
	  });
		 },
	  // end
	  error: function(){
	 	 alert("error");
	  }
	  });
	}


function callgraph(scale){
	  var date=new Date();
	  var begin,end;
	 
	  $.ajax({
		 type:"POST",
		 url:"InfoDisplay?scale=CALLGRAPH_"+scale,
		 dataType:"JSON",
		 success: function(data){
		     if(scale!="null")
		    	 $("#infovis").empty();
	  // init data
	  var json = data;
	  // end
	  // init ForceDirected
	  var fd = new $jit.ForceDirected({
	    //id of the visualization container
	    injectInto: 'infovis',
	    //Enable zooming and panning
	    //by scrolling and DnD
	   
	    Navigation: {
	      enable: true,
	      //Enable panning events only if we're dragging the empty
	      //canvas (and not a node).
	      panning: 'avoid nodes',
	      zooming: 10 //zoom speed. higher is more sensible
	    },
	    // Change node and edge styles such as
	    // color and width.
	    // These properties are also set per node
	    // with dollar prefixed data-properties in the
	    // JSON structure.
	    Node: {
	      overridable: true
	    },
	    Edge: {
	      overridable: true,
	      type: 'arrow',
	      color: '#23A4FF',
	      lineWidth: 0.4
	    },
	    //Native canvas text styling
	    Label: {
	      type: labelType, //Native or HTML
	      size: 10,
	      style: 'bold'
	    },
	    //Add Tips
	    Tips: {
	      enable: true,
	      onShow: function(tip, node) {
	        //count connections
	        var count = 0;
	        node.eachAdjacency(function() { count++; });
	        //display node info in tooltip
	        tip.innerHTML = "<div class=\"tip-title\">" + node.name + "</div>"
	          + "<div class=\"tip-text\"><b>connections:</b> " + count + "</div>";
	      }
	    },
	    // Add node events
	    Events: {
	      enable: true,
	      type: 'Native',
	      //Change cursor style when hovering a node
	      onMouseEnter: function() {
	        fd.canvas.getElement().style.cursor = 'move';
	      },
	      onMouseLeave: function() {
	        fd.canvas.getElement().style.cursor = '';
	      },
	      //Update node positions when dragged
	      onDragMove: function(node, eventInfo, e) {
	          var pos = eventInfo.getPos();
	          node.pos.setc(pos.x, pos.y);
	          fd.plot();
	      },
	      //Implement the same handler for touchscreens
	      onTouchMove: function(node, eventInfo, e) {
	        $jit.util.event.stop(e); //stop default touchmove event
	        this.onDragMove(node, eventInfo, e);
	      },
	      //Add also a click handler to nodes
	      onClick: function(node) {
	        if(!node) return;
	        // Build the right column relations list.
	        // This is done by traversing the clicked node connections.
	        var html = "<h4>" + node.name + "</h4><b> connections:</b><ul><li>",
	            list = [];
	        node.eachAdjacency(function(adj){
	          list.push(adj.nodeTo.name);
	        });
	        //alert(node.name);
	        countclick++;
	        if(!(!node.id.contains(".java")&&node.id.contains("_")))
	        window.open ("GetCodefile?filename="+node.id, "newwindow"+countclick, "height=500, width=1000, " +
	        		"top=300, left=300, toolbar=no, menubar=yes, scrollbars=yes, resizable=no,location=no, status=no");
	        else
	        	window.open ("GetJavafiles?node="+node.name, "newwindow"+countclick, "height=500, width=1000, " +
        		"top=300, left=300, toolbar=no, menubar=yes, scrollbars=yes, resizable=no,location=no, status=no");
        	        
	        
	        return;
	        //append connections information
	        $jit.id('inner-details').innerHTML = html + list.join("</li><li>") + "</li></ul>";
	       
	      }
	    },
	    //Number of iterations for the FD algorithm
	    iterations: 200,
	    //Edge length
	    levelDistance: 130,
	    // Add text to the labels. This method is only triggered
	    // on label creation and only for DOM labels (not native canvas ones).
	    onCreateLabel: function(domElement, node){
	      domElement.innerHTML = node.name;
	      var style = domElement.style;
	      style.fontSize = "0.8em";
	      style.color = "#ddd";
	    },
	    // Change node styles when DOM labels are placed
	    // or moved.
	    onPlaceLabel: function(domElement, node){
	      var style = domElement.style;
	      var left = parseInt(style.left);
	      var top = parseInt(style.top);
	      var w = domElement.offsetWidth;
//	      style.left = (left - w / 2) + 'px';
//	      style.top = (top + 10) + 'px';
//	      style.display = '';
	      style.left = (left - w / 2) + 'px';
	      style.top = (top + 10) + 'px';
	      style.display = '';
	    }
	  });
	  
	  // load JSON data.
	  fd.loadJSON(json);
	  // compute positions incrementally and animate.
	  fd.computeIncremental({
	    iter: 40,
	    property: 'end',
	    onStep: function(perc){
	      Log.write(perc + '% loaded...');
	    },
	    onComplete: function(){
	      Log.write('done');
	      fd.animate({
	        modes: ['linear'],
	        transition: $jit.Trans.Elastic.easeOut,
	        duration: 2500
	      });
	    }
	    
	  });
		 },
	  // end
	  error: function(){
	 	 alert("error");
	  }
	  });
	}


function spaceTreeInit(isload){
	if(isload==true) {loadfolder();} //show.js 
    //init data
	$.ajax({
		type: "POST",
		url: "FileInfoDisplay",
		dataType: "JSON",
		success:function(data){
	
	var json=data; 
    //end
    //init Spacetree
    //Create a new ST instance
    var st = new $jit.ST({
        //id of viz container element
        injectInto: 'infovis',
        //set duration for the animation
        duration: 800,
        //set animation transition type
        transition: $jit.Trans.Quart.easeInOut,
        //set distance between node and its children
        levelDistance: 100,
        //enable panning
        Navigation: {
          enable:true,
          panning:true,
          zooming: 10 //zoom speed. higher is more sensible
        },
        //set node and edge styles
        //set overridable=true for styling individual
        //nodes or edges
        Node: {
            height: 20,
            width: 60,
            type: 'rectangle',
            color: '#caf',
            overridable: true
        },
        
        Edge: {
            type: 'bezier',
            overridable: true
        },
        
        onBeforeCompute: function(node){
        	
            Log.write("loading " + node.name);
        },
        
        onAfterCompute: function(){
            Log.write("done");
            
        },
        
        //This method is called on DOM label creation.
        //Use this method to add event handlers and styles to
        //your node.
        onCreateLabel: function(label, node){
            label.id = node.id;            
            label.innerHTML = node.name;
            label.onclick = function(){
            	//if(normal.checked) {
            	  st.onClick(node.id);
            	//} else {
                //st.setRoot(node.id, 'animate');
            	//}
            };
            //alert(node.name.length);
            //set label styles
            var style = label.style;
            
           
            style.width = 60 + 'px';
            style.height = 17 + 'px';            
            style.cursor = 'pointer';
            style.color = '#333';
            style.fontSize = '0.7em';
            style.textAlign= 'center';
            style.paddingTop = '3px';
            
           if(node.name.contains(".java")&&node.name.length>20)  
        	   if(node.name.length<33) style.fontSize = '0.55em';
        	   else if (node.name.length<35) style.fontSize = '0.52em';
        	   else if (node.name.length<40) style.fontSize = '0.5em';
        	   else style.fontSize = '0.45em';
        },
        
        //This method is called right before plotting
        //a node. It's useful for changing an individual node
        //style properties before plotting it.
        //The data properties prefixed with a dollar
        //sign will override the global node style properties.
        onBeforePlotNode: function(node){
            //add some color to the nodes in the path between the
            //root node and the selected node.
            if (node.selected) {
                node.data.$color = "#ff7";
            }
            else {
                delete node.data.$color;
                //if the node belongs to the last plotted level
                if(!node.anySubnode("exist")) {
                    //count children number
                    var count = 0;
                    node.eachSubnode(function(n) { count++; });
                    //alert(count);
                    //assign a node color based on
                    //how many children it has
                    node.data.$color = ['#f80', '#daa', '#eaa', '#faa',
                                        '#bac', '#bad', '#bae', '#baf', 
                                        '#cab', '#cac', '#cad', '#cae', '#caf',
                                        '#dab', '#dac', '#dad', '#dae', '#daf',
                                        '#caa', '#daa', '#eaa', '#faa',
                                        '#bac', '#bad', '#bae', '#baf', 
                                        '#cab', '#cac', '#cad', '#cae', '#caf',
                                        '#dab', '#dac', '#dad', '#dae', '#daf',][count];                    
                }
            }
        },
        
        //This method is called right before plotting
        //an edge. It's useful for changing an individual edge
        //style properties before plotting it.
        //Edge data proprties prefixed with a dollar sign will
        //override the Edge global style properties.
        onBeforePlotLine: function(adj){
            if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                adj.data.$color = "#f09";
                adj.data.$lineWidth = 3;
            }
            else {
                delete adj.data.$color;
                delete adj.data.$lineWidth;
            }
        }
    });
    //load json data
    st.loadJSON(json);
    //compute node positions and layout
    st.compute();
    //optional: make a translation of the tree
    st.geom.translate(new $jit.Complex(-200, 0), "current");
    //emulate a click on the root node.
    st.onClick(st.root);
    //end
    //Add event handlers to switch spacetree orientation.
    var top = $jit.id('r-top'), 
        left = $jit.id('r-left'), 
        bottom = $jit.id('r-bottom'), 
        right = $jit.id('r-right'),
        normal = $jit.id('s-normal');
        
    
    function changeHandler() {
        if(this.checked) {
            top.disabled = bottom.disabled = right.disabled = left.disabled = true;
            st.switchPosition(this.value, "animate", {
                onComplete: function(){
                    top.disabled = bottom.disabled = right.disabled = left.disabled = false;
                }
            });
        }
    };
    
    top.onchange = left.onchange = bottom.onchange = right.onchange = changeHandler;
    //end
    
    
		},
		error: function(){
			alert("ERROR...");
		}
	});
}
