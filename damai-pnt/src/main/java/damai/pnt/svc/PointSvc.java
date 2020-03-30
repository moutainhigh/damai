package damai.pnt.svc;

import rebue.pnt.mo.PntAccountMo;

public interface PointSvc {

	/**
	 * 用户长时间未购买商品扣减积分
	 * @param accountMo
	 */
	void longTimeNotBuyGoodsDeductionsPoint(PntAccountMo accountMo);

}
