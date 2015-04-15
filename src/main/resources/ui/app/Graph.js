// also need to adjust Table.js in addition to main.js
// require.js needed to manage dependencies between modules
function Graph() {}

Graph.prototype = new Visualization();
Graph.prototype.constructor = Visualization;
Graph.prototype.parent = Visualization.prototype;

Graph.prototype._draw = function(graph) {
    var width = this._width,
        height = this._height;

    var nodes = graph.vertices;
    var links = graph.edges;

    this._graphDiv = generateDiv(this._content, "graph");

    var svg = d3.select('#graph').append('svg')
        .attr('width', width)
        .attr('height', height);

    var force = d3.layout.force()
        .size([width, height])
        .nodes(nodes)
        .links(links)
        .linkDistance(Math.min(width, height)/6.5);

    var links = svg.selectAll('.link')
        .data(links)
        .enter().append('line')
        .attr('class', 'link');

    var nodes = svg.selectAll('.node')
        .data(nodes)
        .enter()

    var circles = nodes.append('circle')
        .attr('class', 'node');

    var labels = nodes.append('text')
      .text(function(n) { return n.label; })
    .attr('fill', 'black');

    force.on('tick', function() {
        circles.attr('r', 5)
            .attr('cx', function(n) { return n.x; })
            .attr('cy', function(n) { return n.y; });

        labels.attr('x', function(n) { return n.x+7; })
            .attr('y', function(n) { return n.y-4; })

        links.attr('x1', function(l) { return l.source.x; })
            .attr('y1', function(l) { return l.source.y; })
            .attr('x2', function(l) { return l.target.x; })
            .attr('y2', function(l) { return l.target.y; });
    });

    force.start();
}

Graph.prototype.clear = function() {
	removeElementIfExists(this._graphDiv);
}
