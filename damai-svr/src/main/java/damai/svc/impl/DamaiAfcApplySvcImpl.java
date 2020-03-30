package damai.svc.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import damai.pnt.dic.PointLogTypeDic;
import damai.svc.DamaiAfcApplySvc;
import rebue.afc.mo.AfcAccountMo;
import rebue.afc.mo.AfcWithdrawMo;
import rebue.afc.ro.WithdrawNumberForMonthRo;
import rebue.afc.svr.feign.AfcAccountSvc;
import rebue.afc.svr.feign.AfcWithdrawSvc;
import rebue.afc.to.ApplyWithdrawTo;
import rebue.afc.withdraw.to.WithdrawOkTo;
import rebue.pnt.mo.PntAccountMo;
import rebue.pnt.svr.feign.PntAccountSvc;
import rebue.pnt.svr.feign.PntPointSvc;
import rebue.pnt.to.AddPointTradeTo;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.IdRo;
import rebue.robotech.ro.Ro;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class DamaiAfcApplySvcImpl implements DamaiAfcApplySvc {
    private static final Logger _log = LoggerFactory.getLogger(DamaiAfcApplySvcImpl.class);

    @Resource
    private AfcWithdrawSvc afcWithdrawSvc;

    @Resource
    private AfcAccountSvc accountSvc;
    @Resource
    private PntAccountSvc pntAccountSvc;

    @Resource
    private PntPointSvc pntPointSvc;

    @Override
    public IdRo apply(ApplyWithdrawTo to) {
        final IdRo ro = new IdRo();

        // 查询本月已提现次数
        final AfcWithdrawMo afcWithdrawMo = new AfcWithdrawMo();
        if (to.getAccountType() == null || to.getAccountType() != 2) {
            afcWithdrawMo.setAccountId(to.getApplicantId());
        } else {
            afcWithdrawMo.setAccountId(to.getApplicantOrgId());
        }

        _log.info("申请提现查询本月已提现次数的参数为：{}", afcWithdrawMo);
        WithdrawNumberForMonthRo withdrawNumberForMonthRo = afcWithdrawSvc.getWithdrawNumberForMonth(afcWithdrawMo);
        _log.info("申请提现查询本月已提现次数的返回值为：{}", withdrawNumberForMonthRo);

        // 判断是否是供应商提现1是个人2是供应商
        if (to.getAccountType() == null || to.getAccountType() != 2) {
            _log.info("申请提现查询积分账号信息的参数为：{}", to.getApplicantId());
            PntAccountMo pntAccountMo = pntAccountSvc.getById(to.getApplicantId());
            _log.info("申请提现查询积分账号信息的参数为：{}", pntAccountMo);
            if (pntAccountMo == null) {
                _log.error("申请提现查询积分账号时发现没有积分账号信息，申请的参数为：{}", to);
                ro.setResult(ResultDic.FAIL);
                ro.setMsg("没有发现积分账号信息");
                return ro;
            }
            if (withdrawNumberForMonthRo.getWithdrawNumber() > 0 && (pntAccountMo.getPoint()
                    .subtract(BigDecimal.valueOf(withdrawNumberForMonthRo.getSeviceCharge())))
                            .compareTo(BigDecimal.ZERO) < 0) {
                _log.error("申请提现时发现该账号的积分不足以抵扣本次提现，申请的信息为：{}", to);
                ro.setResult(ResultDic.FAIL);
                ro.setMsg("积分不足");
                return ro;
            }

        }
        return afcWithdrawSvc.apply(to);
    }

    /**
     * 同意提现(手动)
     */
    @Override
    public Ro ok(WithdrawOkTo to) {
        final Ro ro = new Ro();
        _log.info("查询提现帐号是组织还是个人的参数为：{}", to.getAccountId());
        AfcAccountMo afcAccountMo = accountSvc.getById(to.getAccountId());
        _log.info("查询提现帐号是组织还是个人的结果为：afcAccountMo-{}", afcAccountMo);
        final AfcWithdrawMo afcWithdrawMo = new AfcWithdrawMo();
        afcWithdrawMo.setAccountId(to.getAccountId());
        afcWithdrawMo.setWithdrawState((byte) 3);
        _log.info("确认提现成功查询提现次数和服务费的参数为：{}", afcWithdrawMo);
        final WithdrawNumberForMonthRo withdrawNumberForMonthRo = afcWithdrawSvc
                .getWithdrawNumberForMonth(afcWithdrawMo);
        _log.info("确认提现成功查询提现次数和服务费的返回值为：{}", withdrawNumberForMonthRo);
        if (afcAccountMo.getAccountType() == 1) {
            AddPointTradeTo addPointTradeTo = new AddPointTradeTo();
            addPointTradeTo.setAccountId(to.getAccountId());
            addPointTradeTo.setPointLogType((byte) PointLogTypeDic.VPAY_WITHDRAW.getCode());
            addPointTradeTo.setChangedTitile("大卖网络-用户提现");
            addPointTradeTo.setOrderId(to.getId());
            addPointTradeTo.setChangedPoint(BigDecimal.valueOf(withdrawNumberForMonthRo.getSeviceCharge()).negate());
            _log.info("确认提现添加一笔积分交易的参数为：{}", addPointTradeTo);
            Ro addPointTradeRo = pntPointSvc.addPointTrade(addPointTradeTo);
            _log.info("确认提现添加一笔积分交易的返回值为：{}", addPointTradeRo);
            if (addPointTradeRo == null) {
                _log.error("确认提现添加一笔积分交易出现异常，请求的参数为：{}", to);
                ro.setResult(ResultDic.FAIL);
                ro.setMsg("扣减服务费出现异常");
                return ro;
            }

            if (addPointTradeRo.getResult() != ResultDic.SUCCESS) {
                _log.error("确认提现添加一笔积分交易时出现错误，请求的参数为：{}", to);
                return addPointTradeRo;
            }
        }
        return afcWithdrawSvc.ok(to);
    }

}
