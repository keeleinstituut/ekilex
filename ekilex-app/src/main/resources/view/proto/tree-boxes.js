var i = 1;

function initWordEtymTree() {
  var wordId = $("#word-id").val();
  var applicationUrl = $("#app-url").val();
  var wordEtymTreeUrl = applicationUrl + "proto/wordetymtree/" + wordId;

  $.get(wordEtymTreeUrl).done(function(data) {
    //console.log(JSON.stringify(data, undefined, 4));
    setup(data);
  }).fail(function(data) {
    console.log(data);
  });
}

function setup(data) {
  var maxHeight = data.maxLevel * 100;
  var margin = { top: 20, right: 10, bottom: 30, left: 10 };
  var width = 1000 - margin.left - margin.right;
  var height = maxHeight + margin.top + margin.bottom;

  var svg = d3
    .select("div[id='tree-container']")
    .append("svg")
    .attr("width", width + margin.right + margin.left)
    .attr("height", height + margin.top + margin.bottom)
    ;

  var svgg = svg
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + 100 + ")")
    ;

  var tree = d3.tree().size([550, 120]);

  var root = d3.hierarchy(data.root, function(d) {
    return d.children;
  });

  root.x0 = width / 2;
  root.y0 = 0;

  var treeData = tree(root);
  var nodes = treeData.descendants()

  var nodeMap = {};
  nodes.forEach(function(d) {
    console.log(d)
    d.y = d.depth * 90;
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
  })
  ;

  render(svgg, root, nodes, links);
}

function render(svgg, root, nodes, links) {
  // -- nodes --
  var node = svgg.selectAll("g.node").data(nodes, function(d) {return d.id || (d.id = ++i); });

  // add node
  var nodeEnter = node
    .enter()
    .append("g")
    .attr("class", "node")
    .attr("transform", function(d) {
      return "translate(" + root.x0 + "," + root.y0 + ")";
    })
    .on("click", clickNode)
    ;

  // path lang
  nodeEnter
    .append("path")
    .attr("d", "m -0.1 7.9 c 0 -4.4 3.4 -8 7.6 -8 h 45.4 v 40 h -45.4 c -4.2 0 -7.6 -3.6 -7.6 -8")
    .attr("fill", "#D7E5F2")
    ;

  // text lang
  nodeEnter
    .append("text")
    .style("fill", "#173148")
    .attr("dy", 26)
    .attr("x", 26)
    .attr("text-anchor", function(d) {
      return "middle";
    })
    .text(function(d) {
      return d.data.wordLang;
    })
    ;

  // text word
  nodeEnter
    .append("text")
    .attr("class", "word-text")
    .style("fill", "#173148")
    .attr("dy", 26)
    .attr("x", 60)
    .attr("text-anchor", function(d) {
      return "start";
    })
    .text(function(d) {
      return d.data.wordValue;
    })
    ;

  // rect word
  nodeEnter
    .append("rect")
    .attr("class", "node-border")
    .attr("rx", 7)
    .attr("ry", 7)
    .attr("x", 0)
    .attr("y", 0)
    .attr("height", 40)
    .attr("width", function(d){
      var wordTextElement = d3.select(this.parentNode).select(".word-text").node();
      var textWidth = Math.round(wordTextElement.getBBox().width);
      return textWidth + 70;
    })
    .attr("stroke", "#D7E5F2")
    .attr("stroke-width", 3)
    .attr("fill", "none")
    ;

  var nodeUpdate = nodeEnter.merge(node);

  nodeUpdate
    .attr("transform", function(d) {
      var nodeLength = d3.select(this).select(".node-border").attr("width");
      var position = Math.round(nodeLength / 4);
      return "translate(" + ((d.x + position)) + "," + d.y + ")";
    })
    ;

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
    .on("click", clickLink)
    ;

  var linkUpdate = linkEnter.merge(link);

  linkUpdate
    .attr("d", function(d) {
      var sn = { x: d.s_x, y: d.s_y };
      var tn = { x: d.t_x, y: d.t_y };
      return diagonal(sn, tn);
    });

  // link curviture
  function diagonal(s, d) {
    path = `M ${s.x + 100} ${s.y + 40}
            C ${s.x + 100} ${(s.y + d.y + 40) / 2},
              ${d.x + 100} ${(s.y + d.y + 40) / 2},
              ${d.x + 100} ${d.y}`;
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