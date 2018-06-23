/*
 * Copyright 2018 Merlin Osayimwen
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

package io.github.pojogen.generator;

import io.github.pojogen.generator.internal.GenerationContext;
import io.github.pojogen.struct.Struct;
import io.github.pojogen.struct.StructAttribute;
import java.util.Collections;

public final class GeneratorTests {

  public static void main(final String... ignoredArguments) {
    final PojoGenerator generator = PojoGeneratorFactory.create().getInstance();
    final GenerationProfile profile =
        GenerationProfile.create(
            Collections.emptyList(),
            Collections.singletonMap(GenerationContext.PROPERTY_NEW_LINE_PREFIX, "  "));

    final Struct model =
        Struct.newBuilder()
            .withName("Person")
            .addAttribute(StructAttribute.create("id", "long", true))
            .addAttribute(StructAttribute.create("name", "String", false))
            .addAttribute(StructAttribute.create("address", "Address", false))
            .create();

    System.out.println(generator.generate(model, profile));
  }
}
