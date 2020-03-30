package damai.api.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * 门店账号信息
 * 
 * @author lbl
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
public class DamaiShopAccountRo {

	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 用户是否是测试者
	 */
	private Boolean isTester;
	/**
	 * 用户WxOpenID
	 */
	private String userWxOpenId;
	/**
	 * 用户WxUnionID
	 */
	private String userWxUnionId;
	/**
	 * 用户组织id
	 */
	private Long orgId;
	/**
	 * 用户昵称
	 */
	private String nickname;
	/**
	 * 用户头像
	 */
	private String face;
}
