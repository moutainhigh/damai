package damai.api.ro;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * 搜索分类信息
 * @author lbl
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
public class DamaiSearchCategoryTreeRo {

	/**
	 * 分类ID
	 *
	 * 数据库字段: SLR_SEARCH_CATEGORY.ID
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private Long id;

	/**
	 * 卖家ID
	 *
	 * 数据库字段: SLR_SEARCH_CATEGORY.SELLER_ID
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private Long sellerId;

	/**
	 * 店铺ID
	 *
	 * 数据库字段: SLR_SEARCH_CATEGORY.SHOP_ID
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private Long shopId;

	/**
	 * 分类名称
	 *
	 * 数据库字段: SLR_SEARCH_CATEGORY.NAME
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String name;

	/**
	 * 分类编码
	 *
	 * 数据库字段: SLR_SEARCH_CATEGORY.CODE
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String code;

	/**
	 * 分类备注
	 *
	 * 数据库字段: SLR_SEARCH_CATEGORY.REMARK
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String remark;

	/**
	 * 是否启用
	 *
	 * 数据库字段: SLR_SEARCH_CATEGORY.IS_ENABLED
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private Boolean isEnabled;

	/**
	 * 分类图片
	 *
	 * 数据库字段: SLR_SEARCH_CATEGORY.IMAGE
	 *
	 * @mbg.generated 自动生成，如需修改，请删除本行
	 */
	private String image;

	/**
	 * 子级搜索分类
	 */
	private List<DamaiSearchCategoryTreeRo> list;
}
