package com.hirisun.cloud.paas.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hirisun.cloud.model.shopping.vo.ApplicationInfoVo;
import com.hirisun.cloud.model.shopping.vo.ShoppingCartVo;
import com.hirisun.cloud.paas.bean.PaasGawdsjpt;
import com.hirisun.cloud.paas.mapper.PaasGawdsjptMapper;
import com.hirisun.cloud.paas.service.IPaasGawdsjptService;

/**
 * 公安网大数据平台资源申请信息 服务实现类
 */
@Service
public class PaasGawdsjptServiceImpl extends ServiceImpl<PaasGawdsjptMapper, 
	PaasGawdsjpt> implements IPaasGawdsjptService {


    @Override
    public void saveShoppingCart(ShoppingCartVo shoppingCart) {
    	
    	List<PaasGawdsjpt> serverList = JSON.parseObject(JSON.toJSON(shoppingCart.getServerList()).toString(), 
    			new TypeReference<List<PaasGawdsjpt>>(){});
    	
        if (serverList != null && !serverList.isEmpty()) {
            for (PaasGawdsjpt database : serverList) {
                database.setId(null);
                database.setShoppingCartId(shoppingCart.getId());
                this.save(database);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void save(ApplicationInfoVo info) {
    	
    	List<PaasGawdsjpt> serverList = JSON.parseObject(JSON.toJSON(info.getServerList()).toString(), 
    			new TypeReference<List<PaasGawdsjpt>>(){});
    	
        if (serverList != null && !serverList.isEmpty()) {
            for (PaasGawdsjpt database : serverList) {
                database.setId(null);
                database.setAppInfoId(info.getId());
                this.save(database);
            }
        }
    }

    @Override
    public List<PaasGawdsjpt> getByAppInfoId(String appInfoId) {
        return this.list(new QueryWrapper<PaasGawdsjpt>().lambda()
                .eq(PaasGawdsjpt::getAppInfoId, appInfoId)
                .orderByAsc(PaasGawdsjpt::getCreateTime));
    }

    @Override
    public List<PaasGawdsjpt> getByShoppingCartId(String shoppingCartId) {
        return this.list(new QueryWrapper<PaasGawdsjpt>().lambda()
                .eq(PaasGawdsjpt::getShoppingCartId, shoppingCartId)
                .orderByAsc(PaasGawdsjpt::getCreateTime));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void update(ApplicationInfoVo info) {
        this.remove(new QueryWrapper<PaasGawdsjpt>().lambda().eq(PaasGawdsjpt::getAppInfoId, info.getId()));

        save(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateShoppingCart(ShoppingCartVo shoppingCart) {
        this.remove(new QueryWrapper<PaasGawdsjpt>().lambda().eq(PaasGawdsjpt::getShoppingCartId, shoppingCart.getId()));

        saveShoppingCart(shoppingCart);
    }

    @Override
    public Integer getTotalNum(String appInfoId) {
        return 1;
    }

    @Override
    public Integer getTotalNumInShoppingCart(String shoppingCartId) {
        return 1;
    }

    /**
     * 购物车删除
     *
     * @param shoppingCartId 购物车ID
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void deleteByShoppingCart(String shoppingCartId) {
        this.remove(new QueryWrapper<PaasGawdsjpt>().lambda().eq(PaasGawdsjpt::getShoppingCartId,shoppingCartId));
    }

    /**
     * 服务关联订单
     *
     * @param shoppingCartId
     * @param appInfoId
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void refAppInfoFromShoppingCart(String shoppingCartId, String appInfoId) {
        this.update(new PaasGawdsjpt(),new UpdateWrapper<PaasGawdsjpt>().lambda().eq(PaasGawdsjpt::getShoppingCartId,shoppingCartId)
                .set(PaasGawdsjpt::getAppInfoId,appInfoId));
    }

//    /**
//     * 老数据迁移
//     *
//     * @param shoppingCartId
//     * @param appInfoId
//     */
//    @Transactional(rollbackFor = Throwable.class)
//    @Override
//    public void oldDataMove(String shoppingCartId, String appInfoId) {
//        this.update(new PaasGawdsjpt(),new UpdateWrapper<PaasGawdsjpt>().lambda().eq(PaasGawdsjpt::getAppInfoId,appInfoId)
//                .set(PaasGawdsjpt::getShoppingCartId,shoppingCartId));
//
//
//        this.update(new PaasGawdsjpt(),new UpdateWrapper<PaasGawdsjpt>().lambda().eq(PaasGawdsjpt::getShoppingCartId,shoppingCartId)
//                .set(PaasGawdsjpt::getAppInfoId,""));
//    }

}
