package com.mads.ai.langchain4j;

import com.mads.ai.langchain4j.config.ApiKeys;
import com.mads.ai.langchain4j.model.CreateRecipePrompt;
import com.mads.ai.langchain4j.model.PersonModel;
import com.mads.ai.langchain4j.model.RecipeModel;
import com.mads.ai.langchain4j.service.AssistantService;
import com.mads.ai.langchain4j.service.ChefService;
import com.mads.ai.langchain4j.service.PersonExtractorService;
import com.mads.ai.langchain4j.service.TextTranslatorService;
import com.mads.ai.langchain4j.tool.CalculatorTool;
import com.mads.ai.langchain4j.tool.DateTool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.service.AiServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AiServiceTest {

    static ChatLanguageModel model = OpenAiChatModel.builder()
//            .baseUrl(ApiKeys.BASE_URL)
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .logRequests(true)
            .logResponses(true)
            .timeout(Duration.ofSeconds(60))
            .build();

    static ChatLanguageModel zipuModel = ZhipuAiChatModel.builder()
            .apiKey("48ee4de20f89229f0b2a6e8244b0c7c6.PK0NRlPAupA4imAz")
            .build();


    /**
     * 从输入文本中提取有效信息封装成 Person对象
     * 应用场景:
     * 可以对用户模糊描述提取有用的信息, 进行精确的业务处理
     * 对文档提取特定的数据进行业务处理
     */
    public void createPersonFromText() {
//        AiServices.builder(PersonExtractorService.class)
//                .chatLanguageModel(model)
//                .moderationModel()//设置 违规处理模型，在使用@Moderate注解时为必须参数
//                .build();
        PersonExtractorService extractor = AiServices.create(PersonExtractorService.class, model);

        String text = "In 1968, amidst the fading echoes of Independence Day, "
                + "a child named John arrived under the calm evening sky. "
                + "This newborn, bearing the surname Doe, marked the start of a new journey.";

        PersonModel person = extractor.extractPersonFrom(text);

        System.out.println(person); // Person { firstName = "John", lastName = "Doe", birthDate = 1968-07-04 }
    }

    /**
     * 专业的翻译小助手
     */
    public void translatorTest() {
        TextTranslatorService utils = AiServices.create(TextTranslatorService.class, model);

        String translation = utils.translate("Hello, how are you?", "en");
        System.out.println(translation); // Ciao, come stai?
    }

    /**
     * 内容要点总结
     */
    public void textSummarize() {
        String text = "AI, or artificial intelligence, is a branch of computer science that aims to create "
                + "machines that mimic human intelligence. This can range from simple tasks such as recognizing "
                + "patterns or speech to more complex tasks like making decisions or predictions.";
        TextTranslatorService utils = AiServices.create(TextTranslatorService.class, model);

        List<String> bulletPoints = utils.summarize(text, 3);
        bulletPoints.forEach(System.out::println);
    }

    //聊天记忆，上下文，默认使用功能内存的方式存储
    public void memoryTest() {
        AssistantService assistant = AiServices.builder(AssistantService.class)
                .chatLanguageModel(model)
                /**
                 * 告诉框架按照id方式去找历史消息，这样就可以解决因为淘汰机制造成上下文理解偏差的问题
                 *  现有历史消息两条，消息淘汰数量为1
                 *      id 1 = shuaishuai
                 *      id 2 = meimei
                 *  现在问：我是谁，如果不指定id，llm会认为我是meimei,如果指定了id,LLM就会以此来标示我是谁了
                 *  这个可以用来做不同App的管理员账号，比如 three="Three Team", monkey="逗比猴子"
                 */
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();

        System.out.println(assistant.chat(1, "Hello, my name is shuaishuai"));
        // Hi Klaus! How can I assist you today?

        System.out.println(assistant.chat(2, "Hello, my name is meimei"));
        // Hello Francine! How can I assist you today?

        System.out.println(assistant.chat(1, "What is my name?"));
        // Your name is Klaus.

        System.out.println(assistant.chat(2, "What is my name?"));
    }

    /**
     * function
     * 计算器
     * openAI测试版本不支持function功能
     */
    public void calculatorTool() {
        AssistantService assistant = AiServices.builder(AssistantService.class)
                //openAI 测试版本不支持function功能，所以这里使用了智谱的
                .chatLanguageModel(zipuModel)
                .tools(new CalculatorTool())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

//        String question = "What is the square root of the sum of the numbers of letters in the words \"hello\" and \"world\"?";
        String question = "What is the product of the numbers of letters in the words \"hello\" and \"world\"?";

        String answer = assistant.chatCalculator(question);

        System.out.println("-1-" + answer);

        AssistantService assistant1 = AiServices.builder(AssistantService.class)
                //openAI 测试版本不支持function功能，所以这里使用了智谱的
                .chatLanguageModel(zipuModel)
                .tools(new DateTool())
                .build();

        AiMessage answer1 = assistant1.chat1("What's the date today");
        System.out.println("-2-" + answer1);
    }

    /**
     * function
     * 直接使用model底层的mode.generate(messages)方法， 这个可以方便理解整体的流程，时机开发中使用上面的即可
     *
     * @throws NoSuchMethodException
     */
    public void modelToolTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //单独指定一个方法, 需要手动封装ToolSpecification
        ToolSpecification nowTimeTool = ToolSpecifications.toolSpecificationFrom(DateTool.class.getMethod("nowTime"));

        UserMessage userMessage = UserMessage.userMessage("What's the date today");

        Response<AiMessage> generate = zipuModel.generate(Collections.singletonList(userMessage), nowTimeTool);
        AiMessage content = generate.content();
        System.out.println("-1-"+content);

        if(content.hasToolExecutionRequests()) {
            for (ToolExecutionRequest toolExecutionRequest : content.toolExecutionRequests()) {
                String methodName = toolExecutionRequest.name();
                //注意：如果使用下面方式调用，工具方法要是 static修饰，如果使用注入的方式则没事
                Method method = DateTool.class.getMethod(methodName);
                String nowTimeResult = (String)method.invoke(null);

                ToolExecutionResultMessage toolResultMessage = ToolExecutionResultMessage.from(toolExecutionRequest.id(), methodName, nowTimeResult);
                Response<AiMessage> generate1 = zipuModel.generate(userMessage, content, toolResultMessage);
                System.out.println("-finial-"+generate1.content().text());
            }
        }
    }

    /**
     * 注解 @StructuredPrompt("")
     */

    public void chefTest() {
        ChefService chef = AiServices.create(ChefService.class, model);

        RecipeModel recipe = chef.createRecipeFrom("cucumber", "tomato", "feta", "onion", "olives");

        System.out.println(recipe);

        CreateRecipePrompt prompt = new CreateRecipePrompt();
        prompt.setDish("salad");
        prompt.setIngredients(Arrays.asList("cucumber", "tomato", "feta", "onion", "olives"));

        RecipeModel anotherRecipe = chef.createRecipe(prompt);
        System.out.println(anotherRecipe);
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        new AiServiceTest().calculatorTool();
    }

}
