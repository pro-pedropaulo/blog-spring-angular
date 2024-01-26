package com.example.blog.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dsk6h8bvo");
        config.put("api_key", "411149693688678");
        config.put("api_secret", "DdkXy5Bex1xyuwc-e4gYDUnSD14");
        return new Cloudinary(config);
    }
}
