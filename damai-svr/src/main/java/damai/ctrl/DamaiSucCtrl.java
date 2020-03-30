package damai.ctrl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;

import rebue.afc.mo.AfcAccountMo;
import rebue.afc.ro.OrgWithdrawRo;
import rebue.afc.svr.feign.AfcAccountSvc;
import rebue.afc.svr.feign.AfcTradeSvc;
import rebue.ord.ro.OrdSettleRo;
import rebue.ord.svr.feign.OrdOrderSvc;
import rebue.suc.mo.SucOrgMo;
import rebue.suc.ro.OrgAccountRo;
import rebue.suc.svr.feign.SucOrgSvc;
import rebue.suc.svr.feign.SucUserSvc;

@RestController
public class DamaiSucCtrl {

    private final static Logger _log = LoggerFactory.getLogger(DamaiWxCtrl.class);

    @Resource
    private SucUserSvc sucUserSvc;

    @Resource
    private SucOrgSvc sucOrgSvc;

    @Resource
    AfcAccountSvc afcAccountSvc;

    @Resource
    AfcTradeSvc afcTradeSvc;

    @Resource
    OrdOrderSvc ordOrderSvc;

    @GetMapping("/damaisuc/listOrgAccount")
    public PageInfo<OrgAccountRo> listOrgAccount(final SucOrgMo mo, final int pageNum, final int pageSize) {
        _log.info("查询组织的参数SucOrgMo：{} pageNum-{} pageSize-{} ", mo, pageNum, pageSize);
        final PageInfo<OrgAccountRo> result = sucOrgSvc.listOrgAccount(mo, pageNum, pageSize);
        _log.info("查询组织的参数返回的结果是 {}", result);
        for (final OrgAccountRo item : result.getList()) {
            _log.info("获取供应商账户余额和订单结算信息循环开始----------------------------------------");
            // 获取供应商账户
            _log.info("获取供应商账户的参数是-id-{}", item.getSupplierId());
            final AfcAccountMo AfcAccountResult = afcAccountSvc.getById(item.getSupplierId());
            _log.info("获取供应商账户的结果是-AfcAccountResult-{}", AfcAccountResult);
            if (AfcAccountResult != null) {
                item.setBalance(AfcAccountResult.getBalance());
                item.setWithdrawing(AfcAccountResult.getWithdrawing());
            }
            // 获取供应商已经提现总额
            _log.info("获取供应商已经提现总额的参数是-id-{}", item.getSupplierId());
            final OrgWithdrawRo orgWithdrawRo = afcTradeSvc.getOrgWithdrawTotal(item.getSupplierId());
            _log.info("获取供应商已经提现总额的结果是-orgWithdrawRo-{}", orgWithdrawRo);
            if (orgWithdrawRo != null) {
                item.setWithdrawTotal(orgWithdrawRo.getWithdrawTotal());
            }
            // 获取供应商已经结算和待结算总成本
            _log.info("获取供应商订单待结算和已结算成本的参数为 SupplierId-{} ", item.getSupplierId());
            final OrdSettleRo ordSettleResult = ordOrderSvc.getSettleTotal(item.getSupplierId());
            _log.info("获取供应商订单待结算和已结算成本的结果为 ordSettleResult-{} ", ordSettleResult);
            if (ordSettleResult != null) {
                if (ordSettleResult.getAlreadySettle() != null) {
                    item.setAlreadySettle(ordSettleResult.getAlreadySettle());
                }
                if (ordSettleResult.getNotSettle() != null) {
                    item.setNotSettle(ordSettleResult.getNotSettle());
                }
            }
            _log.info("获取供应商账户余额和订单结算信息循环开结束+++++++++++++++++++++++++++++++++++++++");

        }
        return result;
    }

}
