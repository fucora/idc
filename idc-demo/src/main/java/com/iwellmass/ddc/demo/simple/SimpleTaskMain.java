package com.iwellmass.ddc.demo.simple;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.google.common.collect.Sets;
import com.iwellmass.dispatcher.sdk.SchedulerStarter;
import com.iwellmass.dispatcher.sdk.model.DDCException;
import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.idc.lookup.SourceLookup;

public class SimpleTaskMain {

	public static void main(String[] args) {
		Set<ITaskService> tasks = Sets.newHashSet();
		tasks.add(new SimpleTask());
		SchedulerStarter starter = new SchedulerStarter("default", tasks, "127.0.0.1");
		
		
		SourceLookup lookup = new SourceLookup() {
			public boolean lookup(String jobId, LocalDateTime loadDate) {
				System.out.println("检查到了");
				return false;
			}
		};
		
		starter.withSourceLookup("com.iwellmass.datafactory.job.DataProcessJob", lookup);
		
		try {
			starter.start();
			
		} catch (DDCException e) {
			System.exit(1);
			e.printStackTrace();
		}
		
		Scanner input = new Scanner(System.in);
        String val = null;       // 记录输入的字符串
        do{
            System.out.print("请输入：");
            val = input.next();       // 等待输入值
        }while(!val.equals("#"));   // 如果输入的值不是#就继续输入
        System.out.println("程序已经退出！");
        input.close(); // 关闭资源
		
	}

}