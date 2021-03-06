package com.hirisun.cloud.paas.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hirisun.cloud.model.user.UserVO;
import com.hirisun.cloud.paas.bean.Paas;

public interface PaasService {

	public String create(Paas paas);
	
	public void serviceSort(String id,String ope);
	
	public void publish(UserVO user, String id, Integer result, String remark);

	public IPage<Paas> getPage(IPage<Paas> page, UserVO user, 
			Integer status, String name, String subType);

	public void delete(UserVO user,String id);
	
	public void edit(Paas paas);
	
	public Paas getDetail(UserVO user, String id);
	
	public Paas setWorkflow(String id,String flowId);

}
