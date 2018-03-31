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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.geoevent.GeoEventPropertyName;
import com.esri.ges.core.property.Property;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManagerException;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.Messaging;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;

public class ReverseGeocoderProcessor extends GeoEventProcessorBase
{
	/**
	 * Initialize the i18n Bundle Logger
	 * 
	 * See {@link BundleLogger} for more info.
	 */

	private static final Log					LOG						= LogFactory.getLog(ReverseGeocoderProcessor.class);
	private static final BundleLogger	LOGGER				= BundleLoggerFactory.getLogger(ReverseGeocoderProcessor.class);

	private Object										propertyLock	= new Object();
	private int												agolSearchDistance;
	private String										agolSearchFormat;
	private String										geocodeServiceUrl;
	private GeoEventCreator						geoEventCreator;
	private String										newGeoEventDefinitionName;


	protected ReverseGeocoderProcessor(GeoEventProcessorDefinition definition) throws ComponentException
	{
		super(definition);
	}

	@Override
	public void afterPropertiesSet()
	{
		synchronized (propertyLock)
		{
			agolSearchDistance = 100;
			if (hasProperty(ReverseGeocoderProcessorDefinition.AGOL_SEARCHDISTANCE_PROPERTY))
			{
				agolSearchDistance = (int) getProperty(ReverseGeocoderProcessorDefinition.AGOL_SEARCHDISTANCE_PROPERTY).getValue();
			}

			geocodeServiceUrl = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/reverseGeocode";
			if (hasProperty(ReverseGeocoderProcessorDefinition.GEOCODE_SERVICE_PROPERTY))
			{
				geocodeServiceUrl = (String) getProperty(ReverseGeocoderProcessorDefinition.GEOCODE_SERVICE_PROPERTY).getValue();
			}

			newGeoEventDefinitionName = "NewGeoEventDefinition";
			if (hasProperty(ReverseGeocoderProcessorDefinition.NEWGEOEVENTDEFINITION_PROPERTY))
			{
				newGeoEventDefinitionName = (String) getProperty(ReverseGeocoderProcessorDefinition.NEWGEOEVENTDEFINITION_PROPERTY).getValue();
			}
		}
	}

	@Override
	public GeoEvent process(GeoEvent geoEvent) throws Exception
	{
		GeoEvent agolStreetAddress = processGeoEvent(geoEvent);

		return agolStreetAddress;
	}

	private GeoEvent processGeoEvent(GeoEvent geoEvent) throws MalformedURLException, JSONException, ConfigurationException, GeoEventDefinitionManagerException, FieldException
	{
		if (geoEvent.getTrackId() == null || geoEvent.getGeometry() == null)
		{
			LOGGER.warn("NULL_ERROR: TrackID and/or Geometry is NULL.");
			return null;
		}

		Geometry geom = geoEvent.getGeometry().getGeometry();
		if (geom.isEmpty())
			return geoEvent;

		if (!Geometry.isPoint(geom.getType().value()))
			return geoEvent;

		if (Geometry.isMultiVertex(geom.getType().value()))
			return geoEvent;

		Point point = (Point) geom;

		double lon = point.getX();
		double lat = point.getY();
		int wkid = geoEvent.getGeometry().getSpatialReference().getID();

		// fetch nearest street address (reverse geocode) via ArcGIS Online World GeoCode service
		// The response format. Values: html | json | kmz 
		// The default response format is html.
		agolSearchFormat = "json"; 
		URL agolURL = new URL(geocodeServiceUrl + "?location=" + Double.toString(lon) + "," + Double.toString(lat) + "&distance=" + Integer.toString(agolSearchDistance) + "&outSR=" + Integer.toString(wkid) + "&f=" + agolSearchFormat);
		
		String addressJson = getReverseGeocode(agolURL);
		GeoEvent agolStreetAddress = augmentGeoEventWithAddress(geoEvent, addressJson);
		return agolStreetAddress;
	}

