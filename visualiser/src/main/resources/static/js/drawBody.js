$(document).ready(function(){
var margin = {
		top : 20,
		right : 20,
		bottom : 30,
		left : 40
	};
	width = 400 - margin.left - margin.right, 
	height = 700 - margin.top
			- margin.bottom;

	var bodyparts = [ {
		"name" : "Augen, Ohren & Nase",
		"href": "/search?symptom=Augen+Ohren+Nase",
		"x" : width * 0.42,
		"y" : height * 0.08
	}, {
		"name" : "Haut, Haare & Nägel",
		"href": "/search?symptom=Haut+Haare+Nägel",
		"x" : width * 0.85,
		"y" : height * 0.57
	}, {
		"name" : "Herz",
		"href": "/search?symptom=Herz",
		"x" : width * 0.5,
		"y" : height * 0.26
	}, {
		"name" : "Magen, Darm & Verdauung",
		"href": "/search?symptom=Magen+Darm+Verdauung",
		"x" : width * 0.42,
		"y" : height * 0.45
	}, {
		"name" : "Muskeln, Knochen & Gelenke",
		"href": "/search?symptom=Muskeln+Knochen+Gelenke",
		"x" : width * 0.42,
		"y" : height * 0.8
	}, {
		"name" : "Allergie & Asthma",
		"href": "/search?symptom=Allergie+Asthma",
		"x" : width * 0.42,
		"y" : height * 0.22
	}, {
		"name" : "Frauen",
		"href": "/search?symptom=Frauen",
		"x" : width * 0.32,
		"y" : height * 0.55
	}, {
		"name" : "Männer",
		"href": "/search?symptom=Männer",
		"x" : width * 0.52,
		"y" : height * 0.55
	}, {
		"name" : "Mund & Rachen",
		"href": "/search?symptom=Mund+Rachen+Halsschmerzen",
		"x" : width * 0.42,
		"y" : height * 0.13
	}, {
		"name" : "Nerven & Psyche",
		"href": "/search?symptom=Nerven+Psyche+Psycho",
		"x" : width * 0.42,
		"y" : height * 0.04
	}, {
		"name" : "Niere & Blase",
		"href": "/search?symptom=Niere+Blase",
		"x" : width * 0.42,
		"y" : height * 0.5
	}, {
		"name" : "Schilddrüse",
		"href": "/search?symptom=Schilddrüse",
		"x" : width * 0.42,
		"y" : height * 0.17
	} ];
	

	var svg = d3.select("#human_body").append("svg").attr("width",
			width + margin.left + margin.right).attr("height",
			height + margin.top + margin.bottom);

	var g = svg.selectAll("g")
    .data(bodyparts)
  .enter().append("g").append("a")
  .attr("xlink:href", function(d) { return d.href; });

	/*http://bl.ocks.org/andreaskoller/7674031*/
	g.append("rect"); // placeholder for background rect, position and size will be defined later

	g.append("text") // content of text will be defined later
    .attr("x", function(d) { return d.x; })
    .attr("y", function(d) { return d.y; })
    .attr("fill", "black")
	.attr("text-anchor", "middle")
	.style("font-weight","bold");

    svg.selectAll("text").text(function(d) {
        return d.name;
    });

    svg.selectAll("text").each(function(d, i) {
        bodyparts[i].bb = this.getBBox(); // get bounding box of text field and store it in texts array
    });

    var paddingLeftRight = 18;
    var paddingTopBottom = 5;

    svg.selectAll("rect")
        .attr("x", function(d) { return d.x - d.bb.width/2 - paddingLeftRight/2; })
        .attr("y", function(d) { return d.y - d.bb.height + paddingTopBottom/2 - 2;  })
        .attr("width", function(d) { return d.bb.width + paddingLeftRight; })
        .attr("height", function(d) { return d.bb.height + paddingTopBottom; })
        .style("fill", "green")
        .style("fill-opacity", 0.7);
});