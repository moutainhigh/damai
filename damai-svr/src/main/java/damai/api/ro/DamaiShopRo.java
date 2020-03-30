package damai.api.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * 店铺信息
 * @author lbl
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
public class DamaiShopRo {

	/**
	 * 店铺ID
	 *
	 * 数据库字段: SLR_SHOP.ID
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private Long id;

	/**
	 * 卖家ID(=卖家组织ID)
	 *
	 * 数据库字段: SLR_SHOP.SELLER_ID
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private Long sellerId;

	/**
	 * 店铺名称
	 *
	 * 数据库字段: SLR_SHOP.SHOP_NAME
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String shopName;

	/**
	 * 店铺简称
	 *
	 * 数据库字段: SLR_SHOP.SHORT_NAME
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String shortName;

	/**
	 * 详细地址
	 *
	 * 数据库字段: SLR_SHOP.ADDERSS
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String adderss;

	/**
	 * 经度
	 *
	 * 数据库字段: SLR_SHOP.LONGITUDE
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String longitude;

	/**
	 * 纬度
	 *
	 * 数据库字段: SLR_SHOP.LATITUDE
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String latitude;

	/**
	 * 联系方式
	 *
	 * 数据库字段: SLR_SHOP.CONTACT
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String contact;

	/**
	 * 是否启用
	 *
	 * 数据库字段: SLR_SHOP.IS_ENABLED
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private Boolean isEnabled;
}
