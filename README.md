# reverse-geocoder-for-geoevent

ArcGIS 10.3.x GeoEvent Extension for Server Reverse GeoCoder Lookup Processor Sample for looking up the nearest street address using the GeoEvent's Geometry and the ArcGIS Online World GeoCoding Service.

The Reverse GeoCoder Processor is a sample processor that can be used in a GeoEvent Service to take the Geometry of the incoming GeoEvent and reverse GeoCode it against the ArcGIS Online World GeoCoding Service to find the nearest street address within a user-defined radius. It appends the fields of the nearest address, if any, to the GeoEvent, generating a new GeoEvent Definition.

![App](reverse-geocoder-for-geoevent.jpg?raw=true)

## Features
* Reverse GeoCoder Processor

## Instructions

Building the source code:

1. Make sure Maven and ArcGIS GeoEvent Extension SDK are installed on your machine.
2. Run 'mvn install -Dcontact.address=[YourContactEmailAddress]'

Installing the built jar files:

1. Copy the *.jar files under the 'target' sub-folder(s) into the [ArcGIS-GeoEvent-Extension-Install-Directory]/deploy folder.

## Requirements

* ArcGIS GeoEvent Extension for Server.
* ArcGIS GeoEvent Extension SDK.
* Java JDK 1.7 or greater.
* Maven.

## Resources

* [Download the connector's tutorial](http://www.arcgis.com/home/item.html?id=245dd62551f0459e92e0fb2f079f3da3) from the ArcGIS GeoEvent Extension Gallery
* [ArcGIS GeoEvent Extension for Server Resources](http://links.esri.com/geoevent)
* [ArcGIS Blog](http://blogs.esri.com/esri/arcgis/)
* [twitter@esri](http://twitter.com/esri)

## See also

* [ArcGIS Online Geocoding Service](https://geocode.arcgis.com/arcgis/index.html)
* [Esri World Geocoding Service](https://developers.arcgis.com/en/features/geocoding/)
* [ArcGIS REST API: World Geocoding Service - Reverse Geocode](https://developers.arcgis.com/rest/geocode/api-reference/geocoding-reverse-geocode.htm)
* [World Geocoding](http://www.arcgis.com/home/item.html?id=305f2e55e67f4389bef269669fc2e284)

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

## Licensing
Copyright 2015 Esri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

A copy of the license is available in the repository's [license.txt](license.txt?raw=true) file.

[](ArcGIS, GeoEvent, Extension, Processor)
[](Esri Tags: ArcGIS GeoEvent Extension for Server)
[](Esri Language: Java)
