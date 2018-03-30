# reverse-geocoder-for-geoevent

ArcGIS GeoEvent Server sample Reverse Geocoder Processor for looking up the nearest street address using the GeoEvent's geometry and a geocode service. 

The Reverse Geocoder Processor can be used in a GeoEvent Service to take the geometry of the incoming point GeoEvent and reverse geocode it against the configured geocoding service to find the nearest street address within a user-defined radius. It appends the fields of the nearest address, if any, to the GeoEvent, generating a new GeoEvent Definition. By default it uses the ArcGIS Online World GeoCoding Service, but users can specify another. This use of the ArcGIS Online World Geocoding Service does not consume ArcGIS Online credits.

![App](reverse-geocoder-for-geoevent.jpg?raw=true)

## Features
* Reverse GeoCoder Processor

## Instructions

Building the source code:

1. Make sure Maven and ArcGIS GeoEvent Server SDK are installed on your machine.
2. Run 'mvn install -Dcontact.address=[YourContactEmailAddress]'

Installing the built jar files:

1. Copy the *.jar files under the 'target' sub-folder(s) into the [ArcGIS-GeoEvent-Server-Install-Directory]/deploy folder.

## Requirements

* ArcGIS GeoEvent Server.
* ArcGIS GeoEvent Server SDK.
* Java JDK 1.7 or greater.
* Maven.

## Resources

* [Download the connector's tutorial](http://www.arcgis.com/home/item.html?id=7eed835dc2044a6787799de4f7cd0e45) from the ArcGIS GeoEvent  Gallery
* [ArcGIS GeoEvent Server Resources](http://links.esri.com/geoevent)
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
Copyright 2017 Esri

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
