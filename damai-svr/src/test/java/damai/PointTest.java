package damai;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import rebue.pnt.mo.PntAccountMo;
import rebue.wheel.OkhttpUtils;

public class PointTest {

	private final String hostUrl = "http://127.0.0.1:9081";
	
	@Test
	public void longTimeNotBuyGoodsDeductionsPointTest() throws IOException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		PntAccountMo mo = new PntAccountMo();
		mo.setId(1L);
		mo.setPoint(BigDecimal.valueOf(11));
		mo.setIncome(BigDecimal.valueOf(0.02331));
		mo.setTotalIncome(BigDecimal.valueOf(0.02331));
		mo.setModifiedTimestamp(1547603135317L);
		mo.setRegTime(new Date(sdf.parse("2019-01-07 00:00:00").getTime()));
		System.out.println(mo);
		String str = OkhttpUtils.postByJsonParams(hostUrl + "/damai/pnt/deductionspoint", mo);
		System.out.println(str);
	}
}
