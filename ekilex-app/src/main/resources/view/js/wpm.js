/*
   WPM - Wiseman plugin manager
*/

const $root = {

   "class": new Array(),

   "prefix": "data-",
   resizeArray: new Array(),
};

const $wpm = {

   debug: true,
   
   attributes: [$root.prefix+"plugin"],

   initialize: function(){

      if( $wpm.debug ){
         console.info("jQuery present. Initialization started. So far so good!"); 
      }

      var resizeDelay = 200;
      var resizeDebounce = false;

      $(window).on("resize", function(e){
         clearTimeout(resizeDebounce);

         resizeDebounce = setTimeout(function(){
            for( var i in $root.resizeArray ){
               var functionName = $root.resizeArray[i];
               $(window).trigger("resize:"+functionName);   
            };
         }, resizeDelay);

      }).trigger('resize');

      //FastClick.attach(document.body);
      
      $wpm.bindObjects();
   },
   
   getAttributes: function( $node ) {
       var attrs = {};
       $.each( $node[0].attributes, function ( index, attribute ) {
           attrs[attribute.name] = attribute.value;
       } );

       return attrs;
   },
   
   bindObjects: function(elem){
      
      var selectorString = '';
      for( var i in $wpm.attributes ){
         if( i > 0 ){ selectorString+=",";}
         selectorString+= "["+$wpm.attributes[i]+"]";
      }
      
      var selector = elem ? elem.find(selectorString) : $(selectorString);

      selector.each(function(){
         var obj = $(this);
         
         var objectAttrs = $wpm.getAttributes( obj );

         for(var i in $wpm.getAttributes( obj ) ){
            
            var name = i.replace($root.prefix, "");
            var value = objectAttrs[i];
            
            if( $.inArray( i, $wpm.attributes ) !== -1 && i.match($root.prefix) ){
               if( name == "show" || name == "hide" ){}
               else{
                  $wpm[name](obj);
               }
            }
         };
         
         
         obj.removeAttr(""+$root.prefix+"plugin");
      }); 
   },
   
   plugin: function(obj){

      var plugin = obj.attr($root.prefix+"plugin");
      var attributes = plugin.match(/ *\([^)]*\) */g, "");

      if( attributes ){
         attributes = attributes.toString().replace(/[\(\)]+/g,'').split(", ");
      }else{
         attributes = null;
      }

      plugin = plugin.replace(/ *\([^)]*\) */g, "");
      
      // Allows for adding multiple plugins to the same element
      // Example syntax: data-plugin="examplePlugin, secondExamplePlugin,thirdExamplePlugin"
      // Can use a space next to comma or leave it out
      plugin.split(',').forEach(str => {
         // Remove all whitespace
         const splitPlugin = str.replace(/\s/g, '');
         $root.resizeArray.push( splitPlugin );
         try {
            obj[splitPlugin](attributes);
         }
         catch(err){
            if( $wpm.debug ){
               console.info("The plugin '"+splitPlugin+"' does not exist! Maybe there's a typo in the plugin name?");
               console.log(err);
            }
         }
      })
   },
   
   
}


if( 'jQuery' in window ){
   $(function(){
      $wpm.initialize();
   });
}
else{
   console.warn("jQuery not loaded! Load jQuery before WPM!");
}