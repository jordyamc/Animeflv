// ==UserScript==
// @name         Animeflv Anti-AntiAdblock
// @namespace    http://tampermonkey.net/
// @version      0.1
// @description  Saltar el bloqueador
// @icon https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
// @author       AnimeFlvApp
// @match        http://*/*
// @grant unsafeWindow
// @grant GM_log
// ==/UserScript==

(function(window) {
    'use strict';
var anti_adblock_animeflv={
    intoString : function (a) {
      if (typeof a === 'function') {
        var str = a.toString();
        var first = str.indexOf("{") + 1;
        var last = str.lastIndexOf("}");
        return str.substr(first, last - first).trim();
      } else if (typeof entry === 'object') {
        return JSON.stringify(a);
      } else { // array or string
        return a.toString();
      }
    },
 addScript : function (source, body) {
      var script = document.createElement('script');
      script.type = "text/javascript";
      script.innerHTML = (typeof source === 'function') ? Aak.intoString(source) : source.toString();
      if (body) {
        document.body.appendChild(script);
      } else {
        document.head.appendChild(script);
      }
      script.remove();
    }
 };
    anti_adblock_animeflv.addScript(anti_adblock_animeflv.intoString(function(){
        $("#contenedor").show();
        $("#adbl").hide();
        console.log("AnimeflvApp Script loaded");
    }));
})(window);