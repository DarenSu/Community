package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import javax.xml.crypto.Data;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

	/**
	 * 2019-10-24
	 * 文件上传大小的配置
	 * 2020-03-21修改，进行测试用，已恢复
	 * 2020-06-12修改，数据测试使用，已恢复
	 * @return
	 */

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		//单个文件最大
		factory.setMaxFileSize(DataSize.parse("2048000KB"));
		/// 设置总上传数据总大小
		factory.setMaxRequestSize(DataSize.parse("209715200KB"));

		return factory.createMultipartConfig();
	}

}
