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
      return "translate(" + (d.x + position) + "," + d.y + ")";
    })
    ;

  // -- links --
  var link = svgg.selectAll("path.link").data(links, function(d) {
    return d.id;
  });

  var linkEnter = link
  .enter()
  .append("g")
  .attr("class", "link")
  ;

  // append path to linkEnter
  linkEnter
    .append("path")
    .attr("class", "link-path")
    .attr("d", function(d) {
      return diagonal({x: d.s_x, y: d.s_y}, {x: d.t_x, y: d.t_y});
    })
    ;

  // append circle to linkEnter
  linkEnter
    .append("circle")
    .attr("class", "link-circle")
    .attr("r", 10)
    .attr("fill", "#173148")
    .attr("stroke-width", 0)
    .attr("cx", function(d) { return (d.s_x + d.t_x) / 2 + 120; })
    .attr("cy", function(d) { return (d.s_y + d.t_y) / 2 + 20; })
    .on("click", clickLink)
    ;

  //append icon on circle
  linkEnter
    .append("path")
    .attr("class","link-icon")
    .attr("d", "M4.425 10.5C4.425 9.2175 5.4675 8.175 6.75 8.175H9.75V6.75H6.75C4.68 6.75 3 8.43 3 10.5 3 12.57 4.68 14.25 6.75 14.25H9.75V12.825H6.75C5.4675 12.825 4.425 11.7825 4.425 10.5ZM7.5 11.25H13.5V9.75H7.5V11.25ZM14.25 6.75H11.25V8.175H14.25C15.5325 8.175 16.575 9.2175 16.575 10.5 16.575 11.7825 15.5325 12.825 14.25 12.825H11.25V14.25H14.25C16.32 14.25 18 12.57 18 10.5 18 8.43 16.32 6.75 14.25 6.75Z")
    .attr("fill", "#fff")
    .attr("stroke-width", 0)
    .attr("transform", function(d) 
      { return "translate(" + ((d.s_x + d.t_x) / 2 + 109.5) + "," + ((d.s_y + d.t_y) / 2 + 9.5) + ")" })
    ;

  // link curviture
  function diagonal(s, d) {
    path = `M ${s.x + 120} ${s.y + 40}
            C ${s.x + 120} ${(s.y + d.y + 40) / 2},
              ${d.x + 120} ${(s.y + d.y + 40) / 2},
              ${d.x + 120} ${d.y}`;
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