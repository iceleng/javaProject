var page = require('webpage').create();
var address="http://www.sohu.com"
var output="kibana.dashboard.png"
page.viewportSize={width:1280,height:800};
page.open(address, function (status) {
	if (status!=='success'){
		console.log('Unable to load the address!');
		phantom.exit();
	}else{
		/*
		window.setTimeout(function(){
			page.render(output);
			phantom.exit();
		},30000);
		*/
		page.render(output);
		console.log('success!');
		phantom.exit();		
	}
});
