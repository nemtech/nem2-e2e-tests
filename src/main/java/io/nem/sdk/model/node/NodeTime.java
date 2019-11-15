/*
 * Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.nem.sdk.model.node;

import java.math.BigInteger;

public class NodeTime {

    private final BigInteger sendTimeStamp;
    private final BigInteger receiveTimeStamp;

    /**
     * Constructor
     */
    public NodeTime(BigInteger sendTimeStamp, BigInteger receiveTimeStamp) {
        this.receiveTimeStamp = receiveTimeStamp;
        this.sendTimeStamp = sendTimeStamp;
    }

    /**
     * Get send timestamp
     *
     * @return BigInteger
     */
    public BigInteger getSendTimeStamp() {
        return this.sendTimeStamp;
    }

    /**
     * Get receive timestamp
     *
     * @return BigInteger
     */
    public BigInteger getReceiveTimeStamp() {
        return this.receiveTimeStamp;
    }
}
