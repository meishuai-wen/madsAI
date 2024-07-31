package com.mads.ai.langchain4j.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mads.ai.util.JsonUtil;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 模拟数据库存储上下文消息的方式，这里也可以换成自己的向量数据库
 */
@Service
public class PersistentChatMemoryStoreService implements ChatMemoryStore  {
    private final DB db = DBMaker.fileDB("chat-memory.db").transactionEnable().make();
    private final Map<String, String> map = db.hashMap("messages", Serializer.STRING, Serializer.STRING).createOrOpen();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = map.get((String) memoryId);
        return JsonUtil.toObject(json, new TypeReference<List<ChatMessage>>() {}).get();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String json = JsonUtil.toJson(messages).get();
        map.put((String) memoryId, json);
        db.commit();
    }

    @Override
    public void deleteMessages(Object memoryId) {
        map.remove((String) memoryId);
        db.commit();
    }
}
