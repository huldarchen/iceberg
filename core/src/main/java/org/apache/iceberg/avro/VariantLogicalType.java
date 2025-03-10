/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iceberg.avro;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.iceberg.relocated.com.google.common.base.Preconditions;

public class VariantLogicalType extends LogicalType {
  static final String NAME = "variant";
  private static final VariantLogicalType INSTANCE = new VariantLogicalType();

  static VariantLogicalType get() {
    return INSTANCE;
  }

  private VariantLogicalType() {
    super(NAME);
  }

  @Override
  public void validate(Schema schema) {
    super.validate(schema);
    Preconditions.checkArgument(
        AvroSchemaUtil.isVariantSchema(schema), "Invalid variant record: %s", schema);
  }
}
