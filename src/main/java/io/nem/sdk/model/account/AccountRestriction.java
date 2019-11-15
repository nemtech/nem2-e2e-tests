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

package io.nem.sdk.model.account;

import io.nem.sdk.model.transaction.AccountRestrictionType;
import java.util.List;

/**
 * It defines one account restriction.
 */
public class AccountRestriction {

    /**
     * The restriction type.
     */
    private final AccountRestrictionType restrictionType;

    /**
     * The list of model objects referencing the restricted value. It can be a {@link
     * io.nem.sdk.model.mosaic.MosaicId}, an {@link Address} or a {@link
     * io.nem.sdk.model.transaction.TransactionType} depending on the target of the restrictionType
     */
    private final List<Object> values;

    public AccountRestriction(AccountRestrictionType restrictionType, List<Object> values) {
        this.restrictionType = restrictionType;
        this.values = values;
    }

    public AccountRestrictionType getRestrictionType() {
        return restrictionType;
    }

    public List<Object> getValues() {
        return values;
    }
}
