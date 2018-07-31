package com.jimi.agv_mock.dao;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseWindow<M extends BaseWindow<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setArea(java.lang.Integer area) {
		set("area", area);
		return (M)this;
	}
	
	public java.lang.Integer getArea() {
		return getInt("area");
	}

	public M setRow(java.lang.Integer row) {
		set("row", row);
		return (M)this;
	}
	
	public java.lang.Integer getRow() {
		return getInt("row");
	}

	public M setCol(java.lang.Integer col) {
		set("col", col);
		return (M)this;
	}
	
	public java.lang.Integer getCol() {
		return getInt("col");
	}

}
