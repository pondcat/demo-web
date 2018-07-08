package com.github.pondcat.webdemo.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.time.LocalDateTime;

/**
 * @author gejian at 2018/5/29 23:50
 */
@TableName("user")
public class User {
	@TableId
	private Long id;

	private String mobile;

	private String realName;

	private String stat;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ctime;

	private String role;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public LocalDateTime getCtime() {
		return ctime;
	}

	public void setCtime(LocalDateTime ctime) {
		this.ctime = ctime;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", mobile='" + mobile + '\'' +
				", realName='" + realName + '\'' +
				", stat='" + stat + '\'' +
				", ctime=" + ctime +
				", role='" + role + '\'' +
				'}';
	}
}
