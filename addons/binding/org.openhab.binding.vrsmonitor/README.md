# Vrs Departure Monitor Binding

This binding allows the informations from a VRS Departure Monitor configured at <https://www.vrsinfo.de/abfahrtsmonitor.html> to be used at openHab items. 


_If possible, provide some resources like pictures, a YouTube video, etc. to give an impression of what can be done with this binding. You can place such resources into a `doc` folder next to this README.md._

## Supported Things

This binding only supports one thing, a vrs departure monitor.

## Thing Configuration

To configure this thing you need first to create a departure monitor at <https://www.vrsinfo.de/abfahrtsmonitor.html>. Once you have done this you should get an url in the form

https://www.vrsinfo.de/abfahrtsmonitor/anzeige/vrs\_info/Departuremonitor/show/vrs\_info-showid/**someSortOfid**.html?L=0&cHash=**someSortOfHash**

You need the first id, behind the slash after vrs\_info-showid. The thing configuration needs three parameters:

* The showId: The id extracted from the URL. Needed to access the departure monitor.
* Refresh: How often the data should be refreshed. Minimum is 60 seconds.
* Number of departures: How many departures should be retrieved. It should not be higher than then number you configured at the vrs website. There will be channels created for every departure.

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within ```ESH-INF/thing``` of your binding._

## Full Example

_Provide a full usage example based on textual configuration files (*.things, *.items, *.sitemap)._

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
