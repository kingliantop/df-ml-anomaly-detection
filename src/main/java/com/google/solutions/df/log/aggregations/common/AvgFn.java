/*
 * Copyright 2019 Google LLC
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
package com.google.solutions.df.log.aggregations.common;

import java.io.Serializable;

import org.apache.beam.sdk.transforms.Combine.CombineFn;

public class AvgFn extends CombineFn<Long, AvgFn.Accum, Double> {

  public static class Accum implements Serializable {
    long sum = 0;
    long count = 0;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (count ^ (count >>> 32));
      result = prime * result + (int) (sum ^ (sum >>> 32));
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Accum other = (Accum) obj;
      if (count != other.count) return false;
      if (sum != other.sum) return false;
      return true;
    }
  }

  @Override
  public Accum createAccumulator() {
    return new Accum();
  }

  @Override
  public Accum addInput(Accum mutableAccumulator, Long input) {
    mutableAccumulator.sum += input;
    mutableAccumulator.count++;
    return mutableAccumulator;
  }

  @Override
  public Accum mergeAccumulators(Iterable<Accum> accumulators) {
    Accum merged = createAccumulator();
    for (Accum accum : accumulators) {
      merged.sum += accum.sum;
      merged.count += accum.count;
    }
    return merged;
  }

  @Override
  public Double extractOutput(Accum accumulator) {
    return ((double) accumulator.sum) / accumulator.count;
  }
}
