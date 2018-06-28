function drawGraphIdee(searchMed, filename) {
	
	$.getJSON(filename, function(data) {

		var filteredMedikamente = [];
		var filteredNebenwirkungen = [];
		var filteredEdges = [];
		var searchMedId = null;
		var yM = 2;
		var yN = 0;

		var nodes = data["nodes"]?data["nodes"]:[];
		var edges = data["edges"]?data["edges"]:[];
		var medikamente = [];
		var nebenwirkungen = [];
		
		for (var i = 0; i < nodes.length; i++) {
			var elem = nodes[i];
			if (elem.art == "medikament") {
				medikamente.push(elem);
			} else {
				nebenwirkungen.push(elem);
			}
		}
		if(searchMed != ""){	

			for (var i = 0; i < medikamente.length; i++) {
				var elem = medikamente[i];
				if (elem.name == searchMed) {
					elem.y = yM;
					filteredMedikamente.push(elem);
					yM++;
					searchMedId = elem.id;
				}
			}
			var nodesToAdd = [];
			for (var i = 0; i < edges.length; i++) {
				var edge = edges[i];
				if (edge.source == searchMedId) {
					filteredEdges.push(edge);
					nodesToAdd.push(edge.target);
				} else if(edge.target == searchMedId) {
					filteredEdges.push(edge);
					nodesToAdd.push(edge.source);
				}
			}
			for (var i = 0; i < nebenwirkungen.length; i++) {
				var elem = nebenwirkungen[i];
				if (nodesToAdd.indexOf(elem.id) != -1) {
					elem.y = yN;;
					yN++;
					filteredNebenwirkungen.push(elem);
				}
			}
		} else {
			for (var i = 0; i < medikamente.length; i++) {
				medikamente[i].y = yM;
				yM++;
			}
			for (var i = 0; i < nebenwirkungen.length; i++) {
				nebenwirkungen[i].y = yN;
				yN++;
			}
			filteredEdges = edges;
			filteredMedikamente = medikamente;
			filteredNebenwirkungen = nebenwirkungen;
		}

		var filteredNodes = filteredMedikamente.concat(filteredNebenwirkungen);
		var links = [];
		for (var i = 0; i < filteredEdges.length; i++) {
			var link = {};
			link.weight = filteredEdges[i].weight;
			for (var j = 0; j < filteredNodes.length; j++) {
				if(filteredEdges[i].source == filteredNodes[j].id) {
					link.source = j;
				}
				if(filteredEdges[i].target == filteredNodes[j].id) {
					link.target = j;
				}
			}
			links.push(link);
		}
		console.log(filteredMedikamente);
		console.log(filteredNebenwirkungen);
		console.log(filteredEdges);
		console.log(links);

//		************** Generate the tree diagram *****************
		var margin = {
				top : 20,
				right : 20,
				bottom : 30,
				left : 40
		};
		width = 700 - margin.left - margin.right, 
		height = 500 - margin.top
		- margin.bottom;

		var svg = d3.select("#graph").append("svg").attr("width",
				width + margin.left + margin.right).attr("height",
						height + margin.top + margin.bottom);

		/*
		 * svg.selectAll("circle").data(bodyparts).enter().append("circle").attr("cx",
		 * function(d) { return d.x; }).attr("cy", function(d) { return d.y;
		 * }).attr("r", 5).style('fill', 'blue');
		 */

		var g = svg.selectAll("g")
		.data(filteredNodes)
		.enter().append("g");

		var maxVals = drawNodes(g, svg, filteredNodes, "g");

		var paddingLeftRight = 18;
		var paddingTopBottom = 5;
		var colors = ["#ffcc00", "#ffa31a", "#ff6600", "#ff4d4d"];

		var links = svg.selectAll("line").data(links)
		.enter().append("line")
		.style('stroke', function(d) {return colors[d.weight-1];})
		.style('stroke-width', function(d) { return d.weight;})
		.attr("x1", function(d){ return filteredNodes[d.source].bb.x + filteredNodes[d.source].bb.width/2 + maxVals[0]/2 + paddingLeftRight/2; })
		.attr("y1", function(d){ return filteredNodes[d.source].bb.y + maxVals[1]/2; })
		.attr("x2", function(d){ return filteredNodes[d.target].bb.x + filteredNodes[d.target].bb.width/2 - maxVals[0]/2 - paddingLeftRight/2; })
		.attr("y2", function(d){ return filteredNodes[d.target].bb.y + maxVals[1]/2; });

		/* Add event listener to rectangles to build details table */
		$(".med").on("click", function(){
			var id = this.id;
			console.log(this);
			console.log(id);
			addToDetailsTable(id);
		});
	});
}


function drawNodes(selection, svg, dataArr, elemName){
	/* http://bl.ocks.org/andreaskoller/7674031 */
	selection.append("rect"); // placeholder for background rect, position and size will be defined later

	
	svg.selectAll(elemName)
	.append("text") // content of text will be defined later
	.attr("x", function(d) { return 100+(d.art!="medikament") *400})
	.attr("y", function(d) { return 20+d.y*30; })
	.attr("fill", "black")
	.attr("text-anchor", "middle")
	.style("font-size", "18px")
	.style("font-weight","bold")
	.style("pointer-events", "none");

	svg.selectAll(elemName + " text").text(function(d) {
		return d.name;
	});

	svg.selectAll(elemName+" text").each(function(d, i) {
		dataArr[i].bb = this.getBBox(); // get bounding box of text field and
		// store it in texts array
	});

	var maxWidth = 0;
	var maxHeight = 0;
	for (var i = 0; i < dataArr.length; i++) {
		if (dataArr[i].bb.width > maxWidth) {maxWidth = dataArr[i].bb.width;}
		if (dataArr[i].bb.height > maxHeight) {maxHeight = dataArr[i].bb.height;}
	}


	var paddingLeftRight = 18;
	var paddingTopBottom = 5;
	svg.selectAll(elemName+" rect")
	.attr("id", function(d) { return d.id; })
	.attr("class", function(d) { if(d.art=="medikament") return "med"; })
	.attr("x", function(d) { return 100+(d.art!="medikament") *400 - maxWidth/2 - paddingLeftRight/2; })
	.attr("y", function(d) { return 20+30*d.y - maxHeight;  })
	.attr("width", function(d) { return maxWidth + paddingLeftRight; })
	.attr("height", function(d) { return maxHeight + paddingTopBottom; })
	.style("fill", "green")
	.style("fill-opacity", 0.4)

	return [maxWidth, maxHeight];
}

function addToDetailsTable(id){
	$.getJSON("data/beispielDetails.json", function(data) {

		for (var i = 0; i < data.length; i++) {
			var elem = data[i];
			if (elem.id == id) {
				$('#details tbody').append('<tr><td>'+elem.name+'</td><td>'+elem.wirkstoff+'</td><td>'+elem.abgabeformen+'</td><td><button type="submit" class="form-control" action="/search" style="width:100%; text-align:center;">Mehr</button></td></tr>');
				break;
			}
		}
	});
}