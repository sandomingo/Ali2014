package me.app.mdl;

import java.util.List;

/**
 * 从Brand跟User类中抽取出共同的结构
 *
 * @author wuxuef2
 * @date: Apr 17, 2014 11:20:33 AM
 * @version 
 */
public class Topic {
	protected Long id;
	protected List<Behavior> behaviors;
	
	public Topic() {		
    }
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<Behavior> getBehaviors() {
		return behaviors;
	}
	public void setBehaviors(List<Behavior> behaviors) {
		this.behaviors = behaviors;
	}	
}
