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
import com.hirisun.cloud.paas.bean.PaasElb;
import com.hirisun.cloud.paas.mapper.PaasElbMapper;
import com.hirisun.cloud.paas.service.IPaasElbService;

/**
 * ELB弹性负载均衡 服务实现类
 */
@Service
public class PaasElbServiceImpl extends ServiceImpl<PaasElbMapper, PaasElb> implements IPaasElbService {

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveShoppingCart(ShoppingCartVo shoppingCart) {
    	
    	List<PaasElb> elbList = JSON.parseObject(JSON.toJSON(shoppingCart.getServerList()).toString(), 
    			new TypeReference<List<PaasElb>>(){});
    	
        for(PaasElb elb:elbList){
            elb.setId(null);
            elb.setShoppingCartId(shoppingCart.getId());
            elb.insert();
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void save(ApplicationInfoVo info) {
    	
    	List<PaasElb> elbList = JSON.parseObject(JSON.toJSON(info.getServerList()).toString(), 
    			new TypeReference<List<PaasElb>>(){});
    	
        for(PaasElb elb:elbList){
            elb.setId(null);
            elb.setAppInfoId(info.getId());
            elb.insert();
        }
    }

    /**
     * 获取申请信息
     *
     * @param appInfoId
     */
    @Override
    public List<PaasElb> getByAppInfoId(String appInfoId) {
        return this.list(new QueryWrapper<PaasElb>().lambda().eq(PaasElb::getAppInfoId,appInfoId)
                    .orderByDesc(PaasElb::getModifiedTime));
    }

    @Override
    public List<PaasElb> getByShoppingCartId(String shoppingCartId) {
        return this.list(new QueryWrapper<PaasElb>().lambda().eq(PaasElb::getShoppingCartId,shoppingCartId)
                .orderByDesc(PaasElb::getModifiedTime));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void update(ApplicationInfoVo info) {
        this.remove(new QueryWrapper<PaasElb>().lambda().eq(PaasElb::getAppInfoId,info.getId()));
        this.save(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateShoppingCart(ShoppingCartVo shoppingCart) {
        this.remove(new QueryWrapper<PaasElb>().lambda().eq(PaasElb::getShoppingCartId,shoppingCart.getId()));
        this.saveShoppingCart(shoppingCart);
    }

    /**
     * 获取申请总数(购物车显示字段)
     *
     * @param appInfoId
     */
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
        this.remove(new QueryWrapper<PaasElb>().lambda().eq(PaasElb::getShoppingCartId,shoppingCartId));
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
        this.update(new PaasElb(),new UpdateWrapper<PaasElb>().lambda()
                .eq(PaasElb::getShoppingCartId,shoppingCartId)
                .set(PaasElb::getAppInfoId,appInfoId));
    }

}
