package damai.ctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import damai.api.ro.DamaiShopLoginRo;
import damai.svc.DamaiShopLoginSvc;
import rebue.jwt.svr.feign.JwtSvc;
import rebue.robotech.dic.ResultDic;
import rebue.scx.jwt.dic.JwtSignResultDic;
import rebue.scx.jwt.ro.JwtSignRo;
import rebue.scx.jwt.to.JwtUserInfoTo;
import rebue.suc.to.LoginByUserNameTo;
import rebue.wheel.AgentUtils;
import rebue.wheel.turing.JwtUtils;

@RestController
public class DamaiShopLoginCtrl {

    private final static Logger _log = LoggerFactory.getLogger(DamaiShopLoginCtrl.class);

    @Resource
    private DamaiShopLoginSvc svc;

    @Resource
    private JwtSvc jwtSvc;

    /**
     * 前面经过的代理
     */
    @Value("${damai.passProxy:noproxy}")
    private String passProxy;

    /**
     * 店铺账号登录
     * 
     * @param to
     * @return
     */
    @PostMapping("/milkteashoppos/login")
    DamaiShopLoginRo shopAccountLogin(@RequestBody LoginByUserNameTo to, final HttpServletRequest req,
            final HttpServletResponse resp) {
        // 添加领域Id，奶茶店只有商家能登陆
        List<String> domainId = new ArrayList<String>();

        to.setIp(AgentUtils.getIpAddr(req, passProxy));
        to.setUserAgent("APP登录不获取浏览器信息");
        to.setMac("不再获取MAC地址");
        to.setDomainId("bussines");
        _log.info("店铺账号登录的请求参数为：{}", to);
        DamaiShopLoginRo ro = new DamaiShopLoginRo();
        if (to.getLoginPswd() == null || to.getUserName() == null) {
            ro.setResult(ResultDic.PARAM_ERROR);
            ro.setMsg("帐号或密码不能为空");
            return ro;
        }
        ro = svc.shopAccountLogin(to);

        if (ResultDic.SUCCESS.equals(ro.getResult())) {
            jwtSignWithCookie(ro, to.getSysId(), null, resp);
        }
        return ro;
    }

    /**
     * JWT签名并将其加入Cookie
     */
    private void jwtSignWithCookie(final DamaiShopLoginRo ro, final String sysId, Map<String, Object> addition,
            final HttpServletResponse resp) {
        final JwtUserInfoTo to = new JwtUserInfoTo();
        to.setUserId(ro.getUser().getUserId().toString());
        to.setSysId(sysId);
        final JwtSignRo signRo = jwtSvc.sign(to);
        if (JwtSignResultDic.SUCCESS.equals(signRo.getResult())) {
            JwtUtils.addCookie(signRo.getSign(), signRo.getExpirationTime(), resp);
        }
    }
}
