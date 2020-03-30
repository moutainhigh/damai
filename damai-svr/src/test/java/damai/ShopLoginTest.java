package damai;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;


import rebue.suc.to.LoginByUserNameTo;
import rebue.wheel.OkhttpUtils;
import rebue.wheel.turing.DigestUtils;

public class ShopLoginTest {

	private final String hostUrl = "http://127.0.0.1:9081";
	
	//@Test
	public void shopAccountLoginTest() throws IOException {
		LoginByUserNameTo to = new LoginByUserNameTo();
		to.setUserName("admin");
		to.setLoginPswd(DigestUtils.md5AsHexStr("12345678".getBytes()));
		to.setSysId("damai-pos-food-app");
		String result = OkhttpUtils.postByJsonParams(hostUrl + "/milkteashoppos/login", to);
		System.out.println(result);
	}
	
	//微信端登录
	@Test
	public void WxLoginTest() throws IOException {
		HashMap<String, Object> hmap = new HashMap<String, Object>();
		hmap.put("key", "11223");
		hmap.put("openid", "0123454");
		hmap.put("unionid", "0055674");// 微信开放平台信息编号(微信的ID)[补充说明]
		hmap.put("nickname", "nickname225150");// 用户昵称
		hmap.put("headimgurl", "headimgurl1");// 用户头像
		hmap.put("source", 4);// 登录的来源 编码与用户注册的注册来源一致
		hmap.put("mac", "15123");// 登录mac
		hmap.put("ip", "0077");// 登录ip
		String result = OkhttpUtils.get(hostUrl+"/damai-wx/login", hmap);
		System.out.println(result);
	}
}
