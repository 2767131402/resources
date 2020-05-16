package com.bbd.elasticsearch.conf;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @program: es-demo
 * @description: es配置类
 * @author: 01
 * @create: 2018-06-28 22:32
 **/
@Configuration
public class ESConfig {

    @Bean
    public TransportClient client() throws UnknownHostException {
        // 9300是es的tcp服务端口
        TransportAddress node1 = new  TransportAddress(
                InetAddress.getByName("39.98.152.206"),9300);
        TransportAddress node2 = new  TransportAddress(
                InetAddress.getByName("39.98.152.206"),9301);

        // 设置es节点的配置信息
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch-cluster")
                .build();

        // 实例化es的客户端对象
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node1);
        client.addTransportAddress(node2);

        return client;
    }
}