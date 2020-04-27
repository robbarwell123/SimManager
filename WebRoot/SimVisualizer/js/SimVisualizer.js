// Global Variables
var myLog;			// List of log entries
var myFrames;		// List of time periods in the log entry

var myMap;					// A map panel
var myLogStateOutput;		// State Output
var myLogMessageOutput;		// Message Output

/**
 * Initalization functions that are executed when the page loads
 */
$(document).ready(function ()
{
	var myURL = new URL(window.location.href);
	var iId=myURL.searchParams.get("iId");
	document.getElementById("myTimeSlider").oninput=SliderChange;
	
	$.ajax({
		type: "GET",
		url: Addr_GetLogs + iId,
		timeout: 0,
		success: function (jsonResp)
		{
			if(jsonResp.status === "Success")
			{
				try
				{
					// Parse log results into myLog and myFrames
					myLog=jQuery.parseJSON(jsonResp.data[0]);
					myFrames=Object.keys(myLog);
					$('#CURRTIME').html(myFrames[0]);
					iMaxFrame=myFrames.length-1;
					myMap=MapPanel().id("ExpMap").canvas("#PANELS").newPanel();
					myLogStateOutput=TextPanel().id("StateOutput").canvas("#PANELS").filterType("state").filterProperty("message").newPanel();
					myLogMessageOutput=TextPanel().id("MessageOutput").canvas("#PANELS").filterType("message").filterProperty("Debug").newPanel();
					$('#myTimeSlider').attr("max",iMaxFrame);
				}catch(errMsg)
				{
					console.log(errMsg);
					$('#STATUS').html("Error retriving data.  Could be a bad experiment id or the application is unable to parse the log file.");
					$('#MESSAGE').show();
				}
			}else
			{
				$('#STATUS').html("Error retriving data. "+jsonResp.data);
				$('#MESSAGE').show();
			}
		},
		error: function (errMsg)
		{
			$('#STATUS').html("Unknown error: "+errMsg+".  Is the ViewExperiment service running?");
			$('#MESSAGE').show();
		}
	});	
});

// Constants for the different playback options listed at the top of the page
const MODE_START=1;
const MODE_FORWARD=2;
const MODE_PLAYPAUSE=3;
const MODE_NEXT=4;
const MODE_END=5;

var bPlay=true;

var iCurrFrame=0;
var bContinous=false;
var iMaxFrames=0;
var oRun;

/**
 * Reacts to the player buttons pressed at the top of the page
 * @param  {integer} modeOpt Which button was pressed
 */
function ChangeMode(modeOpt)
{
	var bPlaySwitch=false;
	switch(modeOpt)
	{
		case MODE_START:
			iCurrFrame=0;
			break;
		case MODE_FORWARD:
			iCurrFrame--;
			break;
		case MODE_PLAYPAUSE:
			bPlay=!bPlay;
			bPlaySwitch=true;
			break;
		case MODE_NEXT:
			iCurrFrame++;
			break;
		case MODE_END:
			iCurrFrame=iMaxFrame;
			break;
	}

	clearInterval(oRun);
	$('#PLAYPAUSE').attr("src","./img/play.svg");

	if(bPlay && bPlaySwitch)
	{
		oRun=setInterval(RunPlay,1000);
		$('#PLAYPAUSE').attr("src","./img/pause.svg");
	}else
	{
		bPlay=false;
	}

	ChangeFrame();
}

/**
 * Allows panels to subscribe to data updates from the player.
 * @param  {string} myObjectId Unique name of the object.
 * @param  {function} myNotifyFunction The functio to notify when data changes.  Must have the format of Function(data).
 */
var myNotifyList = {};
function SubscribeToData(myObjectId,myNotifyFunction)
{
	myNotifyList[myObjectId]=myNotifyFunction;
}
/**
 * Allows panels to unsubscribe to data updates from the player.
 * @param  {string} myObjectId Unique name of the object.
 */
function UnsubscribeToData(myObjectId)
{
	delete myNotifyList[myObjectId]; 
}

/**
 * Used to notify each data subscriber of a frame change.
 */
function ChangeFrame()
{
	iCurrFrame=iCurrFrame < 0 ? 0 : iCurrFrame;
	iCurrFrame=iCurrFrame > iMaxFrame ? iMaxFrame : iCurrFrame;	
	$('#CURRTIME').html(myFrames[iCurrFrame]);
	for(var myObjectId in myNotifyList)
	{
		myNotifyList[myObjectId](myLog[myFrames[iCurrFrame]]);
	}
	$('#myTimeSlider').val(iCurrFrame);	
}

/**
 * Used as part of the play function to advance to the next frame every second.
 */
function RunPlay()
{
	iCurrFrame++;
	ChangeFrame();
	if(iCurrFrame>=iMaxFrame)
	{
		clearInterval(oRun);
		$('#PLAYPAUSE').attr("src","./img/play.svg");
		bPlay=false;
	}
}

// Functions to handle opening, closing, and displaying menus

/**
 * Opens the main hamburger menu
 */
var bMenu=false;
function OpenMenu()
{
	if(bMenu)
	{
		$("#MENUOPTIONS").slideUp(200);
		bMenu=false;
	}else
	{
		$("#MENUOPTIONS").slideDown(200);
		bMenu=true;
	}
}

/**
 * Used to show the faded background layer.  This prevents interaction with the page if a popup is open.
 * @param  {boolean} bToogleState Show or Hide the faded background layer
 */
function ToggleBlank(bToggleState)
{
	if(bToggleState)
	{
		$(".BLANK").css("display","flex");
	}else
	{
		$(".BLANK").hide();
	}
}

function SliderChange()
{
	bPlay=false;
	clearInterval(oRun);
	$('#PLAYPAUSE').attr("src","./img/play.svg");
	iCurrFrame=$('#myTimeSlider').val();
	ChangeFrame();
}
