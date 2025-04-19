package com.yuhao.yupicturebackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.yupicturebackend.annotation.AuthCheck;
import com.yuhao.yupicturebackend.common.BaseResponse;
import com.yuhao.yupicturebackend.common.DeleteRequest;
import com.yuhao.yupicturebackend.common.ResultUtils;
import com.yuhao.yupicturebackend.constant.UserConstant;
import com.yuhao.yupicturebackend.exception.BusinessException;
import com.yuhao.yupicturebackend.exception.ErrorCode;
import com.yuhao.yupicturebackend.exception.ThrowUtils;
import com.yuhao.yupicturebackend.model.dto.user.*;
import com.yuhao.yupicturebackend.model.entity.User;
import com.yuhao.yupicturebackend.model.vo.LoginUserVO;
import com.yuhao.yupicturebackend.model.vo.PictureVO;
import com.yuhao.yupicturebackend.model.vo.UserVO;
import com.yuhao.yupicturebackend.service.LikeRecordService;
import com.yuhao.yupicturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    @Qualifier("likeService")
    private LikeRecordService likeRecordService;
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null , ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
    //用户编辑个人资料
    @PostMapping("/update/user")
    public BaseResponse<Boolean> updateUserData(@RequestBody UserUpdateRequest userUpdateRequest,HttpServletRequest request) {
        //校验参数，仅能修改本人信息,即当前登录用户
        ThrowUtils.throwIf(userUpdateRequest == null ||  userUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(!(userUpdateRequest.getUserRole() == null), ErrorCode.NO_AUTH_ERROR,"您没有权限修改用户角色");
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(!(userUpdateRequest.getId().equals(loginUser.getId())), ErrorCode.NO_AUTH_ERROR,"您没有权限修改其他用户信息");
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 查询该用户的点赞记录(图片列表)
     */
    @GetMapping("/like/Record")
    public BaseResponse<List<PictureVO>> getUserLikeRecord(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<PictureVO> result = likeRecordService.getUserLikeRecord(loginUser.getId());
        return ResultUtils.success(result);
    }


}
