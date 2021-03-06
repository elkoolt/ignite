/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.pagemem.wal.record.delta;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.pagemem.PageMemory;
import org.apache.ignite.internal.processors.cache.database.tree.io.PageIO;
import org.apache.ignite.internal.processors.cache.database.tree.io.PageMetaIO;
import org.apache.ignite.internal.processors.cache.database.tree.io.PagePartitionMetaIO;

/**
 *
 */
public class MetaPageInitRecord extends InitNewPageRecord {
    /** */
    private long treeRoot;

    /** */
    private long reuseListRoot;

    /** */
    private int ioType;

    /**
     * @param cacheId Cache ID.
     * @param pageId Page ID.
     * @param ioType IO type.
     * @param treeRoot Tree root.
     * @param reuseListRoot Reuse list root.
     */
    public MetaPageInitRecord(int cacheId, long pageId, int ioType, int ioVer, long treeRoot, long reuseListRoot) {
        super(cacheId, pageId, ioType, ioVer, pageId);

        assert ioType == PageIO.T_META || ioType == PageIO.T_PART_META;

        this.treeRoot = treeRoot;
        this.reuseListRoot = reuseListRoot;
        this.ioType = ioType;
    }

    /**
     * @return Tree root.
     */
    public long treeRoot() {
        return treeRoot;
    }

    /**
     * @return Reuse list root.
     */
    public long reuseListRoot() {
        return reuseListRoot;
    }

    /**
     * @return IO type.
     */
    public int ioType() {
        return ioType;
    }

    /** {@inheritDoc} */
    @Override public void applyDelta(PageMemory pageMem, long pageAddr) throws IgniteCheckedException {
        PageMetaIO io = ioType == PageIO.T_META ?
            PageMetaIO.VERSIONS.forPage(pageAddr) :
            PagePartitionMetaIO.VERSIONS.forPage(pageAddr);

        io.initNewPage(pageAddr, newPageId, pageMem.pageSize());

        io.setTreeRoot(pageAddr, treeRoot);
        io.setReuseListRoot(pageAddr, reuseListRoot);
    }

    /** {@inheritDoc} */
    @Override public RecordType type() {
        return RecordType.META_PAGE_INIT;
    }
}
