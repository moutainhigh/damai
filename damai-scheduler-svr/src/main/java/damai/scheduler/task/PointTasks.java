package damai.scheduler.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import damai.svr.feign.PointSvc;
import rebue.pnt.mo.PntAccountMo;
import rebue.pnt.svr.feign.PntAccountSvc;

/**
 * 长时间未购买的积分任务
 * @author lbl
 *
 */
@Component
public class PointTasks {
	
	private final static Logger _log = LoggerFactory.getLogger(PointTasks.class);

	@Resource
	private PntAccountSvc pntAccountSvc;
	
	@Resource
	private PointSvc pointSvc;
	
	/**
	 * 获取积分账号的条数
	 */
	@Value("${damai.pnt.pointAccountCount:1}")
	private Integer pointAccountCount;

	// 每天凌晨1点执行
	@Scheduled(cron = "${damai.pnt.executeLongTimeNotBuyTaskTime:0 15 10 * * ?}")
	public void executeTasks() {
		_log.info("定时执行需要长时间未购买的积分任务");
		try {
			int pageNum = 0;
			while (true) {
				try {
					_log.info("开始获取长时间未购买的积分任务的账户列表的参数为：pageNum-{}", pageNum);
					List<PntAccountMo> accountList = pntAccountSvc.pntAccountByLimitCountList(pageNum++, pointAccountCount);
					_log.info("开始获取长时间未购买的积分任务的账户列表的返回值为：{}", String.valueOf(accountList));
					if (accountList == null || accountList.isEmpty() || accountList.size() == 0) {
						break;
					}
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.DAY_OF_YEAR, -30);
					for (PntAccountMo pntAccountMo : accountList) {
						// 如果注册时间小于30天前的时间毫秒数则开始扣除积分
						if(pntAccountMo.getRegTime().compareTo(calendar.getTime()) == -1) {
							try {
								_log.info("执行长时间未购买的积分任务的参数为：{}", pntAccountMo);
								pointSvc.longTimeNotBuyGoodsDeductionsPoint(pntAccountMo);
								_log.info("执行长时间未购买的积分任务成功");
								Thread.sleep(1);
							} catch (Exception e) {
								_log.error("执行长时间未购买的积分任务失败，出现的异常为：{}", e);
							}
							Thread.sleep(1);
						}
					}
				} catch (Exception e) {
					_log.error("获取长时间未购买的积分任务的账户列表失败, 出现的异常为：{}", e);
				}
			}
		} catch (final RuntimeException e) {
			_log.error("执行长时间未购买的积分任务时发生系统中断, 异常为：{}", e);
		}
	}
}
