package damai.ctrl;

import java.text.ParseException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import damai.svc.DamaiAfcApplySvc;
import rebue.afc.to.ApplyWithdrawTo;
import rebue.afc.withdraw.to.WithdrawOkTo;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.IdRo;
import rebue.robotech.ro.Ro;
import rebue.wheel.AgentUtils;
import rebue.wheel.turing.JwtUtils;

@RestController
public class DamaiAfcCtrl {

    private final static Logger _log = LoggerFactory.getLogger(DamaiWxCtrl.class);

    @Value("${debug:false}")
    private Boolean isDebug;

    @Resource
    private DamaiAfcApplySvc damaiAfcApplySvc;

    @PostMapping("/withdraw/apply")
    IdRo apply(@RequestBody ApplyWithdrawTo to, HttpServletRequest req) {
        _log.info("申请提现： {}", to);
        if (to.getIp() == null) {
            to.setIp(AgentUtils.getIpAddr(req, "nginx"));
        }
        try {
            return damaiAfcApplySvc.apply(to);

        } catch (Exception e) {
            _log.error("添加申请提现出错, {}", e);
            IdRo ro = new IdRo();
            ro.setResult(ResultDic.FAIL);
            ro.setMsg(e.getMessage());
            return ro;
        }
    }

    /**
     * 确认提现成功（手动）
     * 
     * @throws ParseException
     * @throws NumberFormatException
     */
    @PutMapping("/withdraw/ok")
    Ro ok(@RequestBody WithdrawOkTo to, HttpServletRequest req) throws NumberFormatException, ParseException {
        _log.info("确认提现成功（手动）： {}", to);
        // 获取当前登录用户id
        Long loginId = 521912602410876932L;
        if (!isDebug) {
            loginId = JwtUtils.getJwtUserIdInCookie(req);
        }
        to.setIp(AgentUtils.getIpAddr(req, "nginx"));
        to.setMac("不再获取MAC地址");
        to.setOpId(loginId);
        try {
            return damaiAfcApplySvc.ok(to);
        } catch (Exception e) {
            _log.error("确认提现失败，{}", e);
            Ro ro = new Ro();
            ro.setResult(ResultDic.FAIL);
            ro.setMsg(e.getMessage());
            return ro;
        }
    }

}
