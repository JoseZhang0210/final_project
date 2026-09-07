package com.hotel.model.dto;

import java.io.File;
import java.util.List;

import lombok.Data;

@Data
public class EmailDTO {
    private String to; // 收件人
    private String subject; // 主旨
    private String content; // 內容 (支援純文字或 HTML)
    private boolean isHtml = true; // 是否為 HTML 信件 (預設為 true)
    private List<File> attachments; // 附件列表 (選填，留空代表無附件)

    // 建構子 (最常用的快速建立方式)
    public EmailDTO(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    public EmailDTO(String to, String subject, String content, boolean isHtml) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.isHtml = isHtml;
    }
}
