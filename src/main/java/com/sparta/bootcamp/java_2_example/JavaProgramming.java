package com.sparta.bootcamp.java_2_example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaProgramming {

  static class CategoryFlatDto {

    private Long id;
    private String name;
    private Long parentId;

    public CategoryFlatDto(Long id, String name, Long parentId) {
      this.id = id;
      this.name = name;
      this.parentId = parentId;
    }

    public Long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public Long getParentId() {
      return parentId;
    }

  }

  static class CategoryTreeDto {

    private Long id;
    private String name;
    private List<CategoryTreeDto> children;

    public CategoryTreeDto(Long id, String name) {
      this.id = id;
      this.name = name;
      this.children = new ArrayList<>();

    }

    public Long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public List<CategoryTreeDto> getChildren() {
      return children;
    }

    public void addChild(CategoryTreeDto child) {
      this.children.add(child);
    }
  }

  private static List<CategoryFlatDto> createTestData() {
    return Arrays.asList(
        new CategoryFlatDto(1L, "전자제품", null),
        new CategoryFlatDto(2L, "의류", null),
        new CategoryFlatDto(3L, "컴퓨터", 1L),
        new CategoryFlatDto(4L, "스마트폰", 1L),
        new CategoryFlatDto(5L, "상의", 2L),
        new CategoryFlatDto(6L, "노트북", 3L),
        new CategoryFlatDto(7L, "데스크톱", 3L),
        new CategoryFlatDto(8L, "안드로이드", 4L),
        new CategoryFlatDto(9L, "아이폰", 4L),
        new CategoryFlatDto(10L, "티셔츠", 5L),
        new CategoryFlatDto(11L, "셔츠", 5L)
    );
  }

  private static List<CategoryTreeDto> convertToTree(List<CategoryFlatDto> flatCategories) {
    Map<Long, CategoryTreeDto> nodeMap = new HashMap<>();

    for (CategoryFlatDto flat : flatCategories) {
      CategoryTreeDto treeNode = new CategoryTreeDto(flat.getId(), flat.getName());
      nodeMap.put(flat.getId(), treeNode);
    }

    List<CategoryTreeDto> rootNodes = new ArrayList<>();

    for (CategoryFlatDto flat : flatCategories) {
      CategoryTreeDto currentNode = nodeMap.get(flat.getId());

      if (flat.getParentId() == null) {
        rootNodes.add(currentNode);
      } else {
        CategoryTreeDto parentNode = nodeMap.get(flat.getParentId());
        if (parentNode != null) {
          parentNode.addChild(currentNode);
        }
      }
    }

    return rootNodes;
  }

  private static void printTree(List<CategoryTreeDto> nodes, int depth) {
    for (CategoryTreeDto node : nodes) {
      String indent = "  ".repeat(depth);

      System.out.println(indent + "├─ " + node.getName() + " (ID: " + node.getId() + ")");

      if (!node.getChildren().isEmpty()) {
        printTree(node.getChildren(), depth + 1);
      }
    }
  }

  public static void main(String[] args) {
    List<CategoryFlatDto> flatCategories = createTestData();

    List<CategoryTreeDto> treeCategories = convertToTree(flatCategories);

    System.out.println("=== 계층 구조 변환 결과 ===");
    printTree(treeCategories, 0);
  }
}