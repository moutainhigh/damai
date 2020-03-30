package damai.pnt.svc.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import damai.pnt.dic.PointLogTypeDic;
import damai.pnt.svc.PointSvc;
import rebue.ord.mo.OrdOrderMo;
import rebue.ord.svr.feign.OrdOrderSvc;
import rebue.pnt.mo.PntAccountMo;
import rebue.pnt.mo.PntPointLogMo;
import rebue.pnt.svr.feign.PntPointLogSvc;
import rebue.pnt.svr.feign.PntPointSvc;
import rebue.pnt.to.AddPointTradeTo;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.Ro;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class PointImpl implements PointSvc {

    private static final Logger _log = LoggerFactory.getLogger(PointImpl.class);

    @Resource
    private OrdOrderSvc ordOrderSvc;

    @Resource
    private PntPointSvc pntPointSvc;

    @Resource
    private PntPointLogSvc pntPointLogSvc;

    /**
     * 30天未购买的积分扣率
     */
    @Value("${damai.pnt.thirtyDaysPointDeductionRate:0.3}")
    private BigDecimal thirtyDaysPointDeductionRate;

    /**
     * 60天未购买的积分扣率
     */
    @Value("${damai.pnt.sixtyDaysPointDeductionRate:0.6}")
    private BigDecimal sixtyDaysPointDeductionRate;

    /**
     * 90天未购买的积分扣率
     */
    @Value("${damai.pnt.ninetyDaysPointDeductionRate:1}")
    private BigDecimal ninetyDaysPointDeductionRate;

    /**
     * 用户长时间未购买商品扣减积分流程： *
     * --------------------修改扣减积分流程-2019-06-01----------------------------
     * 1：判断用户账号积分是否为0，如果为0则退出扣减积分流程 2：查询用户查询用户最新一条订单信息 3-1 用户有订单信息：
     * 1：查询用户最新一条订单的支付时间 2：根据支付时间，计算出未购买商品的天数，获取扣减积分的积分日志类型
     * 3：根据订单id和积分日志类型查询积分日志，如果查询出扣除记录则不扣减积分，反之扣减积分 3-2 用户没有订单信息： 1：查询用户的注册时间，
     * 2：根据注册时间，计算出未购买商品的天数，获取扣减积分的积分日志类型 3：根据积分日志类型查询积分日志，如果查询出扣除记录则不扣减积分，反之扣减积分
     * 
     * 注：如果计算后的积分小于1，则直接清0 注2：未购买商品天数大于30天且小于60,扣除当前积分的30%；
     * 未购买商品天数大于60天且小于90，扣除当前积分的60%； 未购买商品天数大于90，扣除全部积分
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void longTimeNotBuyGoodsDeductionsPoint(PntAccountMo accountMo) {
        _log.info("用户长时间未购买商品扣减积分的参数为：{}", accountMo);
        if (accountMo == null || accountMo.getId() == null) {
            _log.error("用户长时间未购买商品扣减积分时发现请求的参数有误，请求的参数为：{}", accountMo);
            return;
        }

        if (accountMo.getPoint().compareTo(BigDecimal.ZERO) < 0) {
            _log.error("用户长时间未购买商品扣减积分时发现该用户的积分小于或者等于0，请求的参数为：{}", accountMo);
            return;
        }

        // 查询用户最新一条订单信息
        OrdOrderMo orderMo = ordOrderSvc.getLatestOneByUserId(accountMo.getId());
        _log.info("查询用户最新一条订单信息返回值为：{}", orderMo);
        // 如果用户有订单信息取支付时间为购买商品的时间,反之取注册时间
        Date buyTime = orderMo != null ? orderMo.getPayTime() : accountMo.getRegTime();
        _log.info("用户购买商品的时间为-{},时间戳为-{}", buyTime, buyTime.getTime());

        // 计算上次购买商品和现在相隔的天数
        Date now = new Date();

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(now);

        Calendar buyTimeCalendar = Calendar.getInstance();
        buyTimeCalendar.setTime(buyTime);

        long nowMillisecond = nowCalendar.getTimeInMillis();
        long buyTimeMillisecond = buyTimeCalendar.getTimeInMillis();
        int betweenDays = (int) ((nowMillisecond - buyTimeMillisecond) / (1000 * 60 * 60 * 24));

        // 两日期的毫秒数之差 < 一天的毫秒数(1000*60*60*24)时,即使两日期跨了一天，强制类型转换后结果也会变成0
        nowCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);// 使nowCalendar减去这些天数，将问题转换为两时间的毫秒数之差不足一天的情况
        nowCalendar.add(Calendar.DAY_OF_MONTH, -1);// 再使nowCalendar减去1天
        if (buyTimeCalendar.get(Calendar.DAY_OF_MONTH) == nowCalendar.get(Calendar.DAY_OF_MONTH))// 比较两日期的DAY_OF_MONTH是否相等
            betweenDays += 1; // 相等说明跨天了
        else
            betweenDays += 0; // 不相等说明未跨天

        _log.info("用户上次购买商品和现在相隔的时间为-{}", betweenDays);

        // 获取积分日志字典和积分扣率
        PointLogTypeDic pointLogType = null;
        BigDecimal PointDeductionRate = BigDecimal.ZERO;
        int UnpurchasedTime = 0;
        if (betweenDays >= 31 && betweenDays < 61) {
            pointLogType = PointLogTypeDic.THIRTY_DAY_NOT_BUY;
            UnpurchasedTime = 30;
            PointDeductionRate = thirtyDaysPointDeductionRate;
        } else if (betweenDays >= 61 && betweenDays < 91) {
            pointLogType = PointLogTypeDic.SIXTY_DAY_NOT_BUY;
            UnpurchasedTime = 60;
            PointDeductionRate = sixtyDaysPointDeductionRate;
        } else if (betweenDays >= 91) {
            pointLogType = PointLogTypeDic.NINETY_DAY_NOT_BUY;
            UnpurchasedTime = 90;
            PointDeductionRate = ninetyDaysPointDeductionRate;
        }

        // 如果没有大于或者等于三十天则返回
        _log.info("计算出来的积分扣率和积分日志类型为 pointLogType-{},betweenDays-{}", pointLogType, betweenDays);
        if (betweenDays < 31) {
            _log.info("注册时间或者是购买商品的最新时间没有超过三十天，不进行扣除积分");
            return;
        }

        // 添加积分日志
        PntPointLogMo pntPointLogMo = new PntPointLogMo();
        BigDecimal changedPoint = BigDecimal.ONE;
        String changedDetail = "";
        if (orderMo != null) {
            // 根据账号、订单id和积分日志类型查询积分日志，如果查询出扣除记录则不扣减积分，反之扣减积分
            pntPointLogMo.setAccountId(accountMo.getId());
            pntPointLogMo.setPointLogType((byte) pointLogType.getCode());
            pntPointLogMo.setOrderId(orderMo.getId());
            int result = pntPointLogSvc.countByIdAndOrderId(pntPointLogMo);
            if (result != 0) {
                _log.info("积分日志已存在,该条扣除积分参数-{}", pntPointLogMo);
                return;
            }
            _log.info("积分日志未存在，开始添加积分日志");
            changedPoint = accountMo.getPoint().multiply(PointDeductionRate);
            _log.info("乘值：{},{},{}", changedPoint, accountMo.getPoint(), PointDeductionRate);

            changedDetail = "超过" + UnpurchasedTime + "天未成功购买商品，积分已扣除" + changedPoint + "分";
            addLongTimeNotBuyPointTrade(accountMo.getId(), changedPoint, changedDetail, orderMo.getId(), pointLogType);
        } else {
            // 根据账号id和积分日志类型查询积分日志，如果查询出扣除记录则不扣减积分，反之扣减积分
            pntPointLogMo.setAccountId(accountMo.getId());
            pntPointLogMo.setPointLogType((byte) pointLogType.getCode());
            pntPointLogMo.setOrderId(null);
            _log.info(" 根据账号id和积分日志类型查询积分是否已经被扣除过的参数为 pntPointLogMo-{}", pntPointLogMo);
            int result = pntPointLogSvc.countByIdAndOrderId(pntPointLogMo);
            if (result != 0) {
                _log.info("积分日志已存在,该条扣除积分参数-{}", pntPointLogMo);
                return;
            }
            _log.info("积分日志未存在，开始添加积分日志");
            changedPoint = accountMo.getPoint().multiply(PointDeductionRate);
            _log.info("乘值：{},{},{}", changedPoint, accountMo.getPoint(), PointDeductionRate);
            changedDetail = "超过" + UnpurchasedTime + "天未成功购买商品，积分已扣除" + changedPoint + "分";
            addLongTimeNotBuyPointTrade(accountMo.getId(), changedPoint, changedDetail, null, pointLogType);
        }
    }

    /**
     * 添加一笔长时间未购买商品的积分交易
     * 
     * @param accountId
     * @param changedPoint
     * @param changedDetail
     */
    public void addLongTimeNotBuyPointTrade(Long accountId, BigDecimal changedPoint, String changedDetail, Long orderId,
            PointLogTypeDic pointLogType) {
        _log.info("添加一笔长时间未购买商品的积分交易的请求参数为：accountId-{}, changedPoint-{}, changedDetail-{}， orderId-{}", accountId,
                changedPoint, changedDetail, orderId);

        AddPointTradeTo addPointTradeTo = new AddPointTradeTo();
        addPointTradeTo.setAccountId(accountId);
        addPointTradeTo.setPointLogType((byte) pointLogType.getCode());
        addPointTradeTo.setChangedPoint(changedPoint.negate());
        addPointTradeTo.setOrderId(orderId);
        addPointTradeTo.setOrderDetailId(null);
        addPointTradeTo.setChangedTitile("大卖网络-扣减积分");
        addPointTradeTo.setChangedDetail(changedDetail);
        addPointTradeTo.setModifedTimestamp(new Date().getTime());
        _log.info("添加一笔长时间未购买商品的积分交易的参数为：{}", addPointTradeTo);
        Ro addPointTradeRo = null;
        try {
            addPointTradeRo = pntPointSvc.addPointTrade(addPointTradeTo);
        } catch (DuplicateKeyException e) {
            _log.warn("添加一笔长时间未购买商品的积分交易时出现重复添加的情况，请求的参数为：{}", addPointTradeTo);
            return;
        }
        _log.info("添加一笔长时间未购买商品的积分交易的返回值为：{}", addPointTradeRo);
        if (addPointTradeRo.getResult() != ResultDic.SUCCESS) {
            _log.error("添加一笔长时间未购买商品的积分交易时出现错误，请求的参数为：{}", addPointTradeTo);
            throw new RuntimeException("添加积分交易失败");
        }
    }
}
