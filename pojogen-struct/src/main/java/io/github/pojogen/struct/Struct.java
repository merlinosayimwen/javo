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

package io.github.pojogen.struct;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.pojogen.struct.util.ObjectChecks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Value-Object representation of a {@code Struct-Blueprint}.
 *
 * <p>The struct class is holding information about a defined {@code Struct-Blueprint} which follows
 * the {@code PojoGen Struct Specifications}. It may be parsed or simply created and is needed by
 * the {@code PojoGen-Generator} in order to generate a Pojo java file. Instances of this class are
 * immutable.
 *
 * @see StructAttribute
 */
public final class Struct {

  /** Name of the struct. */
  private final String name;

  /** The structs attributes. */
  private final Collection<StructAttribute> attributes;

  /** Flag that indicates whether the struct is "final". */
  private final boolean constant;

  /**
   * Parameterized constructor that initializes a struct with all available arguments.
   *
   * @param name The name that the struct will have.
   * @param attributes Iterable of attributes which will be given to the struct.
   * @param constant Flag that indicates whether the struct will be "final".
   */
  private Struct(
      final String name, final Iterable<StructAttribute> attributes, final boolean constant) {

    this.name = name;
    this.attributes = ImmutableList.copyOf(attributes);
    this.constant = constant;
  }

  /**
   * Returns the structs name.
   *
   * @return Name of the struct.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns a stream of the structs attributes.
   *
   * <p>A stream is returned to indicate, that the attributes can not be changed after construction
   * and prevents pitfalls.
   *
   * @return Stream of the structs attributes.
   */
  public Stream<StructAttribute> getAttributes() {
    return this.attributes.stream();
  }

  /**
   * Returns whether the structs is a constant.
   *
   * @return Whether the struct is constant.
   */
  public boolean isConstant() {
    return constant;
  }

  /**
   * Returns whether the struct is effectively immutable.
   *
   * <p>This is either {@code true}, if the {@code struct} is declared {@code const} or every of its
   * {@code attributes} are.
   *
   * @return Whether the struct is immutable.
   */
  public boolean isImmutable() {
    return isConstant() || this.attributes.stream().allMatch(StructAttribute::isConstant);
  }

  @Override
  public String toString() {
    final ToStringHelper representationBuilder =
        MoreObjects.toStringHelper(this)
            .add("name", this.name)
            .add("const", this.constant)
            .add("attributes", this.attributes.size());

    return representationBuilder.toString();
  }

  @Override
  public boolean equals(final Object other) {
    return ObjectChecks.equalsDefinitely(this, other).orElseGet(() -> deepEquals(other));
  }

  /**
   * Compares the attributes of both classes.
   *
   * @param other Instance who's attributes are compared to those of the invoked instance.
   * @return Whether the attributes of both instances are equal.
   */
  private boolean deepEquals(final Object other) {
    if (!(other instanceof Struct)) {
      return false;
    }

    final Struct otherStruct = (Struct) other;
    return (this.constant == otherStruct.constant)
        && (this.name.equals(otherStruct.name))
        && Arrays.deepEquals(this.attributes.toArray(), otherStruct.attributes.toArray());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.constant, Arrays.deepHashCode(this.attributes.toArray()));
  }

  public static final class Builder {

    private String name;
    private boolean constant;
    private Collection<StructAttribute> attributes;

    private Builder() {
      this.attributes = new ArrayList<>();
    }

    public Builder withName(final String name) {
      this.name = name;
      return this;
    }

    public Builder withConstant(final boolean constant) {
      this.constant = constant;
      return this;
    }

    public Builder withAttribute(final Collection<StructAttribute> attribute) {
      this.attributes = new ArrayList<>(attribute);
      return this;
    }

    public Builder addAttribute(final StructAttribute attribute) {
      this.ensureCollectionNotNull();
      this.attributes.add(attribute);
      return this;
    }

    private void ensureCollectionNotNull() {
      if (this.attributes == null) {
        this.attributes = new ArrayList<>();
      }
    }

    public Struct create() {
      Preconditions.checkNotNull(this.name);
      this.ensureCollectionNotNull();

      return new Struct(this.name, this.attributes, this.constant);
    }
  }

  /**
   * Creates a struct from just a name.
   *
   * @param name Name that is given to the struct.
   * @return Created struct with the given {@code name} and no attributes.
   */
  public static Struct create(final String name) {
    return Struct.create(name, Collections.emptyList());
  }

  /**
   * Creates a struct from a name and some attributes.
   *
   * @param name Name that is given to the struct.
   * @param attributes Iterable collection of attributes.
   * @return Created struct with the given {@code name} and {@code attributes}.
   */
  public static Struct create(final String name, final Iterable<StructAttribute> attributes) {
    return Struct.create(name, attributes, false);
  }

  /**
   * Creates a struct with all possible arguments.
   *
   * @param name Name that is given to the struct.
   * @param attributes Iterable collection of attributes.
   * @param constant Indicates whether the struct is "final".
   * @return Struct created from the given arguments.
   */
  public static Struct create(
      final String name, final Iterable<StructAttribute> attributes, final boolean constant) {

    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(attributes);

    return new Struct(name, attributes, constant);
  }

  /**
   * Creates a copy of the given struct and returns it.
   *
   * <p>Even though the class is immutable and acts as a VO but instances may still need to be
   * copied and this class-method allows to easily do that.
   *
   * @param struct Struct that is to be copied.
   * @return Copy of the {@code struct}.
   */
  public static Struct copyOf(final Struct struct) {
    Preconditions.checkNotNull(struct);

    return new Struct(struct.name, struct.attributes, struct.constant);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
}
