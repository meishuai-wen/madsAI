package com.mads.ai.langchain4j.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 段落切分器，这个要根据实际中 文本的格式来搞
 */
public class CustomerServiceDocumentSplitter implements DocumentSplitter {
    @Override
    public List<TextSegment> split(Document document) {
        List<TextSegment> segments = new ArrayList<>();
        String[] parts = split(document.text());
        for (String part : parts) {
            segments.add(TextSegment.from(part));
        }
        return segments;
    }
    public String[] split(String text) {
        return text.split("\\s*\\R\\s*\\R\\s*");
    }
}
