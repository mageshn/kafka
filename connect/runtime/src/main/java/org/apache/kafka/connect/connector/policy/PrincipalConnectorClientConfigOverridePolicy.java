/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.connect.connector.policy;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Allows all {@code sasl} configurations to be overridden via the connector configs by setting {@code client.config.policy} to
 * {@code Principal}. This allows to set a principal per connector.
 */
public class PrincipalConnectorClientConfigOverridePolicy implements ConnectorClientConfigOverridePolicy {
    private static final Logger log = LoggerFactory.getLogger(PrincipalConnectorClientConfigOverridePolicy.class);

    private static final Set<String> ALLOWED_CONFIG =
        Stream.of(SaslConfigs.SASL_JAAS_CONFIG, SaslConfigs.SASL_MECHANISM, CommonClientConfigs.SECURITY_PROTOCOL_CONFIG).
            collect(Collectors.toSet());


    @Override
    public List<ConfigValue> validate(ConnectorClientConfigRequest connectorClientConfigRequest) {
        Map<String, Object> inputConfig = connectorClientConfigRequest.clientProps();
        return inputConfig.entrySet().stream().map(configEntry -> configValue(configEntry)).collect(Collectors.toList());
    }

    private static ConfigValue configValue(Map.Entry<String, Object> configEntry) {
        ConfigValue configValue =
            new ConfigValue(configEntry.getKey(), configEntry.getValue(), new ArrayList<Object>(), new ArrayList<String>());
        if (!ALLOWED_CONFIG.contains(configEntry.getKey())) {
            configValue.addErrorMessage("Principal policy allows only " + ALLOWED_CONFIG + "to be overriden");
        }
        return configValue;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {
        log.info("Setting up Principal policy for ConnectorClientConfigOverride. This will allow `sasl` client configuration to be "
                 + "overridden.");
    }
}
