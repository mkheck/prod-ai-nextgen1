package com.thehecklers.prod_ai_nextgen1;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class AIController {
    private final ChatClient client;

    public AIController(ChatClient.Builder builder) {
        this.client = builder.build();
    }

    @GetMapping
    public String generateResponse(@RequestParam(defaultValue = "What is the meaning of life") String message) {
        return client.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/fun")
    public ChatResponse generateResponseWithATwist(
            @RequestParam(defaultValue = "What is the meaning of life") String message,
            @RequestParam(required = false) String celebrity) {

        ChatClient.ChatClientRequest request = client.prompt()
                .user(message);

        if (celebrity != null) {
            request = request.system(String.format("You respond in the style of %s.", celebrity));
        }

        return request
                .call()
                .chatResponse();
    }

    @GetMapping("/template")
    public String generateResponseFromTemplate(
            @RequestParam String type,
            @RequestParam String topic) {

        var template = new PromptTemplate("Tell me a {type} about {topic}",
                Map.of("type", type, "topic", topic));

        return client.prompt(template.create())
                .call()
                .content();
    }
}
