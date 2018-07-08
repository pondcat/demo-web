package com.github.pondcat.webdemo.dto;


import com.github.pondcat.webdemo.entity.User;

/**
 * @author gejian
 */
public class Student extends User {

	private String grade;

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

}
