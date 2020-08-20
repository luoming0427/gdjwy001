package com.hirisun.cloud.order.service.paas.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hirisun.cloud.order.bean.ApplicationInfo;
import com.hirisun.cloud.order.bean.paas.PaasDatabase;
import com.hirisun.cloud.order.bean.shopping.ShoppingCart;
import com.hirisun.cloud.order.mapper.paas.PaasDatabaseMapper;
import com.hirisun.cloud.order.service.paas.IPaasDatabaseService;

/**
 * PaaS 数据库申请信息 服务实现类
 */
@Service
public class PaasDatabaseServiceImpl extends ServiceImpl<PaasDatabaseMapper, 
	PaasDatabase> implements IPaasDatabaseService {

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveShoppingCart(ShoppingCart<PaasDatabase> shoppingCart) {
        List<PaasDatabase> serverList = shoppingCart.getServerList();
        if (serverList != null && !serverList.isEmpty()) {
            for (PaasDatabase database : serverList) {
                database.setId(null);
                database.setShoppingCartId(shoppingCart.getId());
                this.save(database);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void save(ApplicationInfo<PaasDatabase, Object> info) {
        List<PaasDatabase> serverList = info.getServerList();
        if (serverList != null && !serverList.isEmpty()) {
            for (PaasDatabase database : serverList) {
                database.setId(null);
                database.setAppInfoId(info.getId());
                this.save(database);
            }
        }
    }

    @Override
    public List<PaasDatabase> getByAppInfoId(String appInfoId) {
        return this.list(new QueryWrapper<PaasDatabase>().lambda()
                .eq(PaasDatabase::getAppInfoId, appInfoId)
                .orderByAsc(PaasDatabase::getCreateTime));
    }

    @Override
    public List<PaasDatabase> getByShoppingCartId(String shoppingCartId) {
        return this.list(new QueryWrapper<PaasDatabase>().lambda()
                        .eq(PaasDatabase::getShoppingCartId,shoppingCartId)
                        .orderByAsc(PaasDatabase::getCreateTime));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void update(ApplicationInfo<PaasDatabase, Object> info) {
        this.remove(new QueryWrapper<PaasDatabase>().lambda().eq(PaasDatabase::getAppInfoId, info.getId()));

        save(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateShoppingCart(ShoppingCart<PaasDatabase> shoppingCart) {
        this.remove(new QueryWrapper<PaasDatabase>().lambda().eq(PaasDatabase::getShoppingCartId, shoppingCart.getId()));

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
        this.remove(new QueryWrapper<PaasDatabase>().lambda().eq(PaasDatabase::getShoppingCartId, shoppingCartId));
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
        this.update(new PaasDatabase(),new UpdateWrapper<PaasDatabase>().lambda().eq(PaasDatabase::getShoppingCartId,shoppingCartId)
                .set(PaasDatabase::getAppInfoId,appInfoId));
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
//        this.update(new PaasDatabase(),new UpdateWrapper<PaasDatabase>().lambda().eq(PaasDatabase::getAppInfoId,appInfoId)
//                .set(PaasDatabase::getShoppingCartId,shoppingCartId));
//
//
//        this.update(new PaasDatabase(),new UpdateWrapper<PaasDatabase>().lambda().eq(PaasDatabase::getShoppingCartId,shoppingCartId)
//                .set(PaasDatabase::getAppInfoId,""));
//    }

}