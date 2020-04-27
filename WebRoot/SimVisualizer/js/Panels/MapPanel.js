/**
 * These functions are used to draw the map panel on the Sim Visualizer page to display results on the map.
 */
function MapPanel()
{
	var sContentLoc="";
	var sID="Map"+Math.random();
	
	var myMap;
	var myCurrLayer;
	var myNewLayer;
	var mySelect;
	
	function Render(){}

	Render.newPanel = function () {	
		var myMapPanel=d3.select(sContentLoc).append("DIV")
			.attr("id",sID)
			.attr("class","MAPPANEL");
		myMapPanel.append("DIV")
			.attr("id",sID+"MAPMSG")
			.attr("class","MAPMSG");
		myMapPanel.append("DIV")
			.attr("id",sID+"MAP")
			.attr("class","MAPPANEL");
		
		// Initalizes the map to world scale and centered on Trenton, Ontario
		myMap=new ol.Map({
			target: sID+"MAP",
			layers: [
				new ol.layer.Tile({
					source: new ol.source.OSM()
				})
			],
			view: new ol.View({
				center: ol.proj.fromLonLat([-77.6,44.1]),
				zoom: 0
			})
		});
		
		// Sets up the function to deal with map interaction when the user clicks on the map
		mySelect=new ol.interaction.Select();
		mySelect.on('select',function(e){
			if(e.selected.length>0)
			{
				$("#"+sID+"MAPMSG").html(e.selected[0].get("message"));
				$("#"+sID+"MAPMSG").show();
			}else{
				$("#"+sID+"MAPMSG").html("");
				$("#"+sID+"MAPMSG").hide();
			}
		});
		myMap.addInteraction(mySelect);
		
		// Subscribes to data updates from the player
		SubscribeToData(sID,this.update);
		Render.update(myLog[myFrames[iCurrFrame]]);
				
		return Render;
	};
	
	Render.update = function(myData)
	{
		// Determines if something is already selected on the map so that it can be maintained after the next frame refresh.
		var myCurrSelect;
		var mySelectMarker;
		if(mySelect.getFeatures().getArray().length>0)
		{
			myCurrSelect=mySelect.getFeatures().getArray()[0].getId();;
		}
		
		var myFeatures=[];
		var tMarker;
		
		for(myLogEntry of myData)
		{
			try
			{
				if(myLogEntry.data.hasOwnProperty("location"))
				{
					if(myLogEntry.data.location.long!=0 && myLogEntry.data.location.lat!=0)
					{
						tMarker=new ol.Feature({
							geometry: new ol.geom.Point(
								ol.proj.fromLonLat([myLogEntry.data.location.long,myLogEntry.data.location.lat])
							),
						});
						tMarker.setId(myLogEntry.name);
						tMarker.set('message',myLogEntry.data.message);
						// Sets the icon style for each data point.  It is based off the class type.
						tMarker.setStyle(new ol.style.Style({
							image: new ol.style.Icon(({
								src: './img/locations/'+myLogEntry.data.class+'.png'
							}))
						}));
						myFeatures.push(tMarker);
						if(myCurrSelect === myLogEntry.name)
						{
							mySelectMarker=tMarker;
						}
					}
				}
			}catch(errEntry)
			{
				console.log("Unknown entry log error: "+errEntry);
			}
		}

		var mySources = new ol.source.Vector({
			features: myFeatures
		});
		
		// Creates a new layer
		myNewLayer=new ol.layer.Vector({
			source: mySources
		});
		myMap.addLayer(myNewLayer);
		// Removes the old layer
		if(myCurrLayer!=null)
		{
			myMap.removeLayer(myCurrLayer);
		}

		myCurrLayer=myNewLayer;
		// Re-selects the correct object and updates the map message with the current information.
		if(typeof(mySelectMarker) !== 'undefined')
		{
			$("#"+sID+"MAPMSG").html(mySelectMarker.get("message"));
			mySelect.features_.push(mySelectMarker);
		}
		
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
	
	return Render;
}