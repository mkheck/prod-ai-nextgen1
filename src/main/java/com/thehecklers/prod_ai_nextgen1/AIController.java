package com.thehecklers.prod_ai_nextgen1;

import org.springframework.ai.azure.openai.AzureOpenAiImageModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class AIController {
    private final ChatClient client;
    private final AzureOpenAiImageModel azureOpenaiImageModel;

    public AIController(ChatClient.Builder builder, AzureOpenAiImageModel azureOpenaiImageModel) {
        this.client = builder.build();
        this.azureOpenaiImageModel = azureOpenaiImageModel;
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

    @GetMapping("/entity")
    public AIAnswer generateResponseWithEntity(@RequestParam String message) {
        return client.prompt()
                .user(message)
                .call()
                .entity(AIAnswer.class);
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

    @GetMapping("/image")
    public ImageResponse generateImageResponse(@RequestParam String description) {
        return azureOpenaiImageModel.call(
                new ImagePrompt(description)); //,
//                        AzureOpenAiImageOptions.builder()
//                                .withHeight(1024)
//                                .withWidth(1024).build()));
    }
}
