package com.pragmagenia.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="app")
public class App {

	private static final String VERSION = "1.0.0";
	private static final String NAME = "Pragma-Administrator";
	
	private String version = VERSION;
	private String name = NAME;
	
	private static App app = null;
	
	
	private App(){}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public static App getInstance(){
		if(app == null){
			app = new App();
		}
		return app;
	}
	
	
}
