package org.sunkengrotto.newsgrator.dto;

import java.util.List;

public class PagedArticlesDTO {
    public int index;
    public int size;
    public long total;
    public List<ArticleDTO> items;
}
