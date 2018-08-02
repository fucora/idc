package com.iwellmass.dispatcher.sdk.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * List数据根据线程数量均匀切分
 * @author Ming.Li
 *
 */
public final class ListUtils {

	private ListUtils() {
	}

	/**
	 * 
	 * @param list 待切分原始数据
	 * @param shardCount 分片数量
	 * @return
	 */
	public static <T> List<List<T>> partition(List<T> list, int shardCount) {

		List<List<T>> splitedList = new ArrayList<List<T>>();

		for(int i=0; i<shardCount; i++) {
			splitedList.add(new ArrayList<T>());
		}

		for(int i=0; i<list.size(); i++) {
			int index = i % shardCount;
			splitedList.get(index).add(list.get(i));
		}

		return splitedList;
	}
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<>();
		Random r = new Random();
		
		int total = r.nextInt(10000) + 5;
		for(int i=0; i<total; i++) {
			list.add(i);
		}
		
		List<List<Integer>> splited = partition(list, 5);
		System.out.println("data size = " + total);
		System.out.println("thread size = " + 5);
		for(List<Integer> l : splited) {
			System.out.println(l.size());
		}
	}
}
