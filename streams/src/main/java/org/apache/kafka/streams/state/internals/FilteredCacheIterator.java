/**
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
package org.apache.kafka.streams.state.internals;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;

import java.util.NoSuchElementException;

class FilteredCacheIterator implements PeekingKeyValueIterator<Bytes, LRUCacheEntry> {
    private final PeekingKeyValueIterator<Bytes, LRUCacheEntry> cacheIterator;
    private final HasNextCondition hasNextCondition;

    FilteredCacheIterator(final PeekingKeyValueIterator<Bytes, LRUCacheEntry> cacheIterator,
                          final HasNextCondition hasNextCondition) {
        this.cacheIterator = cacheIterator;
        this.hasNextCondition = hasNextCondition;
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public Bytes peekNextKey() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return cacheIterator.peekNextKey();
    }

    @Override
    public boolean hasNext() {
        return hasNextCondition.hasNext(cacheIterator);
    }

    @Override
    public KeyValue<Bytes, LRUCacheEntry> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return cacheIterator.next();

    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyValue<Bytes, LRUCacheEntry> peekNext() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return cacheIterator.peekNext();
    }
}
