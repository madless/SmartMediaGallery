package com.wb.vapps.network.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.wb.vapps.model.Properties;
import com.wb.vapps.services.model.IntegratedModelsManager;
import com.wb.vapps.utils.FileUtils;
import com.wb.vapps.utils.Logger;

/**
 * Parse app properties response using kxml parser
 * @author amaximenko
 *
 */
public class AppPropertiesParser extends AbstractParser {
	private final Logger logger = Logger.getLogger(AppPropertiesParser.class.getSimpleName());
	
	private Properties properties;
	@SuppressWarnings("unused")
	private IntegratedModelsManager lockerModelManager;
	
	public AppPropertiesParser(IntegratedModelsManager lockerModelManager) {
		this.lockerModelManager = lockerModelManager;
	}
	
	@Override
	public void parse(InputStream is) throws IOException, XmlPullParserException, InterruptedException {
		logger.debug("app properties parse started!");
		
		String currentKey = null;
		String currentValue = null;
		int currentAppId = -1;
		HashMap<String, String> currentProperties = new HashMap<String, String>();
		
		KXmlParser parser = new KXmlParser();
		parser.setInput(new InputStreamReader(is));
		
		String lastEventElementName = "";		
		
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			
			if (interrupted) {	
				FileUtils.close(is);
				
				throw new InterruptedException("Interrupted");
			}
			
			if (parser.getEventType() == XmlPullParser.START_TAG) {
				lastEventElementName = parser.getName();
				
				if (lastEventElementName.equals("appPropertiesResponse")) {					
					currentAppId = Integer.parseInt(parser.getAttributeValue(null, "appId"));
				}
				
				if (lastEventElementName.equals("appProperties")) {					
					properties = new Properties();
					currentProperties = new HashMap<String, String>();
				}
			}
			
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				lastEventElementName = parser.getName();
				
				if (lastEventElementName.equals("property")) {
					currentProperties.put(currentKey, currentValue);
				}
				
				if (lastEventElementName.equals("appProperties")) {
					properties.setAppId(currentAppId);
					properties.setProperties(currentProperties);
				}
			}

			if (parser.getEventType() == XmlPullParser.TEXT) {
				
				if (parser.getText().trim().length()!=0) {
					if (lastEventElementName.equals("key")) {												
						currentKey = parser.getText();
					}
					
					if (lastEventElementName.equals("value")) {												
						currentValue = parser.getText();
					}					
				}
			}
		}
		
		FileUtils.close(is);		
	}

	public Properties getProperties() {
		return properties;
	}
}
