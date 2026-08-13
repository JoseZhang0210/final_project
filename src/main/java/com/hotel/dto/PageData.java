package com.hotel.dto;

import java.util.List;

/**
 * 分頁回應資料封裝類
 */
public class PageData<T> {

    private List<T> content; // 當前頁的內容
    private long totalElements; // 總記錄數
    private int totalPages; // 總頁數
    private int currentPage; // 當前頁碼（0-based）
    private int pageSize; // 每頁記錄數

    public PageData() {
    }

    public PageData(List<T> content, long totalElements, int totalPages, int currentPage, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    // Getter & Setter
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
