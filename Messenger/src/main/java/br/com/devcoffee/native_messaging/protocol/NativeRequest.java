package br.com.devcoffee.native_messaging.protocol;

import javax.xml.bind.annotation.XmlElement;

public class NativeRequest {
	
	@XmlElement(name = "contype")
	private String contype;

	@XmlElement(name = "address")
	private String address;

	@XmlElement(name = "addrport")
	private int addrport;

	public NativeRequest() {
		super();
	}

	public String getContype() {
		return contype;
	}

	public void setContype(String contype) {
		this.contype = contype;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAddrport() {
		return addrport;
	}

	public void setAddrport(int addrport) {
		this.addrport = addrport;
	}
}
