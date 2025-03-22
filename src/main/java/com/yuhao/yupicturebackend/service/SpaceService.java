package com.yuhao.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhao.yupicturebackend.model.dto.space.SpaceAddRequest;
import com.yuhao.yupicturebackend.model.dto.space.SpaceQueryRequest;
import com.yuhao.yupicturebackend.model.entity.Space;
import com.yuhao.yupicturebackend.model.entity.User;
import com.yuhao.yupicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author hyh
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-02-21 09:55:19
*/
public interface SpaceService extends IService<Space> {
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);
    /**
     * 获取空间VO
     *
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间VO分页
     *
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 验证空间
     * @param space
     * @param add
     */
    void validSpace(Space space, boolean add);

    /**
     * 填充空间参数
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 权限校验(仅空间本人或管理员)
     */
    void checkSpaceAuth(Space space, User loginuser);
}
