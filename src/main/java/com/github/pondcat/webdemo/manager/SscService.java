package com.github.pondcat.webdemo.manager;

import com.github.pondcat.webdemo.dto.CqSsc;
import retrofit2.http.GET;

/**
 * @author gejian
 */
public interface SscService {

	@GET("cqssc.json")
	CqSsc<CqSsc.Result> cqssc();

}
