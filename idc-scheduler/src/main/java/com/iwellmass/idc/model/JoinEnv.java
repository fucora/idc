package com.iwellmass.idc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinEnv extends GuardEnv {

	private TaskKey joinKey;

	private TaskKey mainTaskKey;


}
