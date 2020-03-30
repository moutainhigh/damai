package damai.svr.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import rebue.pnt.mo.PntAccountMo;
import rebue.sbs.feign.FeignConfig;

@FeignClient(name = "damai-svr", configuration = FeignConfig.class)
public interface PointSvc {

	/**
	 * 用户长时间未购买商品扣减积分
	 * 
	 * @param accountMo
	 */
	@PostMapping("/damai/pnt/deductionspoint")
	void longTimeNotBuyGoodsDeductionsPoint(@RequestBody PntAccountMo accountMo);
}
