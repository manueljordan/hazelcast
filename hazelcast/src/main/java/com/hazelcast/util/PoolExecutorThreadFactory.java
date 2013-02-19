/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.util;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @mdogan 1/10/13
 */
public class PoolExecutorThreadFactory extends ExecutorThreadFactory {

    private final String threadNamePrefix;
    private final AtomicInteger idGen = new AtomicInteger(0);
    private final Queue<Integer> idQ = new LinkedBlockingQueue<Integer>(1000); // to reuse previous thread IDs

    public PoolExecutorThreadFactory(ThreadGroup threadGroup, String threadNamePrefix, ClassLoader classLoader) {
        super(threadGroup, classLoader);
        this.threadNamePrefix = threadNamePrefix;
    }

    protected String newThreadName() {
        throw new UnsupportedOperationException();
    }

    protected Thread createThread(Runnable r) {
        Integer id = idQ.poll();
        if (id == null) {
            id = idGen.incrementAndGet();
        }
        String name = threadNamePrefix + id;
        return new ManagedThread(r, name, id);
    }

    protected void afterThreadExit(ManagedThread t) {
        super.afterThreadExit(t);
        idQ.offer(t.id);
    }
}
