package com.yunsheng.zk_controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SpringBootApplication
public class ZkControllerApplication {

	private static final String ZK_ADDRESS = "127.0.0.1:2181";
	private static final String ZK_PATH = "/test";

	public static void main(String[] args) {
		SpringApplication.run(ZkControllerApplication.class, args);
	}

	@RequestMapping("/")
	@ResponseBody
	public String root(){
		String result = "[]";
		try {
			Map<String, String> map = getInfo("/");
			result = JSONObject.valueToString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map<String, String> getInfo(String path) throws Exception {
		Map<String, String> result = new HashMap<>();
		// 1.Connect to zk
		CuratorFramework client = CuratorFrameworkFactory.newClient(
				ZK_ADDRESS,
				new RetryNTimes(10, 5000)
		);
		client.start();
		System.out.println("zk client start successfully!");

		// 2.2 Get node and data
		List<String> children = client.getChildren().forPath(path);
		for (String child : children){
			System.out.println("====" + child);
			byte[] bytes = client.getData().forPath("/" + child);
			result.put(child, new String(bytes));
		}
		return result;
	}
}
