package damai.ctrl;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.dozermapper.core.Mapper;

import rebue.suc.dic.LoginResultDic;
import rebue.suc.dic.RegResultDic;
import rebue.suc.ro.UserLoginRo;
import rebue.suc.ro.UserRegRo;
import rebue.suc.svr.feign.SucUserSvc;
import rebue.suc.to.LoginByWxTo;
import rebue.suc.to.RegByWxTo;
import rebue.wheel.turing.JwtUtils;

@RestController
public class DamaiWxCtrl {

    private final static Logger _log = LoggerFactory.getLogger(DamaiWxCtrl.class);

    /**
     * 跳转成功页面链接
     */
    @Value("${damai.wx.successUrl}")
    private String              successUrl;
    /**
     * 跳转失败页面链接
     */
    @Value("${damai.wx.failUrl}")
    private String              failUrl;

    @Resource
    private SucUserSvc          sucUserSvc;

    @Resource
    private Mapper              dozerMapper;

    @GetMapping("/damai-wx/login")
    public String login(@RequestParam final Map<String, String> userInfo, final HttpServletResponse resp) throws IOException {
        _log.info("接收到微信登录的请求: {}", userInfo);
        final LoginByWxTo loginTo = new LoginByWxTo();

        final String state = userInfo.get("state");
        _log.info("接收到微信登录过来的state: {}", state);

        loginTo.setSysId("damai-wx");
        loginTo.setWxId(userInfo.get("unionid"));
        loginTo.setWxOpenid(userInfo.get("openid"));
        loginTo.setWxNickname(userInfo.get("nickname"));
        loginTo.setWxFace(userInfo.get("headimgurl"));
        loginTo.setDomainId("buyer");
        _log.info("接收到的登录参数"+loginTo);
        
        final UserLoginRo loginRo = sucUserSvc.loginByWx(loginTo);
        if (LoginResultDic.SUCCESS.equals(loginRo.getResult())) {
            _log.info("微信登录成功");
            redirectSuccess(loginRo.getSign(), loginRo.getExpirationTime(), resp);
            return null;
        } else if (LoginResultDic.NOT_FOUND_USER.equals(loginRo.getResult())) {
            _log.info("微信登录没有发现此用户，将注册新用户");
            final RegByWxTo regTo = dozerMapper.map(loginTo, RegByWxTo.class);
            final UserRegRo regRo = sucUserSvc.regByWx(regTo);
            // 注册成功
            if (RegResultDic.SUCCESS.equals(regRo.getResult())) {
                _log.info("微信登录注册新用户成功");
                redirectSuccess(regRo.getSign(), regRo.getExpirationTime(), resp);
                return null;
            } else {
                _log.info("微信注册失败: {}", regRo);
            }
        } else {
            _log.info("微信登录失败: {}", loginRo);
        }

        _log.info("跳转失败页面: {}", failUrl);
        resp.sendRedirect(failUrl);
        return null;
    }

    /**
     * 跳转成功页面
     */
    private void redirectSuccess(final String sign, final Date expirationTime, final HttpServletResponse resp) throws IOException {
        JwtUtils.addCookie(sign, expirationTime, resp);
        _log.info("跳转成功页面: {}", successUrl);
        resp.sendRedirect(successUrl);
    }

}
