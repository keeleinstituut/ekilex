var i = 1;
var levelWidth = 270;
var nodeWidth = 200;

function initWordEtymTree() {

	var wordId = $("#word-id").val();
	var wordEtymTreeUrl = applicationUrl + "proto/wordetymtree/" + wordId;

	$.get(wordEtymTreeUrl).done(function(data) {
		//console.log(JSON.stringify(data, undefined, 4));
		setup(data);
	}).fail(function(data) {
		console.log(data);
	});
}

function setup(data) {

	var maxWidth = data.maxLevel * levelWidth;

	var margin = { top: 20, right: 10, bottom: 30, left: 10 };
	var width = maxWidth - margin.left - margin.right;
	var height = 800 - margin.top - margin.bottom;

	var svg = d3
		.select("div[id='tree-container']")
		.append("svg")
		.attr("width", width + margin.right + margin.left)
		.attr("height", height + margin.top + margin.bottom);

	var defs = svg.append("defs");
	var marker = defs
		.append("marker")
		.attr("id", "arrow")
		.attr("viewBox", "-10 -10 20 20")
		.attr("refX", "10")
		.attr("refY", "0")
		.attr("markerWidth", "10")
		.attr("markerHeight", "10")
		.attr("class", "arrow")
		.attr("orient", "auto");
	marker
		.append("path")
		.attr("d", "M0,-10L10,0L0,10");

	var svgg = svg
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	var tree = d3.tree().size([height, width]);
	var root = d3.hierarchy(data.root, function(d) {
		return d.children;
	});
	root.x0 = height / 2;
	root.y0 = 0;

	var treeData = tree(root);
	var nodes = treeData.descendants();

	var nodeMap = {};
	nodes.forEach(function(d) {
		// nodes horiz distance
		d.y = d.depth * levelWidth;
		nodeMap[d.data.wordId] = d;
	});

	var links = data.links;
	links.forEach(function(d) {
		var sn = nodeMap[d.sourceWordId];
		var tn = nodeMap[d.targetWordId];
		d.s_x = sn.x;
		d.s_y = sn.y;
		d.t_x = tn.x;
		d.t_y = tn.y;
	});

	render(svgg, root, nodes, links);
}

function render(svgg, root, nodes, links) {

	// -- nodes --

	var node = svgg.selectAll("g.node").data(nodes, function(d) {
		i++;
		if (d.id == undefined) {
			d.id = i;
		}
		return d.id;
	});

	// add node
	var nodeEnter = node
		.enter()
		.append("g")
		.attr("class", "node")
		.attr("transform", function(d) {
			return "translate(" + root.y0 + "," + root.x0 + ")";
		})
		.on("click", clickNode);

	// rect word
	nodeEnter
		.append("rect")
		.attr("rx", function(d) {
			if (d.parent) {
				return d.children || d._children ? 0 : 10
			};
			return 10;
		})
		.attr("ry", function(d) {
			if (d.parent) {
				return d.children || d._children ? 0 : 10
			};
			return 10;
		})
		.attr("x", 0)
		.attr("y", -30)
		.attr("width", nodeWidth)
		.attr("height", 60)
		.attr("stroke", "#ccd9e0")
		.attr("stroke-width", 3)
		.attr("fill", "none")
		;

	// rect lang
	nodeEnter
		.append("rect")
		.attr("x", 10)
		.attr("y", -20)
		.attr("width", 40)
		.attr("height", 40)
		.attr("fill", "#ccd9e0")
		;

	// text word
	nodeEnter
		.append("text")
		.style("fill", "rgb(150, 150, 150)")
		.attr("dy", ".35em")
		.attr("x", 60)
		.attr("text-anchor", function(d) {
			return "start";
		})
		.text(function(d) {
			return d.data.word;
		});

	// text lang
	nodeEnter
		.append("text")
		.style("fill", "rgb(150, 150, 150)")
		.attr("dy", ".35em")
		.attr("x", 30)
		.attr("text-anchor", function(d) {
			return "middle";
		})
		.text(function(d) {
			return d.data.lang;
		});

	var nodeUpdate = nodeEnter.merge(node);

	nodeUpdate
		.attr("transform", function(d) {
			return "translate(" + d.y + "," + d.x + ")";
		});

	// -- links --

	var link = svgg.selectAll("path.link").data(links, function(d) {
		return d.id;
	});

	var linkEnter = link
		.enter()
		.insert("path", "g")
		.attr("class", "link")
		.attr("d", function(d) {
			var o = { x: root.x0, y: root.y0 };
			return diagonal(o, o);
		})
		.attr("marker-end", "url(#arrow)")
		.on("click", clickLink);

	var linkUpdate = linkEnter.merge(link);

	linkUpdate
		.attr("d", function(d) {
			var sn = { x: d.s_x, y: d.s_y };
			var tn = { x: d.t_x, y: d.t_y };
			return diagonal(sn, tn);
		});

	// link curviture
	function diagonal(s, d) {
		path = `M ${s.y + nodeWidth} ${s.x}
            C ${(s.y + d.y + nodeWidth) / 2} ${s.x},
              ${(s.y + d.y + nodeWidth) / 2} ${d.x},
              ${d.y - 3} ${d.x}`;
		return path;
	}

	function clickNode(d) {
		var modalId = "word-etym-node-edit-modal-" + d.data.wordId;
		$("#" + modalId).modal();
	}

	function clickLink(d) {
		var modalId = "word-etym-link-edit-modal-" + d.wordEtymRelId;
		$("#" + modalId).modal();
	}
}
