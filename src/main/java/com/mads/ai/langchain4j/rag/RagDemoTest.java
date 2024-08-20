package com.mads.ai.langchain4j.rag;

import com.mads.ai.langchain4j.DocumentLoaderTest;
import com.mads.ai.langchain4j.config.ApiKeys;
import com.mads.ai.langchain4j.service.PersonExtractorService;
import com.mads.ai.langchain4j.service.RagService;
import com.mads.ai.langchain4j.tool.DateTool;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

/**
 * RAG:检索生成增强
 *  可以用来做 资料库方式的搜索
 */
@Slf4j
public class RagDemoTest {

    static ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
//            .modelName("gpt-4o-mini")
            .build();

    //向量模型
    static OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .build();

    //向量数据 存储
    static EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
            .host("127.0.0.1")
            .port(6393)
            .indexName("qa")
            .dimension(1536)//向量维度，写错了会写不进去
            .build();

    public static void main(String[] args) {

//        textToStore();
        //问答
        //第一步：提出问题
        String q = "余额提现最晚到账时间";
//        search1(q);
//        search2(q);
        search3(q);
    }

    /***
     * 组件
     * @param q
     */
    public static void search4(String q) {
        Query query = new Query(q);
        //问题拆解器，当前实现类是：按照一定规则将一个问题提炼成多个
        QueryTransformer queryTransformer = new ExpandingQueryTransformer(chatLanguageModel);
        Collection<Query> transform = queryTransformer.transform(query);

        //内容增强器
        ContentAggregator contentAggregator = new DefaultContentAggregator();

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5) // 最相似的5个结果
                .minScore(0.8) // 只找相似度在0.8以上的内容
                .build();

        //内容检索器，可以理解为对应过个表，比如说纪要查订单表，又要查财产表
        QueryRouter queryRouter = new LanguageModelQueryRouter(chatLanguageModel, Map.of(contentRetriever,"订单库", contentRetriever,"财产库"));
        for (ContentRetriever retriever : queryRouter.route(query)) {

        }
    }

    /**
     * 利用大模型来帮我们确认，当查出多条数据时，来帮我们组装更合理的答案
     *      先从向量数据库查出来模糊的多条数据作为大模型的数据样本
     *      让LLM来
     */
    public static void search3(String q) {
        //问题检索器
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5) // 最相似的5个结果
                .minScore(0.8) // 只找相似度在0.8以上的内容
                .build();

//        Query query = new Query(q);
        //从向量数据库查到很多条相关内容
//        List<Content> retrieve = contentRetriever.retrieve(query);

        //内容注入器，这里有问题查询的提示词模板，可以自定义
        ContentInjector contentInjector = new DefaultContentInjector();
        //第一种方式：拿到一个增强的用户信息，拼接提示词模板，下面使用增强器的方式最后也是这个逻辑
//        UserMessage userMessage = contentInjector.inject(retrieve, UserMessage.from(q));
        //让大模型帮我们分析得到更加合理的结果
//        Response<AiMessage> aiMessageResponse = chatLanguageModel.generate(userMessage);
//        System.out.println(aiMessageResponse.content());

        //内容增强器，
        ContentAggregator contentAggregator = new DefaultContentAggregator();

        //内容检路由器器，可以理解为管理多个数据源，比如：即要查订单表，又要查财产表，根据用户问题自动去找不同的数据源获取数据
        QueryRouter queryRouter = new DefaultQueryRouter(contentRetriever);

        //可以将一个问题 拆解成多个
        QueryTransformer queryTransformer = new ExpandingQueryTransformer(chatLanguageModel);


        //第二种方式
        //内容检索增强器，在源码中有体现
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)//
                .contentInjector(contentInjector)
                .contentAggregator(contentAggregator)
                .queryRouter(queryRouter)
                .queryTransformer(queryTransformer)
                .build();

        RagService ragService = AiServices.builder(RagService.class)
                .chatLanguageModel(chatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .tools(new DateTool())
                .build()
                ;
        String result = ragService.call(q);
        System.out.println(result);


    }

    //检索 第二种方式：使用内容检索器
    public static void search2(String q) {
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5) // 最相似的5个结果
                .minScore(0.8) // 只找相似度在0.8以上的内容
                .build();

        Query query = new Query(q);
        List<Content> retrieve = contentRetriever.retrieve(query);

        System.out.println(retrieve);
    }

    //检索 第一种方式：直接使用底层模型方法
    public static void search1(String q) {
        TextSegment textSegment = TextSegment.from(q);
        Embedding contentQ = embeddingModel.embed(textSegment).content();
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(contentQ, 1);
        System.out.println(relevant);
    }


    /***
     * 第一种方式，使用原生方法
     * 生成向量数据
     */
    public static void textToStore() {
        //第一步导入文件
        Path documentPath = DocumentLoaderTest.toPath("/meituan-qa.txt");
        log.info("Loading single document: {}", documentPath);
        Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());
//        System.out.println(document);

        //第二步 对内容切分
        DocumentSplitter splitter = new CustomerServiceDocumentSplitter();
        //拿到切分好的，这一步很关键，直接关系到后续检索的精度
        List<TextSegment> textSegments = splitter.split(document);
        System.out.println(textSegments);

        //第三步 向量化
        List<Embedding> embedList = embeddingModel.embedAll(textSegments).content();



        //将向量数据和文本对应 保存进数据库
        embeddingStore.addAll(embedList, textSegments);
    }
    /***
     * 第二种方式，使用管道方法
     * 生成向量数据
     */
    public static void textToStoreNew() {
        //第一步导入文件
        Path documentPath = DocumentLoaderTest.toPath("/meituan-qa.txt");
        log.info("Loading single document: {}", documentPath);
        Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());
//        System.out.println(document);

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(new CustomerServiceDocumentSplitter())
                .build();

        ingestor.ingest(document);
    }

}
