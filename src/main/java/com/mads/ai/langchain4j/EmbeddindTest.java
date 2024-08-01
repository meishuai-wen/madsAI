package com.mads.ai.langchain4j;

import com.mads.ai.langchain4j.config.ApiKeys;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;

import java.util.List;

/***
 * 向量
 */
public class EmbeddindTest {
    //第一种：这种是使用三方平台，请求接口的方式得到
    OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .build();
    /**
     * 第二种：本地向量库，小模型，计算的量小，本地玩可以，生产上不能用，数据会不准
     * <dependency>
     *     <groupId>dev.langchain4j</groupId>
     *     <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
     *     <version>0.33.0</version>
     *  </dependency>
     *  AllMiniLmL6V2EmbeddingModel在这个实现类中
     */

    /**
     * 内容转向量数组
     */
    public void embedTest() {
        //接口方式：内容转换成向量数组
        Response<Embedding> embed = embeddingModel.embed("Hello ,I am shuaishuai");
        System.out.println(embed.content());
    }

    //redis 存储向量
    public void redisStackStory() {
//        RedisStackContainer redis = new RedisStackContainer(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
//        redis.start();

        EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6393)
                .indexName("table_tag")
                .dimension(1536)//向量维度，写错了会写不进去
                .build();

//        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        //存入 redis
        embeddingStore.add(embeddingModel.embed("I like football.").content());

//        TextSegment segment2 = TextSegment.from("The weather is good today.");
        embeddingStore.add(embeddingModel.embed("The weather is good today.").content());

        Embedding queryEmbedding = embeddingModel.embed("The weather is good today.").content();

        //查询
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
        EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);

        System.out.println(embeddingMatch.score()); // 0.8144288659095
        System.out.println(embeddingMatch.embedded().text()); // I like football.

//        redis.stop();
    }


    public static void main(String[] args) {
        new EmbeddindTest().redisStackStory();
    }
}
