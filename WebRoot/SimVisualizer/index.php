<HTML>
	<HEAD>
		<TITLE>Sim Visualizer v1.0</TITLE>
		<LINK rel="stylesheet" href="./css/SimVisualizer.css" />
		<LINK rel="stylesheet" href="https://cdn.jsdelivr.net/gh/openlayers/openlayers.github.io@master/en/v6.1.1/css/ol.css" type="text/css" />
		<SCRIPT src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></SCRIPT>		
		<SCRIPT src="https://d3js.org/d3.v5.min.js"></SCRIPT>
		<SCRIPT src="https://cdn.jsdelivr.net/gh/openlayers/openlayers.github.io@master/en/v6.1.1/build/ol.js"></SCRIPT>
		<SCRIPT src="./js/config/cfg_simvisualizer.js"></SCRIPT>
		<SCRIPT src="./js/SimVisualizer.js"></SCRIPT>
		<SCRIPT src="./js/Panels/MapPanel.js"></SCRIPT>
		<SCRIPT src="./js/Panels/TextPanel.js"></SCRIPT>
	</HEAD>
	<BODY>
		<DIV class="APP">
			<DIV class="HEADER">
				<DIV class="SUBHEADER">
					<SPAN class="DIVCENTER"><IMG src="./img/menu.svg" height="32px" width="32px" onClick="javascript:OpenMenu()">&nbsp;</SPAN>
					<SPAN class="DIVCENTER">Sim Visualizer v1.0</SPAN>
				</DIV>
			</DIV>
			<DIV id="MENUOPTIONS">
				<UL id="NAVMENU">
<!--					<LI><A href="javascript:">Menu Option 1</A></LI>-->
				</UL>
			</DIV>

			<DIV class="CONTENT">
				<DIV id="MESSAGE">
					<IMG src="./img/info.svg" width=32 height=32><SPAN id="STATUS"></SPAN>
				</DIV>
				<DIV id="PLAYER">
					<A href="javascript:ChangeMode(1)"><IMG src="./img/back.svg" height="32px" width="32px"></A>
					<A href="javascript:ChangeMode(2)"><IMG src="./img/backward.svg" height="32px" width="32px"></A>
					<A href="javascript:ChangeMode(3)"><IMG src="./img/play.svg" height="32px" width="32px" id="PLAYPAUSE"></A>
					<A href="javascript:ChangeMode(4)"><IMG src="./img/forwards.svg" height="32px" width="32px"></A>					
					<A href="javascript:ChangeMode(5)"><IMG src="./img/next.svg" height="32px" width="32px"></A>
					<DIV id="CURRTIME">
					Loading ...
					</DIV>
					<INPUT type="range" min="1" max="1" class="TIMESLIDER" id="myTimeSlider">
				</DIV>
				<DIV id="PANELS">
				</DIV>
				<DIV id="FOOTER">
					<BR>
					<DIV>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></DIV>
				</DIV>
			</DIV>			
			<DIV class="BLANK">
			</DIV>			
		</DIV>
	</BODY>
</HTML>