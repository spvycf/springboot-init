package com.talkman.saas.common.code;

import com.talkman.saas.utils.StringUtils;
import lombok.Data;

import java.util.List;

/**
 * @author doger.wang
 * @date 2019/10/21 11:39
 */
@Data
public class PageInfo<T> {


    List<T> records;
    long total;
    long size;
    long current;
    String[] orders;
    boolean searchCount = true;
    long pages;

    public PageInfo(List<T> records, long total, long size, long current, long pages) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;

        this.pages = pages;
    }


    /*add
    自行计算的list处理
     */
    public PageInfo(List<T> records, int page, int row) {
        int total = records.size();
        this.total = total;
        this.size = row;
        this.current = page;
        this.pages = (total % row) + 1;

        //计算list
        if (StringUtils.isEmptyList(records)) {
            this.records = records;
        } else {
            //处理截取
            int start = (page - 1) * row;
            if (start > total) {
                this.records = null;
                return;
            }
            int end = page * row;
            if (end > total) {
                end = total;
            }
            this.records = records.subList(start, end);

        }

    }

    public PageInfo() {

    }
}
