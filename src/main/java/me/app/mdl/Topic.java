package me.app.mdl;

import java.util.List;

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
