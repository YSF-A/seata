/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.spring.boot.autoconfigure.properties.client;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static io.seata.spring.boot.autoconfigure.StarterConstants.TLS_PREFIX;

@Component
@ConfigurationProperties(prefix = TLS_PREFIX)
public class TlsProperties {
    /**
     * path of trust certificate
     */
    private String trustCertificatePath = null;

    public String getTrustCertificatePath() { return this.trustCertificatePath; }

    public TlsProperties setTrustCertificatePath(String trustCertificatePath) {
        this.trustCertificatePath = trustCertificatePath;
        return this;
    }
}
