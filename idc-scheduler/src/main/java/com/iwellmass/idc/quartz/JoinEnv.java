package com.iwellmass.idc.quartz;

import com.iwellmass.idc.model.TaskKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinEnv extends GuardEnv {

	private TaskKey joinKey;

	private TaskKey mainTaskKey;


}
