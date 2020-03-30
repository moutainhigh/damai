package damai.ctrl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import damai.pnt.svc.PointSvc;
import rebue.pnt.mo.PntAccountMo;

@RestController
public class PointCtrl {

	private static final Logger _log = LoggerFactory.getLogger(PointCtrl.class);

	@Resource
	private PointSvc svc;

	/**
	 * 用户长时间未购买商品扣减积分
	 * 
	 * @param accountMo
	 */
	@PostMapping("/damai/pnt/deductionspoint")
	void longTimeNotBuyGoodsDeductionsPoint(@RequestBody PntAccountMo accountMo) {
		_log.info("用户长时间未购买商品扣减积分的请求参数为：{}", accountMo);
		svc.longTimeNotBuyGoodsDeductionsPoint(accountMo);
	}
}
