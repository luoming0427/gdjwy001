package com.hirisun.cloud.paas.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hirisun.cloud.model.shopping.vo.ApplicationInfoVo;
import com.hirisun.cloud.model.shopping.vo.ShoppingCartVo;
import com.hirisun.cloud.paas.bean.PaasBigdataComponent;
import com.hirisun.cloud.paas.bean.PaasComponentDetail;
import com.hirisun.cloud.paas.bean.PaasGawdsjpt;
import com.hirisun.cloud.paas.mapper.PaasBigdataComponentMapper;
import com.hirisun.cloud.paas.service.IPaasBigdataComponentService;
import com.hirisun.cloud.paas.service.IPaasComponentDetailService;
import com.hirisun.cloud.paas.service.IPaasGawdsjptService;

/**
 * 大数据组件申请信息 服务实现类
 */
@Service
public class PaasBigdataComponentServiceImpl extends ServiceImpl<PaasBigdataComponentMapper, 
		PaasBigdataComponent> implements IPaasBigdataComponentService {

    @Autowired
    private IPaasGawdsjptService paasGawdsjptService;
    @Autowired
    private IPaasComponentDetailService paasComponentDetailService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveShoppingCart(ShoppingCartVo shoppingCart) {
    	
    	List<PaasBigdataComponent> serverList = JSON.parseObject(JSON.toJSON(shoppingCart.getServerList()).toString(), 
    			new TypeReference<List<PaasBigdataComponent>>(){});
    	
        if (serverList != null && !serverList.isEmpty()){
            for (PaasBigdataComponent component : serverList) {
                component.setId(null);
                component.setShoppingCartId(shoppingCart.getId());
                this.save(component);

                List<PaasComponentDetail> componentDetails = component.getComponentDetails();
                for (PaasComponentDetail detail : componentDetails) {
                    detail.setId(null);
                    detail.setComponentId(component.getId());
                    paasComponentDetailService.save(detail);
                }
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void save(ApplicationInfoVo info) {
    	
    	List<PaasBigdataComponent> serverList = JSON.parseObject(JSON.toJSON(info.getServerList()).toString(), 
    			new TypeReference<List<PaasBigdataComponent>>(){});
    	
        if (serverList != null && !serverList.isEmpty()){
            for (PaasBigdataComponent component : serverList) {
                component.setId(null);
                component.setAppInfoId(info.getId());
                this.save(component);

                List<PaasComponentDetail> componentDetails = component.getComponentDetails();
                for (PaasComponentDetail detail : componentDetails) {
                    detail.setId(null);
                    detail.setComponentId(component.getId());
                    paasComponentDetailService.save(detail);
                }
            }
        }
    }

    @Override
    public List<PaasBigdataComponent> getByAppInfoId(String appInfoId) {
        List<PaasBigdataComponent> components = this.list(  new QueryWrapper<PaasBigdataComponent>()
                                                            .lambda()
                                                            .eq(PaasBigdataComponent::getAppInfoId,appInfoId)
                                                            .orderByAsc(PaasBigdataComponent::getModifiedTime));
        if (components!=null && !components.isEmpty()){
            for (PaasBigdataComponent component : components) {
                List<PaasComponentDetail> componentDetails = paasComponentDetailService.list(
                        new QueryWrapper<PaasComponentDetail>().lambda()
                        .eq(PaasComponentDetail::getComponentId,component.getId()));
                component.setComponentDetails(componentDetails);
            }
        }
        return components;
    }

    @Override
    public List<PaasBigdataComponent> getByShoppingCartId(String shoppingCartId) {
        List<PaasBigdataComponent> components = this.list(  new QueryWrapper<PaasBigdataComponent>()
                                                            .lambda()
                                                            .eq(PaasBigdataComponent::getShoppingCartId,shoppingCartId)
                                                            .orderByAsc(PaasBigdataComponent::getModifiedTime));
        if (components!=null && !components.isEmpty()){
            for (PaasBigdataComponent component : components) {
                List<PaasComponentDetail> componentDetails = paasComponentDetailService.list(
                        new QueryWrapper<PaasComponentDetail>().lambda()
                                .eq(PaasComponentDetail::getComponentId,component.getId()));
                component.setComponentDetails(componentDetails);
            }
        }
        return components;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void update(ApplicationInfoVo info) {
        List<PaasBigdataComponent> components = this.list(  new QueryWrapper<PaasBigdataComponent>()
                .lambda()
                .eq(PaasBigdataComponent::getAppInfoId,info.getId()));
        for (PaasBigdataComponent component : components) {
            paasComponentDetailService.remove(new QueryWrapper<PaasComponentDetail>().lambda().eq(PaasComponentDetail::getComponentId,component.getId()));
        }
        this.remove(new QueryWrapper<PaasBigdataComponent>()
                .lambda()
                .eq(PaasBigdataComponent::getAppInfoId,info.getId()));
        save(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateShoppingCart(ShoppingCartVo shoppingCart) {
        List<PaasBigdataComponent> components = this.list(  new QueryWrapper<PaasBigdataComponent>()
                .lambda()
                .eq(PaasBigdataComponent::getShoppingCartId,shoppingCart.getId()));
        for (PaasBigdataComponent component : components) {
            paasComponentDetailService.remove(new QueryWrapper<PaasComponentDetail>().lambda().eq(PaasComponentDetail::getComponentId,component.getId()));
        }
        this.remove(new QueryWrapper<PaasBigdataComponent>()
                .lambda()
                .eq(PaasBigdataComponent::getShoppingCartId,shoppingCart.getId()));
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

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void deleteByShoppingCart(String shoppingCartId) {
        List<PaasBigdataComponent> components = this.list(  new QueryWrapper<PaasBigdataComponent>()
                .lambda()
                .eq(PaasBigdataComponent::getShoppingCartId,shoppingCartId));
        for (PaasBigdataComponent component : components) {
            paasComponentDetailService.remove(new QueryWrapper<PaasComponentDetail>().lambda().eq(PaasComponentDetail::getComponentId,component.getId()));
        }
        this.remove(new QueryWrapper<PaasBigdataComponent>()
                .lambda()
                .eq(PaasBigdataComponent::getShoppingCartId,shoppingCartId));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void refAppInfoFromShoppingCart(String shoppingCartId, String appInfoId) {
        this.update(new PaasBigdataComponent(),new UpdateWrapper<PaasBigdataComponent>().lambda()
                .eq(PaasBigdataComponent::getShoppingCartId,shoppingCartId)
                .set(PaasBigdataComponent::getAppInfoId,appInfoId));
    }

//    @Override
//    public void oldDataMove(String shoppingCartId, String appInfoId) {
//    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void moveData() {
        List<PaasGawdsjpt> list = paasGawdsjptService.list(new QueryWrapper<PaasGawdsjpt>().lambda());
        Set<String> orders=new HashSet<>();
        list.forEach(data ->{
            orders.add(data.getAppInfoId());
        });
        System.out.println("========================>>>size:"+orders.size());
        for (String id : orders) {
            List<PaasGawdsjpt> list1 = paasGawdsjptService.list(new QueryWrapper<PaasGawdsjpt>().lambda().eq(PaasGawdsjpt::getAppInfoId,id));
            if (list1==null || list1 .isEmpty()){
                continue;
            }
            PaasGawdsjpt paasGawdsjpt = list1.get(0);
            PaasBigdataComponent paasBigdataComponent = new PaasBigdataComponent();
            paasBigdataComponent.setAppInfoId(paasGawdsjpt.getAppInfoId());
            paasBigdataComponent.setShoppingCartId(paasGawdsjpt.getShoppingCartId());
            paasBigdataComponent.setClusterType(0);
            paasBigdataComponent.setCreateTime(paasGawdsjpt.getCreateTime());
            paasBigdataComponent.setModifiedTime(paasGawdsjpt.getModifiedTime());
            paasBigdataComponent.setName(paasGawdsjpt.getComponent());
            this.save(paasBigdataComponent);
            for (PaasGawdsjpt gawdsjpt : list1) {
                PaasComponentDetail paasComponentDetail = new PaasComponentDetail();
                paasComponentDetail.setComponentId(paasBigdataComponent.getId());
                paasComponentDetail.setIfAuth(gawdsjpt.getAuthorized());
                paasComponentDetail.setIfCreate(gawdsjpt.getCreateNew());
                paasComponentDetail.setCpu(gawdsjpt.getCpu());
                paasComponentDetail.setMemory(gawdsjpt.getRam());
                paasComponentDetail.setDisk(gawdsjpt.getStorage());
                paasComponentDetail.setCreateTime(gawdsjpt.getCreateTime());
                paasComponentDetail.setModifiedTime(gawdsjpt.getModifiedTime());
                String component = gawdsjpt.getComponent();
                String[] s = component.split(" ");
                paasComponentDetail.setName(s[0]);
                paasComponentDetail.setVersion(s[1]);
                paasComponentDetailService.save(paasComponentDetail);
            }
        }
    }
}
