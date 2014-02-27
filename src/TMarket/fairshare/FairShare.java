/*
 * The Transportation Market
 *
 * Copyright 2011 Kenny Daniel
 *
 * The Transportation Market is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The Transportation Market is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * The Transportation Market.  If not, see <http://www.gnu.org/licenses/>.
 */

package TMarket.fairshare;

import java.util.*;


// TODO: Not clear what a coalition means in this context


public class FairShare {


//    Double A[] = {13.0, 28.0, 30.0, 33.0, 35.0, 49.0, 53.0, 66.0, 73.0, 86.0};
//    Double a[] = {13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0};
//    Double a[] = {13.0, 15.0,  2.0,  3.0,  2.0, 14.0,  4.0, 13.0,  7.0, 13.0};

//    Double A[] = {10.0, 20.0, 60.0, 65.0, 70.0};
//    Double a[] = {10.0, 10.0, 10.0, 10.0, 10.0};

//    Double A[] = {20.0, 40.0, 41.0, 42.0, 43.0};
//    Double a[] = {20.0, 10.0, 10.0, 10.0, 10.0};

//    Double A[] = {13.0, 28.0, 30.0, 33.0, 35.0};
//    Double a[] = {13.0, 13.0, 13.0, 13.0, 13.0};

//    Double A[] = {10.0, 12.0, 12.0};
//    Double a[] = {10.0, 10.0, 10.0};

    // consumer 3 should pay at least 20:
//    Double A[] = {20.0, 50.0, 60.0};
//    Double a[] = {20.0, 20.0, 20.0};


  // Computes the costs based on mc[] and alpha[] with non-uniform proportional cost sharing
  // Exactly like the pseudocode:
  public static double[] nonUniform(double cost[], double alpha[]) {
    assert cost.length == alpha.length;
    int t = cost.length;
    assert t > 0;

    // Compute subtotals (1-indexed for convinience)
    double tc[] = new double[t+1];
    double ta[] = new double[t+1];
    tc[0] = 0;
    ta[0] = 0;
    for(int k = 0; k < t; k++) {
      tc[k+1] = tc[k] + cost[k];
      ta[k+1] = ta[k] + alpha[k];
    }

    // Compute shared costs
    double cost_k[] = new double[t]; // Initial cost
    double cost_t[] = new double[t]; // Final cost

    for(int k = t-1; k >= 0; k--) {
      // Compute initial cost for user k
      cost_k[k] = 0;
      for(int i = 0; i <= k; i++) {
        // Coalition from i thru k
        double coal_cost = tc[k+1] - tc[i];
        double coal_alpha = ta[k+1] - ta[i];
        double cpa = coal_cost / coal_alpha;
        cost_k[k] = Math.max(cost_k[k], alpha[k] * cpa);
      }

      // Compute final cost for user k
      cost_t[k] = Double.POSITIVE_INFINITY;
      for(int j = k; j < t; j++) {
        double cpa = cost_k[j] / alpha[j];
        cost_t[k] = Math.min(cost_t[k], alpha[k] * cpa);
      }
    }

    return cost_t;
  }


  // Returns the initial offer prices
  public static double[] nonUniformInitial(double cost[], double alpha[]) {
    assert cost.length == alpha.length;
    int t = cost.length;
    assert t > 0;

    // Compute subtotals (1-indexed for convinience)
    double tc[] = new double[t+1];
    double ta[] = new double[t+1];
    tc[0] = 0;
    ta[0] = 0;
    for(int k = 0; k < t; k++) {
      tc[k+1] = tc[k] + cost[k];
      ta[k+1] = ta[k] + alpha[k];
    }

    // Compute shared costs
    double cost_k[] = new double[t]; // Initial cost
    double cost_t[] = new double[t]; // Final cost

    for(int k = t-1; k >= 0; k--) {
      // Compute initial cost for user k
      cost_k[k] = 0;
      for(int i = 0; i <= k; i++) {
        // Coalition from i thru k
        double coal_cost = tc[k+1] - tc[i];
        double coal_alpha = ta[k+1] - ta[i];
        double cpa = coal_cost / coal_alpha;
        cost_k[k] = Math.max(cost_k[k], alpha[k] * cpa);
      }
/*
      // Compute final cost for user k
      cost_t[k] = Double.POSITIVE_INFINITY;
      for(int j = k; j < t; j++) {
        double cpa = cost_k[j] / alpha[j];
        cost_t[k] = Math.min(cost_t[k], alpha[k] * cpa);
      }
*/
    }

    return cost_k;
  }



/*
  public String toString() {
    if(n == 0) {
      return "[], total = 0.0";
    } else {
      String str = "";
      double total = 0.0;

      for(int i = 1; i <= n; i++) {
        if(i == 1 || coal[i-1] != coal[i]) str += "[";
        str += String.format("%.2f", cost[i]);
        if(i == n || coal[i] != coal[i+1]) str += "]";
        str += " ";

        total += cost[i];
      }

      str += " total = " + String.format("%.2f", total);
      return str;
    }
  }
*/

}

