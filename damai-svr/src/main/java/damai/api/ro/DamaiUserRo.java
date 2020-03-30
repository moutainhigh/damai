package damai.api.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class DamaiUserRo {

    /**
     * 用户ID
     *
     */
    private Long id;

    /**
     * 公司/组织id
     *
     */
    private Long orgId;

    /**
     * 登录账号
     *
     */
    private String loginName;

    /**
     * 昵称
     *
     */
    private String nickname;

    /**
     * 头像
     *
     */
    private String face;

    /**
     * 手机
     *
     */
    private String mobile;

    /**
     * 是否已验证手机号码
     *
     */
    private Boolean isVerifiedMobile;

    /**
     * QQ的ID
     *
     */
    private String qqId;

    /**
     * QQ的openid
     *
     */
    private String qqOpenid;

    /**
     * QQ昵称
     *
     */
    private String qqNickname;

    /**
     * QQ头像
     *
     */
    private String qqFace;

    /**
     * 微信的ID
     *
     */
    private String wxId;

    /**
     * 微信openid
     *
     */
    private String wxOpenid;

    /**
     * 微信昵称
     *
     */
    private String wxNickname;

    /**
     * 微信头像
     *
     */
    private String wxFace;

    /**
     * 是否测试者
     *
     */
    private Boolean isTester;

    /**
     * 是否锁定
     *
     */
    private Boolean isLock;

    /**
     * 推广者ID
     *
     */
    private Long promoterId;

    /**
     * 修改时间戳
     *
     */
    private Long modifiedTimestamp;

    /**
     * 记录用户所属领域(也可称为群组)
     *
     */
    private String domainId;

}
