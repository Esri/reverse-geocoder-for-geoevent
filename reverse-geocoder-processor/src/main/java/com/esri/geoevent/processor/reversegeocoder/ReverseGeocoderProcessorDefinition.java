/*
  Copyright 1995-2015 Esri

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

  For additional information, contact:
  Environmental Systems Research Institute, Inc.
  Attn: Contracts Dept
  380 New York Street
  Redlands, California, USA 92373

  email: contracts@esri.com
 */

package com.esri.geoevent.processor.reversegeocoder;

import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class ReverseGeocoderProcessorDefinition extends GeoEventProcessorDefinitionBase
{
	private static final BundleLogger	LOGGER													= BundleLoggerFactory.getLogger(ReverseGeocoderProcessorDefinition.class);
	protected static final String			AGOL_SEARCHDISTANCE_PROPERTY		= "agolSearchDist";
	protected static final String			NEWGEOEVENTDEFINITION_PROPERTY	= "newGeoEventDefinitionName";

	public ReverseGeocoderProcessorDefinition()
	{
		try
		{
			PropertyDefinition agolSearchDistanceProperty = new PropertyDefinition(AGOL_SEARCHDISTANCE_PROPERTY, PropertyType.Integer, 100, "ArcGIS Online Reverse Geocoder Search Radius (m)", "The distance from the geoevent's location within which a matching address should be searched.", true, false);
			propertyDefinitions.put(AGOL_SEARCHDISTANCE_PROPERTY, agolSearchDistanceProperty);

			PropertyDefinition newGeoEventDefinitionNameProperty = new PropertyDefinition(NEWGEOEVENTDEFINITION_PROPERTY, PropertyType.String, "NewGeoEventDefinition", "Resulting GeoEvent Definition", "The incoming GeoEvent will be augmented with the address fields from the reverse geocode and a new GeoEvent Definition will be created.", true, false);
			propertyDefinitions.put(NEWGEOEVENTDEFINITION_PROPERTY, newGeoEventDefinitionNameProperty);
		}
		catch (PropertyException ex)
		{
			;
		}
		catch (Exception error)
		{
			LOGGER.error("INIT_ERROR", error.getMessage());
			LOGGER.info(error.getMessage(), error);
		}

	}

	@Override
	public String getName()
	{
		return "ReverseGeocoderProcessor";
	}

	@Override
	public String getDomain()
	{
		return "reversegeocoder.processor";
	}

	@Override
	public String getVersion()
	{
		return "10.5.0";
	}

	@Override
	public String getLabel()
	{
		return "${com.esri.geoevent.processor.reverse-geocoder-processor.PROCESSOR_LABEL}";
	}

	@Override
	public String getDescription()
	{
		return "${com.esri.geoevent.processor.reverse-geocoder-processor.PROCESSOR_DESC}";
	}
}
