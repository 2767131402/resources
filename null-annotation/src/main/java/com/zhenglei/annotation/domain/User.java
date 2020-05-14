package com.zhenglei.annotation.domain;

import com.zhenglei.annotation.EnumMessageType;
import com.zhenglei.annotation.annotation.NotNull;

public class User {

	@NotNull(message = "id不能为空",type= EnumMessageType.APP)
	private String id;
	@NotNull(message = "name不能为空",type=EnumMessageType.MESSAGE)
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
