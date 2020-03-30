package damai.pnt.util;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 积分算法工具类
 * @author lbl
 * 2018年12月21日10:07:37
 */
public class PntPointsAlgorithmUtils {

	private static final Logger _log = LoggerFactory.getLogger(PntPointsAlgorithmUtils.class);
	
	/**
	 * 返佣积分算法（非首单） = 销售价 / 2 - 成本价
	 * @param salePrice 销售价
	 * @param costPrice 成本价
	 * @return
	 */
	public static BigDecimal commissionPoints(BigDecimal salePrice, BigDecimal costPrice) {
		_log.info("计算积分的参数为：salePrice={}, costPrice={}", salePrice, costPrice);
		BigDecimal points = salePrice.divide(BigDecimal.valueOf(2), 4, BigDecimal.ROUND_HALF_UP).subtract(costPrice);
		_log.info("计算积分的返回值为：{}", points);
		if (points.compareTo(BigDecimal.ONE) < 0) {
			return BigDecimal.ONE;
		} else {
			points = points.setScale(0, BigDecimal.ROUND_HALF_UP);
		}
		_log.info("计算积分的最后返回值为：{}", points);
		return points;
	}
	
	/**
	 * 首单积分算法 = 销售价 / 2
	 * @param salePrice 销售价
	 * @return
	 */
	public static BigDecimal firstOrderPoints(BigDecimal salePrice) {
		_log.info("计算首单积分的参数为：salePrice={}", salePrice);
		BigDecimal points = salePrice.divide(BigDecimal.valueOf(2), 4, BigDecimal.ROUND_HALF_UP);
		_log.info("计算首单积分的返回值为：{}", points);
		if (points.compareTo(BigDecimal.ONE) < 0) {
			return BigDecimal.ONE;
		} else {
			points = points.setScale(0, BigDecimal.ROUND_HALF_UP);
		}
		return points;
	}
}
