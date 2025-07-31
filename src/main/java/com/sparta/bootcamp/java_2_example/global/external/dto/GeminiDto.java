package com.sparta.bootcamp.java_2_example.global.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class GeminiDto {

  @Getter
  @RequiredArgsConstructor
  public static class Request {

    private final List<Content> contents;

    @JsonProperty("generationConfig")
    private final GenerationConfig generationConfig;

    public static Request from(String text) {
      Part part = new Part(text);
      Content content = new Content(List.of(part));
      GenerationConfig config = new GenerationConfig();
      return new Request(List.of(content), config);
    }
  }

  @Getter
  @RequiredArgsConstructor
  public static class Content {

    private final List<Part> parts;
  }

  @Getter
  @RequiredArgsConstructor
  public static class Part {

    private final String text;
  }

  @Getter
  public static class GenerationConfig {

    @JsonProperty("response_mime_type")
    private String responseMimeType = "application/json";
  }

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Response {

    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;
    private String modelVersion;
    private String responseId;
  }

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Candidate {

    private ResponseContent content;
    private String finishReason;
    private Double avgLogprobs;
  }

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ResponseContent {

    private List<ResponsePart> parts;
    private String role;
  }

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ResponsePart {

    private String text;
  }

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class UsageMetadata {

    private Integer promptTokenCount;
    private Integer candidatesTokenCount;
    private Integer totalTokenCount;
  }
}