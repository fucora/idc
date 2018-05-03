package com.iwellmass.dispatcher.server.util;

import org.springframework.beans.factory.annotation.Value;

public class PropertyHolder {

	@Value("${five.minute.statistic.delay}")
	private int fiveMinutesDelay;
	
	@Value("${hour.statistic.cron}")
	private  String hourlyStatisticCron ;

	public int getFiveMinutesDelay() {
		return fiveMinutesDelay;
	}

	public void setFiveMinutesDelay(int fiveMinutesDelay) {
		this.fiveMinutesDelay = fiveMinutesDelay;
	}

	public String getHourlyStatisticCron() {
		return hourlyStatisticCron;
	}

	public void setHourlyStatisticCron(String hourlyStatisticCron) {
		this.hourlyStatisticCron = hourlyStatisticCron;
	}

}
