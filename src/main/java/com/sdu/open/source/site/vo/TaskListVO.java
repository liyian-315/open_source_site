package com.sdu.open.source.site.vo;

import lombok.Data;
import java.util.List;

@Data
public class TaskListVO {
    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 任务列表数据
     */
    private List<TaskVO> taskList;
}