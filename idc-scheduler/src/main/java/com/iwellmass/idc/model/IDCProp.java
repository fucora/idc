package com.iwellmass.idc.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_idc_plugin")
public class IDCProp {

	@Id
	@Column(name = "prop_name")
	private String name;
	
	@Column(name = "prop_value")
	private String value;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "updatetime")
	private Timestamp updatetime;
	
}
