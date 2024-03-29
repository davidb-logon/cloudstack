// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.host.dao;

import java.util.List;

import org.apache.cloudstack.framework.config.ConfigKey;
import org.apache.cloudstack.framework.config.Configurable;
import org.springframework.stereotype.Component;

import com.cloud.host.HostTagVO;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.GenericSearchBuilder;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.TransactionLegacy;
import com.cloud.utils.db.SearchCriteria.Func;

@Component
public class HostTagsDaoImpl extends GenericDaoBase<HostTagVO, Long> implements HostTagsDao, Configurable {
    protected final SearchBuilder<HostTagVO> HostSearch;
    protected final GenericSearchBuilder<HostTagVO, String> DistinctImplictTagsSearch;

    public HostTagsDaoImpl() {
        HostSearch = createSearchBuilder();
        HostSearch.and("hostId", HostSearch.entity().getHostId(), SearchCriteria.Op.EQ);
        HostSearch.and("isTagARule", HostSearch.entity().getIsTagARule(), SearchCriteria.Op.EQ);
        HostSearch.done();

        DistinctImplictTagsSearch = createSearchBuilder(String.class);
        DistinctImplictTagsSearch.select(null, Func.DISTINCT, DistinctImplictTagsSearch.entity().getTag());
        DistinctImplictTagsSearch.and("hostIds", DistinctImplictTagsSearch.entity().getHostId(), SearchCriteria.Op.IN);
        DistinctImplictTagsSearch.and("implicitTags", DistinctImplictTagsSearch.entity().getTag(), SearchCriteria.Op.IN);
        DistinctImplictTagsSearch.done();
    }

    @Override
    public List<HostTagVO> getHostTags(long hostId) {
        SearchCriteria<HostTagVO> sc = HostSearch.create();
        sc.setParameters("hostId", hostId);

        return search(sc, null);
    }

    @Override
    public List<String> getDistinctImplicitHostTags(List<Long> hostIds, String[] implicitHostTags) {
        SearchCriteria<String> sc = DistinctImplictTagsSearch.create();
        sc.setParameters("hostIds", hostIds.toArray(new Object[hostIds.size()]));
        sc.setParameters("implicitTags", (Object[])implicitHostTags);
        return customSearch(sc, null);
    }

    @Override
    public void deleteTags(long hostId) {
        TransactionLegacy txn = TransactionLegacy.currentTxn();
        txn.start();
        SearchCriteria<HostTagVO> sc = HostSearch.create();
        sc.setParameters("hostId", hostId);
        expunge(sc);
        txn.commit();
    }

    @Override
    public List<HostTagVO> findHostRuleTags() {
        SearchCriteria<HostTagVO> sc = HostSearch.create();
        sc.setParameters("isTagARule", true);

        return search(sc, null);
    }

    @Override
    public void persist(long hostId, List<String> hostTags, Boolean isTagARule) {
        TransactionLegacy txn = TransactionLegacy.currentTxn();

        txn.start();
        SearchCriteria<HostTagVO> sc = HostSearch.create();
        sc.setParameters("hostId", hostId);
        expunge(sc);

        for (String tag : hostTags) {
            tag = tag.trim();
            if (tag.length() > 0) {
                HostTagVO vo = new HostTagVO(hostId, tag, isTagARule);
                persist(vo);
            }
        }
        txn.commit();
    }

    @Override
    public ConfigKey<?>[] getConfigKeys() {
        return new ConfigKey<?>[] {hostTagRuleExecutionTimeout};
    }

    @Override
    public String getConfigComponentName() {
        return HostTagsDaoImpl.class.getSimpleName();
    }
}
