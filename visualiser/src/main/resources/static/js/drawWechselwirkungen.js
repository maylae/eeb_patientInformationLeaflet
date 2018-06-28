function drawWechselwirkungen(searchMed) {
	$.getJSON("data/beispielWechselwirkungen.json", function(data) {

		var filteredMedikamente = [];
		var filteredEdges = [];
		var searchMedPos = null;
		var yM = 2;

		var nodes = data["nodes"];
		var links = data["links"];

		if(searchMed != ""){	

			for (var i = 0; i < nodes.length; i++) {
				var elem = nodes[i];
				if (elem.name.toLowerCase() == searchMed.toLowerCase()) {
					elem.y = yM;
					filteredMedikamente.push(elem);
					yM++;
					searchMedPos = i;
					break;
				}
			}
			var nodesToAdd = [];
			for (var i = 0; i < links.length; i++) {
				var edge = links[i];
				if (edge.source == searchMedPos) {
					edge.source = 0;
					if(filteredMedikamente.indexOf(nodes[edge.target]) == -1) {
						filteredMedikamente.push(nodes[edge.target]);
						edge.target = filteredMedikamente.length - 1;
					} else {
						edge.target = filteredMedikamente.indexOf(nodes[edge.target]);
					}
					filteredEdges.push(edge);
				} else if(edge.target == searchMedPos) {
					edge.target = 0;
					if(filteredMedikamente.indexOf(nodes[edge.source]) == -1) {
						filteredMedikamente.push(nodes[edge.source]);
						edge.source = filteredMedikamente.length - 1;
					} else {
						edge.source = filteredMedikamente.indexOf(nodes[edge.source]);
					}
					filteredEdges.push(edge);
				}
			}
		} else {
			filteredMedikamente = nodes;
			filteredEdges = links;
		}
		
		nodes = filteredMedikamente;
		links = filteredEdges;
		console.log(filteredMedikamente);
		console.log(filteredEdges);

//		var links = [];
//		for (var i = 0; i < filteredEdges.length; i++) {
//			var link = {};
//			link.weight = filteredEdges[i].data.weight;
//			for (var j = 0; j < filteredMedikamente.length; j++) {
//				if(filteredEdges[i].data.source == filteredMedikamente[j].data.id) {
//					link.source = j;
//				}
//				if(filteredEdges[i].data.target == filteredMedikamente[j].data.id) {
//					link.target = j;
//				}
//			}
//			links.push(link);
//		}


		/*http://bl.ocks.org/jose187/4733747*/
		var svg = d3.select("#svg"),
	    width = +svg.attr("width"),
	    height = +svg.attr("height"),
	    g = svg.append("g").attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

	var simulation = d3.forceSimulation(nodes)
	    .force("charge", d3.forceManyBody().strength(-500))
	    .force("link", d3.forceLink(links).distance(170).strength(2).iterations(10))
	    .force("x", d3.forceX())
	    .force("y", d3.forceY())
	    .stop();

	var loading = svg.append("text")
	    .attr("dy", "0.35em")
	    .attr("text-anchor", "middle")
	    .attr("font-family", "sans-serif")
	    .attr("font-size", 10)
	    .text("Simulating. One moment please…");

	// Use a timeout to allow the rest of the page to load first.
	d3.timeout(function() {
	  loading.remove();

	  // See https://github.com/d3/d3-force/blob/master/README.md#simulation_tick
	  for (var i = 0, n = Math.ceil(Math.log(simulation.alphaMin()) / Math.log(1 - simulation.alphaDecay())); i < n; ++i) {
	    simulation.tick();
	  }

	  g.append("g")
	      .attr("stroke", "#000")
	      .attr("stroke-width", 1.5)
	      .style("z-index", -1)
	    .selectAll("line")
	    .data(links)
	    .enter().append("line")
	      .attr("x1", function(d) { return d.source.x; })
	      .attr("y1", function(d) { return d.source.y; })
	      .attr("x2", function(d) { return d.target.x; })
	      .attr("y2", function(d) { return d.target.y; });

	  g.append("g")
	      .style("z-index", 2)
	    .selectAll("circle")
	    .data(nodes)
	    .enter().append("a")
	    .attr("xlink:href", function(d) { return "/medikament?medikament="+d.name; })
	    .append("circle")
	      .attr("cx", function(d) { return d.x; })
	      .attr("cy", function(d) { return d.y; })
	      .attr("r", 45)
	      .attr("stroke", function(d) { if (["Ibuprofen", "Dolormin", "Aspirin"].indexOf(d.name) != -1) { return "#000"; } else { return "lightgreen";}})
	      .style("fill", "lightgreen")
	      .style("opacity",1);
	  
	  g.append("g")
	  .selectAll("text")
	  .data(nodes)
	  .enter().append("a")
	    .attr("xlink:href", function(d) { return "/medikament?medikament="+d.name; })
	    .append("text") // content of text will be defined later
	  .attr("x", function(d) { return d.x })
	  .attr("y", function(d) { return d.y + 5; })
	  .attr("fill", "black")
	  .attr("text-anchor", "middle")
	  .text(function (d) {return d.name;})
	  .style("font-size", "16px")
	  .style("pointer-events", "none");
	});
	});
}