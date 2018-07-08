package com.github.pondcat.webdemo.openapi.weixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pondcat.commons.combine.hash.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@RestController
@RequestMapping("/openapi/weixin/mp")
public class MpApi {

	private static final Logger log = LoggerFactory.getLogger(MpApi.class);

	private static final String token = "pondcat123";

	private static final String appId = "wxe1ddcd147d03882f";

	private static final String appsecret = "d55157efca7c3d31449be3b538914001";

	private RestTemplate restTemplate = new RestTemplate();

	/**
	 * 微信 基本配置 -> 服务器配置 验证
	 * @return 原样返回echostr, 注意:返回响应头中的Content-Type可以没有, 也可以是text/html或text/plain,
	 * 但绝不能是application/json, 这种情况下微信会报错:{"errcode":-106,"errmsg":"token check fail"}
	 */
	@GetMapping(value = "serverVerify", produces = MediaType.TEXT_PLAIN_VALUE)
	public String serverVerify(@Validated(Default.class) String signature,
			@NotNull(groups = { Default.class }) String timestamp, String nonce,
			String echostr, HttpServletResponse response) throws IOException {
		// 明文模式, 不作校验
		String[] arrays = { timestamp, nonce, token };
		Arrays.sort(arrays);
		String encode = DigestUtils
				.sha1Hex((arrays[0] + arrays[1] + arrays[2]).getBytes());
		if (signature.equalsIgnoreCase(encode)) {
			return echostr;
		}
		else {
			log.error("微信公众号服务器配置验证失败");
			return "";
		}
	}

	@PostMapping("serverVerify")
	public void receiveMessage(HttpServletRequest request) throws IOException {
		String s = StreamUtils.copyToString(request.getInputStream(),
				StandardCharsets.UTF_8);
	}

	@GetMapping("image/scanCode")
	public void scanCode(HttpServletResponse response) {
		if (token == null) {
			restTemplate.getForObject(
					"https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
							+ appId + "&secret=" + appsecret,
					String.class);
		}
	}

}