	private String getReverseGeocode(URL url)
	{
		String output = "";

		try
		{

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200)
			{
				String errorString = "Failed : HTTP error code : " + conn.getResponseCode();
				throw new RuntimeException(errorString);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String line;
			while ((line = br.readLine()) != null)
			{
				output += line;
			}

			conn.disconnect();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return output;
	}

	private GeoEvent augmentGeoEventWithAddress(GeoEvent geoEvent, String addressJson) throws ConfigurationException, GeoEventDefinitionManagerException, FieldException, JSONException
	{
		JSONObject jsonObj = new JSONObject(addressJson);
		JSONObject addressJSONObject = jsonObj.getJSONObject("address");
		GeoEvent newGeoEvent = null;
		if (geoEventCreator != null)
		{
			try
			{
				GeoEventDefinition edIn = geoEvent.getGeoEventDefinition();

				List<FieldDefinition> fds = new ArrayList<FieldDefinition>();
				fds.add(new DefaultFieldDefinition("Address", FieldType.String));
				fds.add(new DefaultFieldDefinition("Neighborhood", FieldType.String));
				fds.add(new DefaultFieldDefinition("City", FieldType.String));
				fds.add(new DefaultFieldDefinition("Subregion", FieldType.String));
				fds.add(new DefaultFieldDefinition("Region", FieldType.String));
				fds.add(new DefaultFieldDefinition("Postal", FieldType.String));
				fds.add(new DefaultFieldDefinition("PostalExt", FieldType.String));
				fds.add(new DefaultFieldDefinition("CountryCode", FieldType.String));
				fds.add(new DefaultFieldDefinition("Match_addr", FieldType.String));
				fds.add(new DefaultFieldDefinition("Loc_name", FieldType.String));

				GeoEventDefinition edOut = edIn.augment(fds);

				edOut.setName(newGeoEventDefinitionName);
				edOut.setOwner(getId());
				GeoEventDefinitionManager geoEventDefinitionManager = geoEventCreator.getGeoEventDefinitionManager();

				// if (geoEventDefinitionManager.getGeoEventDefinition(newGeoEventDefinitionName) == null)
				//   geoEventDefinitionManager.addGeoEventDefinition(edOut);

				// LOGGER.info("Checking for GED: " + edOut.getName());
				String gedGuid = "";
				try
				{
					Collection<GeoEventDefinition> geoEventDefinitions = geoEventDefinitionManager.searchInRepositoryGeoEventDefinitionByName(newGeoEventDefinitionName);

					// LOGGER.info("Found " + geoEventDefinitions.size() + " GEDs named " + edOut.getName());
					if (geoEventDefinitions == null || geoEventDefinitions.size() == 0)
					{
						// LOGGER.info("Didn't find it. Adding GED: " + edOut.getName());
						geoEventDefinitionManager.addGeoEventDefinition(edOut);
					}
					else
						gedGuid = geoEventDefinitions.iterator().next().getGuid();
				}
				catch (Exception ex)
				{
					LOGGER.info("Encountered an error checking for this GED: " + edOut.getName() + ". Adding it anyway.");
					geoEventDefinitionManager.addGeoEventDefinition(edOut);
					gedGuid = edOut.getGuid();
				}

				List<Object> fieldValues = new ArrayList<Object>();
				Object value;
				for (int i = 0; i < edIn.getFieldDefinitions().size(); i++)
				{
					value = geoEvent.getField(i);
					fieldValues.add(value);
				}

				try
				{
					fieldValues.add(addressJSONObject.get("Address"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}
				try
				{
					fieldValues.add(addressJSONObject.get("Neighborhood"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}
				try
				{
					fieldValues.add(addressJSONObject.get("City"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}
				try
				{
					fieldValues.add(addressJSONObject.get("Subregion"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}
				try
				{
					fieldValues.add(addressJSONObject.get("Region"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}
				try
				{
					fieldValues.add(addressJSONObject.get("Postal"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}
				try
				{
					fieldValues.add(addressJSONObject.get("PostalExt"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}
				try
				{
					fieldValues.add(addressJSONObject.get("CountryCode"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}
				try
				{
					fieldValues.add(addressJSONObject.get("Match_addr"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}
				try
				{
					fieldValues.add(addressJSONObject.get("Loc_name"));
				}
				catch (Exception ex)
				{
					fieldValues.add("");
				}

				// LOGGER.info("Creating a Geoevent from GED: " + edOut.getName());
				newGeoEvent = geoEventCreator.create(gedGuid);
				// LOGGER.info("Create a Geoevent from GED: " + edOut.getName());

				// newGeoEvent = geoEventCreator.create(newGeoEventDefinitionName, definition.getUri().toString());
				for (int i = 0; i < edOut.getFieldDefinitions().size(); i++)
				{
					try
					{
						newGeoEvent.setField(i, fieldValues.get(i));
					}
					catch (Exception ex)
					{
					}
				}

				newGeoEvent.setProperty(GeoEventPropertyName.TYPE, "event");
				newGeoEvent.setProperty(GeoEventPropertyName.OWNER_ID, getId());
				newGeoEvent.setProperty(GeoEventPropertyName.OWNER_URI, definition.getUri());

			}
			catch (MessagingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return newGeoEvent;
	}

	public void setMessaging(Messaging messaging)
	{
		geoEventCreator = messaging.createGeoEventCreator();
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(definition.getName());
		sb.append("/");
		sb.append(definition.getVersion());
		sb.append("[");
		for (Property p : getProperties())
		{
			sb.append(p.getDefinition().getPropertyName());
			sb.append(":");
			sb.append(p.getValue());
			sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
}
