package com.rameses.clfc.android.services;

import java.util.Map;

import com.rameses.client.services.AbstractService;

public class MobileStatusService extends AbstractService 
{
	public String getServiceName() {
		return "MobileStatusService";
	}
	
	public Map postMobileStatusEncrypt(Map params) {
		return (Map) invoke("postMobileStatusEncrypt", params);
	}

}
