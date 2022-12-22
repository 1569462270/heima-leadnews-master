package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.admin.AdminConstants;
import com.heima.common.exception.CustException;
import com.heima.common.exception.CustomException;
import com.heima.feign.ArticleFeign;
import com.heima.feign.WemediaFeign;
import com.heima.model.article.ApAuthor;
import com.heima.model.common.dto.PageResponseResult;
import com.heima.model.common.dto.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dto.AuthDTO;
import com.heima.model.user.entity.ApUser;
import com.heima.model.user.entity.ApUserRealName;
import com.heima.model.wemedia.entity.WmUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRealNameMapper;
import com.heima.user.service.ApUserRealNameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author : MR.wu
 * @Description : 用户实名认证Impl
 * @Date : 2022/12/21 17:59
 * @Version : 1.0
 */
@Service
@Slf4j
public class ApUserRealNameServiceImpl extends ServiceImpl<ApUserRealNameMapper, ApUserRealName> implements ApUserRealNameService {
    @Autowired
    private ApUserMapper apUserMapper;

    @Autowired
    private ArticleFeign articleFeign;

    @Autowired
    private WemediaFeign wemediaFeign;

    /**
     * 根据状态查询需要认证相关的用户信息
     *
     * @param dto dto
     * @return {@code ResponseResult}
     */
    @Override
    public ResponseResult loadListByStatus(AuthDTO dto) {
        if (null == dto) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();
        LambdaQueryWrapper<ApUserRealName> wrapper = Wrappers.<ApUserRealName>lambdaQuery();
        wrapper.eq(null != dto.getStatus(), ApUserRealName::getStatus, dto.getStatus());
        Page<ApUserRealName> realNamePage = new Page<>(dto.getPage(), dto.getSize());
        IPage<ApUserRealName> pageReq = page(realNamePage, wrapper);
        return new PageResponseResult(dto.getPage(), dto.getSize(), pageReq.getTotal(), pageReq.getRecords());
    }


    /**
     * 更新状态通过id
     *
     * @param dto    dto
     * @param status 状态
     * @return {@code ResponseResult}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateStatusById(AuthDTO dto, Short status) {
        // 校验参数
        if (null == dto.getId()) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "用户认证信息id为空");
        }
        // 查询当前认证用户是否在 APP端有当前用户
        ApUserRealName apUserRealName = getById(dto.getId());
        if (null == apUserRealName) {
            log.error("待审核 实名认证信息不存在 userRealNameId:{}", dto.getId());
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 校验审核状态
        if (!AdminConstants.WAIT_AUTH.equals(apUserRealName.getStatus())) {
            log.error("实名认证信息非待审核状态   userRealNameId:{}", dto.getId());
            CustException.cust(AppHttpCodeEnum.DATA_NOT_ALLOW);
        }
        // 查询app用户是否存在
        ApUser apUser = apUserMapper.selectById(apUserRealName.getUserId());
        if (null == apUser) {
            log.error("实名认证信息 关联 app的用户不存在    userRealNameId:{}, userId:{} ", dto.getId(), apUserRealName.getUserId());
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 更新状态
        apUserRealName.setStatus(status);
        apUserRealName.setUpdatedTime(new Date());
        if (StringUtils.isNotBlank(dto.getMsg())) {
            apUserRealName.setReason(dto.getMsg());
        }
        updateById(apUserRealName);
        // 认证成功
        if (AdminConstants.PASS_AUTH.equals(status)) {
            // 创建自媒体用户
            WmUser wmUser = createWmUser(dto, apUser);
            // 创建作者用户
            createApAuthor(wmUser);
        }
        return ResponseResult.okResult();
    }

    /**
     * 创建作者账户
     *
     * @param wmUser wm用户
     */
    private void createApAuthor(WmUser wmUser) {
        // 检查是否成功调用
        ResponseResult<ApAuthor> apAuthorResult = articleFeign.findByUserId(wmUser.getApUserId());
        if (apAuthorResult.getCode().intValue() != 0) {
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR, apAuthorResult.getErrorMessage());
        }
        // 检查作者信息是否已经存在
        ApAuthor apAuthor = apAuthorResult.getData();
        if (apAuthor != null) {
            CustException.cust(AppHttpCodeEnum.DATA_EXIST, "作者信息已存在");
        }
        //3. 添加作者信息
        apAuthor = new ApAuthor();
        apAuthor.setCreatedTime(new Date());
        apAuthor.setName(wmUser.getName());
        apAuthor.setType(AdminConstants.AUTHOR_TYPE); // 自媒体人类型
        apAuthor.setUserId(wmUser.getApUserId()); // APP 用户ID
        apAuthor.setWmUserId(wmUser.getId()); // 自媒体用户ID
        ResponseResult result = articleFeign.save(apAuthor);
        //4. 结果失败，抛出异常
        if (result.getCode() != 0) {
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR, result.getErrorMessage());
        }
    }

    /**
     * 创建自媒体用户
     *
     * @param dto    dto
     * @param apUser app用户信息
     * @return {@code WmUser}
     */
    private WmUser createWmUser(AuthDTO dto, ApUser apUser) {
        // 查询自媒体账号是否存在（APP端用户密码和自媒体密码一致）
        ResponseResult<WmUser> wmUserResult = wemediaFeign.findByName(apUser.getName());
        if (wmUserResult.getCode().intValue() != 0) {
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR, wmUserResult.getErrorMessage());
        }
        WmUser wmUser = wmUserResult.getData();
        if (wmUser != null) {
            CustException.cust(AppHttpCodeEnum.DATA_EXIST, "自媒体用户信息已存在");
        }
        wmUser = new WmUser();
        wmUser.setName(apUser.getName());
        wmUser.setSalt(apUser.getSalt());
        wmUser.setPassword(apUser.getPassword());
        wmUser.setPhone(apUser.getPhone());
        wmUser.setCreatedTime(new Date());
        wmUser.setType(0);
        wmUser.setApUserId(apUser.getId());
        wmUser.setStatus(AdminConstants.PASS_AUTH.intValue());

        ResponseResult<WmUser> saveResult = wemediaFeign.save(wmUser);
        if (saveResult.getCode().intValue() != 0) {
            CustException.cust(AppHttpCodeEnum.REMOTE_SERVER_ERROR, saveResult.getErrorMessage());
        }
        return saveResult.getData();
    }
}
