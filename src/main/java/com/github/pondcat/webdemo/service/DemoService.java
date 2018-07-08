package com.github.pondcat.webdemo.service;

import com.github.pondcat.webdemo.dao.UserMapper;
import com.github.pondcat.webdemo.dto.Student;
import com.github.pondcat.webdemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author gejian
 */
@Service
public class DemoService implements Serializable {

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private UserMapper userMapper;

	@Transactional
	public LocalDateTime serve(@Validated User user1) {
		Student user = new Student();
		user.setGrade("4");
		userMapper.insert(user1);
		return LocalDateTime.now();
	}

}
