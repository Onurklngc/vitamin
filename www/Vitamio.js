var exec = require('cordova/exec');

function Vitamio() { 
 console.log("Vitamio.js: create");
}

Vitamio.prototype.playVideo = function(url, loadingText, useMediaController){
 console.log("Vitamio.js: playVideo - url = " + url + " - loadingText = " + loadingText + " - useMediaController = " + useMediaController);

 exec(null, null, "Vitamio", "playVideo", [url, loadingText, useMediaController]);
}


 var vitamio = new Vitamio();
 module.exports = vitamio;

Vitamio.install = function () {
	if (!window.plugins) {
		window.plugins = {};
	}
	window.plugins.vitamio = new Vitamio();
	return window.plugins.vitamio;
};

cordova.addConstructor(Vitamio.install);