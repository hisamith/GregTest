package org.wso2.appfactory.gregloadtest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by wdfdo1986 on 5/22/15.
 */
public class ConfigReader {
	private static ConfigReader reader = new ConfigReader();
	private Element documentElement;
	private ConfigReader(){
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("config.xml").getFile());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			documentElement = doc.getDocumentElement();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static ConfigReader getInstance(){
		return reader;
	}

	public String getProperty(String property){
		return documentElement.getElementsByTagName(property).item(0).getTextContent();
	}

}
