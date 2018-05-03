package com.iwellmass.ddc.demo.simple;

import java.util.Scanner;
import java.util.Set;

import com.google.common.collect.Sets;
import com.iwellmass.dispatcher.sdk.SchedulerStarter;
import com.iwellmass.dispatcher.sdk.model.DDCException;
import com.iwellmass.dispatcher.sdk.service.ITaskService;

public class SimpleTaskMain {

	public static void main(String[] args) {
		Set<ITaskService> tasks = Sets.newHashSet();
		tasks.add(new SimpleTask());
		SchedulerStarter starter = new SchedulerStarter("651fa1182b674cc0ac31142b4d1f7800", tasks, "127.0.0.1");
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