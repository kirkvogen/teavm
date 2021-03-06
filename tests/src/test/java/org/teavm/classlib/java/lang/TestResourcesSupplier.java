/*
 *  Copyright 2015 Alexey Andreev.
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
package org.teavm.classlib.java.lang;

import org.teavm.classlib.ResourceSupplier;
import org.teavm.model.ListableClassReaderSource;

/**
 *
 * @author Alexey Andreev
 */
public class TestResourcesSupplier implements ResourceSupplier {
    @Override
    public String[] supplyResources(ClassLoader classLoader, ListableClassReaderSource classSource) {
        String[] result = { "1", "2", "3", "4", "5", "6", "7", "8" };
        for (int i = 0; i < result.length; ++i) {
            result[i] = "resources-for-test/" + result[i];
        }
        return result;
    }
}
