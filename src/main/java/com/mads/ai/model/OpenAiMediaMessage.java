package com.mads.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  "model": "gpt-4o",
 *  "messages": [
 *       {
 *         "role": "user",
 *         "content": [
 *           {
 *             "type": "text",
 *             "text": "What’s in this image?"
 *           },
 *           {
 *             "type": "image_url",
 *             "image_url": {
 *               "url": "ardwalk.jpg"
 *             }
 *           }
 *         ]
 *       }
 *     ],
 */
@Data
public class OpenAiMediaMessage {
    @Data
    public static class MediaTextMessage {
        private String type = "text";

        private String text;
    }

    @Data
    public static class MediaImageMessage {
        private String type = "image_url";

        private List<MediaImageModel> imageUrl;
    }

    @Builder
    @Data
    public static class MediaImageModel {
        private String url;//可以是图片的地址，也可以是base64的图像编码

        /**
         * 取值：
         *  low：将启用“低分辨率”模式。该模型将接收低分辨率 512px x 512px 版本的图像，并使用 85 个令牌的预算来表示该图像。这允许 API 返回更快的响应，并且对于不需要高细节的用例消耗更少的输入令牌
         *  high：将启用“高分辨率”模式，该模式首先允许模型首先查看低分辨率图像（使用 85 个标记），然后使用 170 个标记为每个 512px x 512px 图块创建详细裁剪
         */
        private String detail;
    }
}
