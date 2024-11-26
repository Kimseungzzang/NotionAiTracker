package com.notionAuto.notionAuto.Entity;


import jakarta.persistence.*;

@Entity
public class NotionPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 10000)
    private String content;

    // 기본 생성자, getter, setter
    public NotionPage() {}

    public NotionPage(String content) {

        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}