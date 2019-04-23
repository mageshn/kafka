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

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.connect.health.ConnectorType;
import org.apache.kafka.connect.runtime.WorkerTest;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrincipalConnectorClientConfigOverridePolicyTest {

    ConnectorClientConfigOverridePolicy principalConnectorClientConfigOverridePolicy = new PrincipalConnectorClientConfigOverridePolicy();

    @Test
    public void testPrincipalOnly() {
        Map<String, Object> clientConfig = Collections.singletonMap(SaslConfigs.SASL_JAAS_CONFIG, "test");
        ConnectorClientConfigRequest connectorClientConfigRequest = new ConnectorClientConfigRequest(
            "test",
            ConnectorType.SOURCE,
            WorkerTest.WorkerTestConnector.class,
            clientConfig,
            ConnectorClientConfigRequest.ClientType.PRODUCER);
        principalConnectorClientConfigOverridePolicy.validate(connectorClientConfigRequest);
    }

    @Test(expected = PolicyViolationException.class)
    public void testPrincipalPlusOtherConfigs() {
        Map<String, Object> clientConfig = new HashMap<>();
        clientConfig.put(SaslConfigs.SASL_JAAS_CONFIG, "test");
        clientConfig.put(ProducerConfig.ACKS_CONFIG, "none");
        ConnectorClientConfigRequest connectorClientConfigRequest = new ConnectorClientConfigRequest(
            "test",
            ConnectorType.SOURCE,
            WorkerTest.WorkerTestConnector.class,
            clientConfig,
            ConnectorClientConfigRequest.ClientType.PRODUCER);
        principalConnectorClientConfigOverridePolicy.validate(connectorClientConfigRequest);
    }
}
