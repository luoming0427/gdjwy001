package com.hirisun.cloud.third.service;

import com.hirisun.cloud.third.bean.ThreePartyInterface;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 第三方接口表 服务类
 * </p>
 *
 * @author wuxiaoxing
 * @since 2020-06-28
 */
public interface ThreePartyInterfaceService extends IService<ThreePartyInterface> {

    List<ThreePartyInterface> getByParams(ThreePartyInterface threePartyInterface);

}