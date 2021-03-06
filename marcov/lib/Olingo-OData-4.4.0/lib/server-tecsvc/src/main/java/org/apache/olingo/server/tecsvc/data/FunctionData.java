/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.tecsvc.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.tecsvc.data.DataProvider.DataProviderException;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;

public class FunctionData {

  protected static EntityCollection entityCollectionFunction(final String name,
      final Map<String, Parameter> parameters, final Map<String, EntityCollection> data)
      throws DataProviderException {
    if (name.equals("UFCRTCollETTwoKeyNavParam")) {
      final List<Entity> esTwoKeyNav = data.get("ESTwoKeyNav").getEntities();
      EntityCollection result = new EntityCollection();
      final int endIndex = parameters.isEmpty() ? 0 : getParameterInt16(parameters);
      result.getEntities().addAll(
          esTwoKeyNav.subList(0,
              endIndex < 0 ? 0 : endIndex > esTwoKeyNav.size() ? esTwoKeyNav.size() : endIndex));
      return result;
    } else if (name.equals("UFCRTCollETMixPrimCollCompTwoParam")) {
      return data.get("ESMixPrimCollComp");
    } else if (name.equals("UFCRTCollETMedia")) {
      return data.get("ESMedia");
    } else {
      throw new DataProviderException("Function " + name + " is not yet implemented.",
          HttpStatusCode.NOT_IMPLEMENTED);
    }
  }

  protected static Entity entityFunction(final String name, final Map<String, Parameter> parameters,
      final Map<String, EntityCollection> data) throws DataProviderException {
    final List<Entity> esTwoKeyNav = data.get("ESTwoKeyNav").getEntities();
    if (name.equals("UFCRTETKeyNav")) {
      return data.get("ESKeyNav").getEntities().get(0);
    } else if (name.equals("UFCRTETTwoKeyNav")) {
      return esTwoKeyNav.get(0);
    } else if (name.equals("UFCRTETTwoKeyNavParam")) {
      final int index = parameters.isEmpty() ? 0 : getParameterInt16(parameters);
      return index < 0 || index >= esTwoKeyNav.size() ? null : esTwoKeyNav.get(index);
    } else if (name.equals("UFCRTETMedia")) {
      final int index = parameters.isEmpty() ? 1 : getParameterInt16(parameters);
      final List<Entity> esMedia = data.get("ESMedia").getEntities();
      return index < 1 || index > esMedia.size() ? null : esMedia.get(index - 1);
    } else if (name.equals("UFCRTETTwoKeyNavParamCTTwoPrim")) {
      final List<Property> parameterProperties = parameters.get("ParameterCTTwoPrim").asComplex().getValue();
      if (parameterProperties == null || parameterProperties.size() < 2) {
        return null;
      }
      final Short parameterInt16 = (Short) parameterProperties.get(0).asPrimitive();
      final String parameterString = (String) parameterProperties.get(1).asPrimitive();
      for (final Entity entity : esTwoKeyNav) {
        if (entity.getProperty("PropertyInt16").asPrimitive().equals(parameterInt16)
            && entity.getProperty("PropertyString").asPrimitive().equals(parameterString)) {
          return entity;
        }
      }
      return null;
    } else {
      throw new DataProviderException("Function " + name + " is not yet implemented.",
          HttpStatusCode.NOT_IMPLEMENTED);
    }
  }

