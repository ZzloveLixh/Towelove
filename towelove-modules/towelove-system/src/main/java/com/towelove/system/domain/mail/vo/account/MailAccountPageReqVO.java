package com.towelove.system.domain.mail.vo.account;


import com.towelove.common.core.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - 邮箱账号分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailAccountPageReqVO extends PageParam {

    @Schema(description = "邮箱", required = true, example = "yudaoyuanma@123.com")
    private String mail;

    @Schema(description = "用户名" , required = true , example = "yudao")
    private String username;

}