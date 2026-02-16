const noNPSQuestionsPages = [
	"/lite",
	"/about",
	"/teacher-tools",
	"/learn",
	"/games",
	"/contacts",
];
const NPSUrlCheck = () =>
	noNPSQuestionsPages
		.map((urlsnippet) => window.location.href.includes(urlsnippet))
		.includes(true);

if (NPSUrlCheck()) {
} else {
	// Delighted NPS survey snippet
	!function(e,t,r,n){if(!e[n]){for(var a=e[n]=[],i=["survey","reset","config","init","set","get","event","identify","track","page","screen","group","alias"],s=0;s<i.length;s++){var c=i[s];a[c]=a[c]||function(e){return function(){var t=Array.prototype.slice.call(arguments);a.push([e,t])}}(c)}a.SNIPPET_VERSION="1.0.1";var o=t.createElement("script");o.type="text/javascript",o.async=!0,o.src="https://d2yyd1h5u9mauk.cloudfront.net/integrations/web/v1/library/"+r+"/"+n+".js";var p=t.getElementsByTagName("script")[0];p.parentNode.insertBefore(o,p)}}(window,document,"4fJTOJZk4jWVXEj0","delighted");

	delighted.survey();
}