  @SuppressWarnings("unchecked")
  protected static Property primitiveComplexFunction(final String name, final Map<String, Parameter> parameters,
      final Map<String, EntityCollection> data) throws DataProviderException {
    if (name.equals("UFNRTInt16")) {
      return DataCreator.createPrimitive(name, (short) 12345);
    } else if (name.equals("UFCRTString")) {
      return DataCreator.createPrimitive(name, "UFCRTString string value");
    } else if (name.equals("UFCRTCollString")) {
      return data.get("ESCollAllPrim").getEntities().get(0).getProperty("CollPropertyString");
    } else if (name.equals("UFCRTCTTwoPrim")) {
      return DataCreator.createComplex(name,
          ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
          DataCreator.createPrimitive("PropertyInt16", (short) 16),
          DataCreator.createPrimitive("PropertyString", "UFCRTCTTwoPrim string value"));
    } else if (name.equals("UFCRTCTTwoPrimParam")) {
      return DataCreator.createComplex(name,
          ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
          DataCreator.createPrimitive("PropertyInt16", getParameterInt16(parameters)),
          DataCreator.createPrimitive("PropertyString", getParameterString(parameters)));
    } else if (name.equals("UFCRTCollCTTwoPrim")) {
      return DataCreator.createComplexCollection(name,
          ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
          Arrays.asList(DataCreator.createPrimitive("PropertyInt16", (short) 16),
              DataCreator.createPrimitive("PropertyString", "Test123")),
          Arrays.asList(DataCreator.createPrimitive("PropertyInt16", 17),
              DataCreator.createPrimitive("PropertyString", "Test456")),
          Arrays.asList(DataCreator.createPrimitive("PropertyInt16", 18),
              DataCreator.createPrimitive("PropertyString", "Test678")));
    } else if (name.equals("UFCRTStringTwoParam")) {
      final String parameterString = getParameterString(parameters);
      // ParameterString is not provided
      if (parameterString == null) {
        return DataCreator.createPrimitive(name, null);
      } else {
        final Short parameterInt16 = getParameterInt16(parameters);
        final StringBuilder builder = new StringBuilder();
        // if parameterInt16 <= 0 return an empty string
        for (short i = parameterInt16; i > 0; i--) {
          if (builder.length() != 0) {
            builder.append(',');
          }
          builder.append('"')
              .append(parameterString)
              .append('"');
        }
        return DataCreator.createPrimitive(name, builder.toString());
      }
    } else if (name.equals("UFCRTCollCTTwoPrimTwoParam")) {
      final Short parameterInt16 = getParameterInt16(parameters);
      final String parameterString = getParameterString(parameters);
      if (parameterString == null) {
        return DataCreator.createComplexCollection(name,
            ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
            Arrays.asList(DataCreator.createPrimitive("PropertyInt16", 1),
                DataCreator.createPrimitive("PropertyString", name + " int16 value: " + parameterInt16)),
            Arrays.asList(DataCreator.createPrimitive("PropertyInt16", 2),
                DataCreator.createPrimitive("PropertyString", name + "string value: null")));
      } else {
        List<ComplexValue> complexValues = new ArrayList<ComplexValue>();
        short counter = 1;
        for (short i = parameterInt16; 0 < i; i--) {
          ComplexValue complexValue = new ComplexValue();
          complexValue.getValue().add(new Property(null, "PropertyInt16", ValueType.PRIMITIVE, counter++));
          complexValue.getValue().add(new Property(null, "PropertyString", ValueType.PRIMITIVE, 
              name + " string value: " + parameterString));
          complexValues.add(complexValue);
        }
        return new Property(null, name, ValueType.COLLECTION_COMPLEX, complexValues);
      }
    } else if (name.equals("UFNRTByteNineParam")) {
      short count = 0;  // counts non-empty parameters
      for (final Parameter parameter : parameters.values()) {
        if (!(parameter.isNull()
            || !parameter.isCollection()
                && (parameter.isComplex() && parameter.asComplex().getValue().isEmpty()
                    || parameter.isEntity() && ((Entity) parameter.getValue()).getProperties().isEmpty())
            || parameter.isCollection()
                && (parameter.isEntity() && ((EntityCollection) parameter.getValue()).getEntities().isEmpty()
                    || !parameter.isEntity() && parameter.asCollection().isEmpty()))) {
          count++;
        }
      }
      return DataCreator.createPrimitive(null, count);
    } else {
      throw new DataProviderException("Function " + name + " is not yet implemented.",
          HttpStatusCode.NOT_IMPLEMENTED);
    }
  }

  private static Short getParameterInt16(final Map<String, Parameter> parameters) {
    return parameters.containsKey("ParameterInt16") ? (Short) parameters.get("ParameterInt16").getValue() : null;
  }

  private static String getParameterString(final Map<String, Parameter> parameters) {
    return parameters.containsKey("ParameterString") ? (String) parameters.get("ParameterString").getValue() : null;
  }
}
