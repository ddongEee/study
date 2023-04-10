package com.study.apps.poc.dlbr;

import com.study.apps.poc.dlbr.service.CriticalRemoteDateHandler;
import com.study.apps.poc.dlbr.service.MultiThreadTester;
import com.study.apps.poc.dlbr.service.RedisPropertyHolder;
import com.study.apps.poc.dlbr.service.RedisTester;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class LightbootApplication {
	private RedisTester redisTester;
	private RedisPropertyHolder redisPropertyHolder;
	private final MultiThreadTester multiThreadTester;
	private final CriticalRemoteDateHandler criticalRemoteDateHandler;

	public static void main(String[] args) {
		SpringApplication.run(LightbootApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		criticalRemoteDateHandler.init();
	}

	@Bean
	public ApplicationRunner myApplicationRunner() {
		return args -> log.info("app started");
	}

	@RestController
	public static final class HelloController {
		@RequestMapping("/hello")
		public String hello() {
//			listObjects("accesslog-net-cloudfront");
			return Thread.currentThread().getName();
		}

		private void listObjects(final String bucketName) {
			System.out.format("Objects in S3 bucket %s:\n", bucketName);
			final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2).build();
			ListObjectsV2Result result = s3.listObjectsV2(bucketName);
			List<S3ObjectSummary> objects = result.getObjectSummaries();
			for (S3ObjectSummary os : objects) {
				System.out.println("* " + os.getKey());
			}
		}

		@Scheduled(fixedDelay = 1000)
		public void test1() {
		}
	}

	public void nslookup(String domain) {
		InetAddress[] inetaddr = null;

		try {
			inetaddr = InetAddress.getAllByName(domain);
		} catch(Exception e) {
			e.printStackTrace();
		}

		for (InetAddress inetAddress : inetaddr) {
			System.out.println("-----------------------------");
			System.out.println(inetAddress.getHostName());
			System.out.println(inetAddress.getHostAddress());
			System.out.println(inetAddress.toString());
			System.out.println("-----------------------------");
		}
	}

	public void nslookup2(String domain) {
		try {
			InetAddress inetHost = InetAddress.getByName(domain);
			String hostName = inetHost.getHostName();
			System.out.println("The host name was: " + hostName);
			System.out.println("The hosts IP address is: " + inetHost.getHostAddress());

		} catch(UnknownHostException ex) {
			System.out.println("Unrecognized host");
		}
	}

}
