package damai.svc;

import damai.api.ro.DamaiPosLoginRo;
import rebue.suc.to.LoginByUserNameTo;

public interface DamaiPosLoginSvc {
    /**
     * 收银账号登录
     * 
     * @param to
     * @return
     */
    DamaiPosLoginRo posAccountLogin(LoginByUserNameTo to);
}
