/*
   WPM - Wiseman plugin manager
*/

const $root = {

   "class": new Array(),

   "prefix": "data-",

   "apply": function(){
      $wpm.setStyles();
      $wpm.setClasses();
   }

};

const $wpm = {

   debug: true,
   
   attributes: [$root.prefix+"plugin"],

   initialize: function(){

      if( $wpm.debug ){
         console.info("jQuery present. Initialization started. So far so good!"); 
      }

      var resizeDelay = 200;
      $root.resizeArray = new Array();
      var resizeDebounce = false;

      $(window).bind("resize", function(e){
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
      

      $root.apply();

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
         
         
         obj.removeAttr(""+$root.prefix+"plugin "+$root.prefix+"toggle "+$root.prefix+"click "+$root.prefix+"init "+$root.prefix+"class");
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

      $root.resizeArray.push( plugin );


      try {
          obj[plugin](attributes);
      }
      catch(err){
          if( $wpm.debug ){
             console.info("The plugin '"+plugin+"' does not exist! Maybe there's a typo in the plugin name?");
          }
      }

   },
   
   click: function(obj){
      var attribute = obj.attr($root.prefix+"click").replace(/ +/g, '').split("=");
      var variable = attribute[0];
      var value = attribute[1];

      
      obj.bind("click", function(e){
         e.preventDefault();

         $root[variable] = value;

         if( $wpm.debug ){
            console.log(variable+"="+$root[variable]);
         }

         $root.apply();

      });
   },
   
   class: function(obj){
      var variable = obj.attr($root.prefix+"class").replace(/ +/g, '');
      var className = variable.match(/ *\'[^']*\' */g).toString().replace(/'+/g, '');

      variable = variable.match(/\:(.*)$/g).toString().replace(":", '');

      var value = true;

      if( variable.match("==") ){
         value = variable.split("==")[1];
         variable = variable.split("==")[0];
      }

      if( variable ){
         if( $root.class[variable] ){
            $root.class[variable].push({
               object: obj,
               className: className,
               value: value
            });
         }else{
            $root.class[variable] = [{
               object: obj,
               className: className,
               value: value
            }];
         }
      }
   },
   
   toggle: function(obj){
      var variable = obj.attr($root.prefix+"toggle");

      obj.bind("click", function(e){

         e.preventDefault();
         $root[variable] = $root[variable] ? false : true;

         if( variable == "accessibility" ){
            $root['headerSearch'] = false;
         }

         if( variable == "headerSearch" ){
            $root['accessibility'] = false;
         }

         if( $wpm.debug ){
            console.log(variable+"="+$root[variable]);
         }

         $root.apply();

      });
   },
   
   init: function(obj){
      var attribute = obj.attr($root.prefix+"init").replace(/ +/g, '').split("=");
      var variable = attribute[0];
      var value = attribute[1];

      $root[variable] = value;

      if( $wpm.debug ){
         console.log(variable+"="+$root[variable]);
      }
   },

   setStyles: function(obj){

      if( !$root.styleObj ){
         $("body").append( $root.styleObj = $("<style></style>") );
      }

      var styleOutput = '';

      for( var i in $root ){

         if( $root[i] == $root.prefix ){ continue; }
         
         if( typeof $root[i] == "string" ){
            styleOutput+= "* ["+$root.prefix+"show='"+i+" == "+$root[i]+"']{display:block !important;}";
            styleOutput+= "* ["+$root.prefix+"show='"+i+" == "+$root[i]+" | flex']{display:flex !important;}";
            styleOutput+= "* ["+$root.prefix+"show='"+i+" == "+$root[i]+" | inlineBlock']{display:inline-block !important;}";
         }
         else if( typeof $root[i] !== "function" && typeof $root[i] !== "object" ){
            
            var show = $root[i] ? "block" : "none";
            var hide = $root[i] ? "none" : "block";
            
            styleOutput+= "* ["+$root.prefix+"show='"+i+"']{display:"+show+" !important;}";
            styleOutput+= "* ["+$root.prefix+"show='"+i+" | inlineBlock']{display:"+( show == 'block' ? 'inline-block' : 'none') +" !important;}";
            styleOutput+= "* ["+$root.prefix+"show='"+i+" | flex']{display:"+( show == 'block' ? 'flex' : 'none') +" !important;}";
            
            styleOutput+= "* ["+$root.prefix+"hide='"+i+"']{display:"+hide+" !important;}";
         }
      }

      $root.styleObj.text( styleOutput );

      if( $wpm.debug ){
         console.info("New styles generated");  
      }
   },

   setClasses: function(){
      
      for( var i in $root['class'] ){
         var key = i;
         var value = $root[i];
         var instance = $root['class'][i];
         
         for( var ii in instance ){
            var entry = instance[ii];

            if( $root[key] == entry['value'] ){
               entry.object.addClass(entry.className);
            }else{
               entry.object.removeClass(entry.className);
            }
         }
      }

      if( $wpm.debug ){
         console.info("Classes toggled!");
      }
   }
}


if( 'jQuery' in window ){
   $(function(){
      $wpm.initialize();
   });
}
else{
   console.warn("jQuery not loaded! Load jQuery before WPM!");
}