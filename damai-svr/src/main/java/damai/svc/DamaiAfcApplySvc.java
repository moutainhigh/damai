package damai.svc;

import rebue.afc.to.ApplyWithdrawTo;
import rebue.afc.withdraw.to.WithdrawOkTo;
import rebue.robotech.ro.IdRo;
import rebue.robotech.ro.Ro;

public interface DamaiAfcApplySvc {
    /**
     * acf 提现申请
     * 
     * @param to
     * @return
     */
    IdRo apply(ApplyWithdrawTo to);

    /**
     * 确认提现成功（手动）
     */
    Ro ok(WithdrawOkTo to);

}
