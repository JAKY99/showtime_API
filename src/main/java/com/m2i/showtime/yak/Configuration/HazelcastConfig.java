package com.m2i.showtime.yak.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {
    @Value("${application.hazelcast.host}")
    private String hazelcastHost;
    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        NetworkConfig network = config.getNetworkConfig();
        JoinConfig join = network.getJoin();
        join.getTcpIpConfig().setEnabled(true);
        join.getTcpIpConfig().addMember(hazelcastHost);
        join.getMulticastConfig().setEnabled(false);

        return Hazelcast.newHazelcastInstance(config);
    }
}