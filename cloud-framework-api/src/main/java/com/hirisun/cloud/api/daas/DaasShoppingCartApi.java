package com.hirisun.cloud.api.daas;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hirisun.cloud.model.shopping.vo.ShoppingCartVo;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("cloud-daas-service")
public interface DaasShoppingCartApi {

	@ApiOperation(value = "购物车保存购物项")
    @PostMapping("/daas/shopping/item/save")
	public void itemSave(@RequestBody ShoppingCartVo shoppingCartVo);
	
	@ApiOperation("根据购物车id获取购物项")
    @PostMapping(value = "/daas/shopping/item/get")
    public List getShoppingCartItemList(@RequestBody ShoppingCartVo shoppingCartVo);

    @ApiOperation("根据购物车id统计购物项数量")
    @PostMapping(value = "/daas/shopping/item/total")
    public Integer getTotalNumInShoppingCart(@RequestBody ShoppingCartVo shoppingCartVo);
    
    @ApiOperation("根据购物车id统计购物项数量")
    @PostMapping(value = "/daas/shopping/item/update")
    public void updateShoppingCartItem(@RequestBody ShoppingCartVo shoppingCartVo);
    
    @ApiOperation("根据购物车id删除购物项")
    @PostMapping(value = "/daas/shopping/item/delete")
    public void deleteItemByShoppingCartId(@RequestBody ShoppingCartVo shoppingCartVo);
    
    @ApiOperation("关联购物车购物项")
    @PostMapping(value = "/daas/shopping/item/ref")
    public void refAppInfoFromShoppingCart(@RequestBody ShoppingCartVo shoppingCartVo);

    @ApiOperation("根据表单类型获取表单信息")
    @PostMapping(value = "/daas/shopping/getHandlerWrapper")
    public String getHandlerWrapper(@RequestParam String formNum);

    @ApiOperation("根据表单类型和工单信息获取表单列表")
    @PostMapping(value = "/daas/shopping/getByAppInfoId")
    public List<JSONObject> getByAppInfoId(@RequestParam String formNum,@RequestParam String applyInfoId);

    @ApiOperation("根据表单类型和工单信息获取实施列表")
    @PostMapping(value = "/daas/shopping/getImplServerListByAppInfoId")
    public List<JSONObject> getImplServerListByAppInfoId(@RequestParam String formNum,@RequestParam String applyInfoId);
    
}
