package damai.svc;

import damai.api.ro.DamaiShopLoginRo;
import rebue.suc.to.LoginByUserNameTo;

public interface DamaiShopLoginSvc {

	/**
	 * 店铺账号登录
	 * @param to
	 * @return
	 */
	DamaiShopLoginRo shopAccountLogin(LoginByUserNameTo to);

}
