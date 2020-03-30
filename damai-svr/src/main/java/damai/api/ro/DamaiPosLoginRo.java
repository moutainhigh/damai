package damai.api.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import rebue.robotech.dic.ResultDic;

/**
 * 收银系统登录
 * 
 * @author you
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
public class DamaiPosLoginRo {
    /**
     * 返回值
     */
    private ResultDic result;

    /**
     * 返回值说明
     */
    private String msg;

    /**
     * 店铺信息（该参数只有在登录成功时才返回）
     */
    private DamaiShopRo shop;

    /**
     * 用户信息（该参数只有在登录成功时才返回）
     */
    private DamaiUserRo user;

}
