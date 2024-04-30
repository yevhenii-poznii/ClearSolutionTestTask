package com.kiskee.users.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "users")
public class UserProperties {

    private Integer minimumAgeConstraint;
}
