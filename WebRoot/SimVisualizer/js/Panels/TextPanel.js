/**
 * These functions are used to draw the map panel on the Sim Visualizer page to display results on the map.
 */
function TextPanel()
{
	var sContentLoc="";
	var sID="Text"+Math.random();
	
	var myText;
	var sFilterType="";
	var sFilterProperty="";
	
	function Render(){}

	Render.newPanel = function () {	
		var myText=d3.select(sContentLoc).append("DIV")
			.attr("id",sID)
			.attr("class","TEXTPANEL");
						
		// Subscribes to data updates from the player
		SubscribeToData(sID,this.update);
		Render.update(myLog[myFrames[iCurrFrame]]);
				
		return Render;
	};
	
	Render.update = function(myData)
	{		
		var myTextContent="";
		for(myLogEntry of myData)
		{
			try
			{
				if(myLogEntry.type === sFilterType && myLogEntry.data.hasOwnProperty(sFilterProperty))
				{
					myTextContent+="<STRONG>"+myLogEntry.name+":</STRONG> "+myLogEntry.data[sFilterProperty]+"<BR>";
				}
			}catch(errEntry)
			{
				console.log("Unknown entry log error: "+errEntry);
			}
		}

		$("#"+sID).html(myTextContent);
		
		return Render;
	}
	
	Render.remove = function()
	{
		UnsubscribeToData(sID);
		d3.select("#"+sID).remove();
		return null;
	}
	
	Render.canvas = function(sValue) {
		if(!arguments.length) return sContentLoc;
		sContentLoc=sValue;
		return Render;
	};

	Render.id = function(sValue) {
		if(!arguments.length) return sID;
		sID=sValue;
		return Render;
	};

	Render.filterType = function(sValue) {
		if(!arguments.length) return sFilterType;
		sFilterType=sValue;
		return Render;
	};

	Render.filterProperty = function(sValue) {
		if(!arguments.length) return sFilterProperty;
		sFilterProperty=sValue;
		return Render;
	};
	
	return Render;
}