package blossom.project.towelove.community.req;

import blossom.project.towelove.community.entity.posts.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 评论更新请求
 * 用于更新已有的评论
 * 
 * @author: ZhangBlossom
 * @date: 2024-06-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentsUpdateRequest {

    @NotNull(message = "评论ID不能为空")
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "文章ID不能为空")
    private Long postId;

    @NotNull(message = "用户信息不能为空")
    private UserInfo userInfo;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentId;

    private List<String> showTags;
}
