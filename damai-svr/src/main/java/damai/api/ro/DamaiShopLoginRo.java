package damai.api.ro;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import rebue.onl.ro.OnlSearchCategoryTreeRo;
import rebue.robotech.dic.ResultDic;
/**
 * 店铺账号登录返回信息
 * @author lbl
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
public class DamaiShopLoginRo {

	/**
	 * 返回值
	 */
	private ResultDic result;

	/**
	 * 返回值说明
	 */
	private String msg;

	/**
	 * 搜索分类信息（该参数只有在登录成功时才返回）
	 */
	private List<OnlSearchCategoryTreeRo> searchCategoryList;

	/**
	 * 店铺信息（该参数只有在登录成功时才返回）
	 */
	private DamaiShopRo shop;

	/**
	 * 门店账号信息（该参数只有在登录成功时才返回）
	 */
	private DamaiShopAccountRo user;
}
