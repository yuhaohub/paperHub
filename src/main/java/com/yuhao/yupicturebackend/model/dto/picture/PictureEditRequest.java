package com.yuhao.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureEditRequest implements Serializable {
  
    /**  
     * id  
     */  
    private Long id;  
  
    /**  
     * 图片名称  
     */  
    private String name;  
  
    /**  
     * 简介  
     */  
    private String introduction;  
  
    /**  
     * 分类  
     */  
    private String category;  
  
    /**  
     * 标签  
     */  
    private List<String> tags;
    /**
     * 所属空间id
     */
    private Long spaceId;
    /**
     * 兼容公共空间 spaceId 为 null 的数据
     */
    private boolean nullSpaceId;
  
    private static final long serialVersionUID = 1L;  
}
