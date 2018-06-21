package com.jimi.uw_server.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装与解封实体类，继承该类重写相关方法
 * @author Lenovo
 *
 */
public abstract class EntityFieldFiller<T, K> {
	
	/**
	 * 封装
	 * @param t
	 * @return
	 */
	public K fill(T t) {
		throw new RuntimeException("子类必须重写该方法才能调用");
	}
	
	/**
	 * 封装列表
	 * @param tList
	 * @return vo的List
	 */
	public List<K> fill(List<T> tList) {
		List<K> list = new ArrayList<K>(tList.size());
		for (T t : tList) {
			K vo = fill(t);
			list.add(vo);
		}
		return list;
	}
	
	/**
	 * 解封
	 * @param k
	 * @return
	 */
	public T unfill(K k) {
		throw new RuntimeException("子类必须重写该方法才能调用");
	}
	
	/**
	 * 解封集合
	 * @param kList
	 * @return
	 */
	public List<T> unFill(List<K> kList) {
		List<T> tList = new ArrayList<T>(kList.size());
		for (K k : kList) {
			T bo = unfill(k);
			tList.add(bo);
		}
		return tList;
	}
}
