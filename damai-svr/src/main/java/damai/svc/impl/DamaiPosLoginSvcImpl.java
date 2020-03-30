package damai.svc.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.Mapper;

import damai.api.ro.DamaiPosLoginRo;
import damai.api.ro.DamaiShopRo;
import damai.api.ro.DamaiUserRo;
import damai.svc.DamaiPosLoginSvc;
import rebue.robotech.dic.ResultDic;
import rebue.slr.mo.SlrShopAccountMo;
import rebue.slr.mo.SlrShopMo;
import rebue.slr.svr.feign.SlrShopAccountSvc;
import rebue.slr.svr.feign.SlrShopSvc;
import rebue.suc.dic.LoginResultDic;
import rebue.suc.ro.UserLoginRo;
import rebue.suc.svr.feign.LoginSvc;
import rebue.suc.to.LoginByUserNameTo;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class DamaiPosLoginSvcImpl implements DamaiPosLoginSvc {
    private static final Logger _log = LoggerFactory.getLogger(DamaiPosLoginSvcImpl.class);

    @Resource
    private LoginSvc loginSvc;

    @Resource
    private SlrShopAccountSvc slrShopAccountSvc;

    @Resource
    private SlrShopSvc slrShopSvc;

    @Resource
    private Mapper dozerMapper;

    /**
     * 收银账号登录
     * 
     * @param to
     * @return
     */
    @Override
    public DamaiPosLoginRo posAccountLogin(LoginByUserNameTo to) {
        to.setSysId("admin");
        _log.info("收银账号登录的参数为：LoginByUserNameTo-{}", to);
        DamaiPosLoginRo ro = new DamaiPosLoginRo();
        if (StringUtils.isAnyBlank(to.getUserName(), to.getLoginPswd(), to.getSysId())) {
            _log.error("收银账号登录出现参数错误，请求的参数为：{}", to);
            ro.setResult(ResultDic.PARAM_ERROR);
            ro.setMsg("参数错误");
            return ro;
        }

        _log.info("收银账号登录用户通过用户名称登录的参数为：LoginByUserNameTo-{}", to);
        UserLoginRo loginRo = loginSvc.loginByUserName(to);
        _log.info("收银账号登录用户通过用户名称登录的返回值为：UserLoginRo-{}", loginRo);
        if (loginRo == null || loginRo.getResult() != LoginResultDic.SUCCESS) {
            _log.error("收银账号登录用户通过用户名称登录出现错误，请求的参数为：LoginByUserNameTo-{}", to);
            ro.setResult(ResultDic.FAIL);
            ro.setMsg(loginRo.getMsg());
            return ro;
        }

        SlrShopAccountMo shopAccountMo = new SlrShopAccountMo();
        _log.info("收银账号登录根据用户id查询店铺账号的参数为：{}", loginRo.getUserId());
        shopAccountMo = slrShopAccountSvc.getOneShopAccountByAccountId(loginRo.getUserId());
        _log.info("收银账号登录根据用户id查询店铺账号的返回值为：{}", shopAccountMo);
        if (shopAccountMo == null) {
            _log.warn("收银账号登录时发现用户未绑定任何店铺，请求的参数为：{}", to);
            ro.setResult(ResultDic.FAIL);
            ro.setMsg("您未绑定任何店铺");
            return ro;
        }

        DamaiUserRo damaiUserRo = dozerMapper.map(loginRo, DamaiUserRo.class);
        damaiUserRo.setId(loginRo.getUserId());

        _log.info("收银账号登录根据店铺id查询店铺信息的参数为：{}", shopAccountMo.getShopId());
        SlrShopMo shopMo = slrShopSvc.getById(shopAccountMo.getShopId());
        _log.info("收银账号登录根据店铺id查询店铺信息的返回值为：{}", shopMo);
        if (shopMo == null) {
            _log.error("收银账号登录时发现店铺不存在，请求的参数为：{}", to);
            ro.setResult(ResultDic.FAIL);
            ro.setMsg("店铺不存在");
            return ro;
        }

        DamaiShopRo damaiShopRo = dozerMapper.map(shopMo, DamaiShopRo.class);

        ro.setResult(ResultDic.SUCCESS);
        ro.setMsg("登录成功");
        ro.setShop(damaiShopRo);
        ro.setUser(damaiUserRo);
        _log.info("收银账号登录的最终返回值为：{}", ro);
        return ro;
    }
}
