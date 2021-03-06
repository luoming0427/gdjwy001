package com.hirisun.cloud.paas.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hirisun.cloud.model.shopping.vo.ApplicationInfoVo;
import com.hirisun.cloud.model.shopping.vo.ShoppingCartVo;
import com.hirisun.cloud.paas.bean.PaasTyxx;
import com.hirisun.cloud.paas.mapper.PaasTyxxMapper;
import com.hirisun.cloud.paas.service.IPaasTyxxService;

/**
 * 统一消息申请信息 服务实现类
 */
@Service
public class PaasTyxxServiceImpl extends ServiceImpl<PaasTyxxMapper, PaasTyxx> implements IPaasTyxxService {


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveShoppingCart(ShoppingCartVo shoppingCart) {
    	
    	List<PaasTyxx> serverList = JSON.parseObject(JSON.toJSON(shoppingCart.getServerList()).toString(), 
    			new TypeReference<List<PaasTyxx>>(){});
    	
        if(CollectionUtils.isNotEmpty(serverList)){
            for(PaasTyxx tyxx:serverList){
                tyxx.setId(null);
                tyxx.setShoppingCartId(shoppingCart.getId());
            }
            this.saveBatch(serverList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void save(ApplicationInfoVo info) {
    	
    	List<PaasTyxx> serverList = JSON.parseObject(JSON.toJSON(info.getServerList()).toString(), 
    			new TypeReference<List<PaasTyxx>>(){});
    	
        if (serverList != null && !serverList.isEmpty()) {
            for (PaasTyxx txyfw : serverList) {
                txyfw.setId(null);
                txyfw.setAppInfoId(info.getId());
                this.save(txyfw);
            }
        }
    }

    @Override
    public List<PaasTyxx> getByAppInfoId(String appInfoId) {
        List<PaasTyxx> tyxxList = this.list(new QueryWrapper<PaasTyxx>().lambda()
                .eq(PaasTyxx::getAppInfoId, appInfoId)
                .orderByAsc(PaasTyxx::getCreateTime));

        return tyxxList;
    }

    @Override
    public List<PaasTyxx> getByShoppingCartId(String shoppingCartId) {
        return this.list(new QueryWrapper<PaasTyxx>().lambda()
                .eq(PaasTyxx::getShoppingCartId, shoppingCartId)
                .orderByAsc(PaasTyxx::getCreateTime));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void update(ApplicationInfoVo info) {
        this.remove(new QueryWrapper<PaasTyxx>().lambda().eq(PaasTyxx::getAppInfoId, info.getId()));
        save(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateShoppingCart(ShoppingCartVo shoppingCart) {
        this.remove(new QueryWrapper<PaasTyxx>().lambda().eq(PaasTyxx::getAppInfoId, shoppingCart.getId()));
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
        this.remove(new QueryWrapper<PaasTyxx>().lambda().eq(PaasTyxx::getAppInfoId,shoppingCartId));
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
        this.update(new PaasTyxx(),new UpdateWrapper<PaasTyxx>().lambda().eq(PaasTyxx::getShoppingCartId,shoppingCartId)
                .set(PaasTyxx::getAppInfoId,appInfoId));
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
//        this.update(new PaasTyxx(),new UpdateWrapper<PaasTyxx>().lambda().eq(PaasTyxx::getAppInfoId,appInfoId)
//                .set(PaasTyxx::getShoppingCartId,shoppingCartId));
//
//
//        this.update(new PaasTyxx(),new UpdateWrapper<PaasTyxx>().lambda().eq(PaasTyxx::getShoppingCartId,shoppingCartId)
//                .set(PaasTyxx::getAppInfoId,""));
//    }

}
