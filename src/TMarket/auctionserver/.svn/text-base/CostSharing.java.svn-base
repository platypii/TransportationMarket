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

package TMarket.auctionserver;

import TMarket.common.*;

import java.util.*;


public class CostSharing {

  // Shared costs:
  public int n = 0; // number of users
  public double mc[] = new double[0]; // marginal cost
  public double alpha[] = new double[0]; // alpha
  public double tc[] = new double[1]; // tc[i] = total cost of users 0..i-1
  public double ta[] = new double[1]; // ta[i] = total alpha of users 0..i-1
  public double init[] = new double[0]; // initial shared cost
  public double sc[] = new double[0]; // shared cost


  // Returns the shared cost IF we added the user
  public double peek(double mc, double alpha) {
    double cost = mc; // coalition (n+1,n+1)
    // Coalition (i,n+1)
    for(int i = 0; i < n; i++) {
      double coal_cost = tc[n] - tc[i] + mc;
      double coal_alpha = ta[n] - ta[i] + alpha;
      double ccpa = coal_cost / coal_alpha;
      double cc = alpha * ccpa; // coalition cost
      cost = Math.max(cost, cc);
    }
    return cost;
  }

  // Adds the user to the cost sharing mechanism. returns their cost
  public double add(double new_mc, double new_alpha) {
    // Build list of cost/alpha
    double mc2[] = new double[n+1];
    double alpha2[] = new double[n+1];
    for(int i = 0; i < n; i++) {
      mc2[i] = mc[i];
      alpha2[i] = alpha[i];
    }
    mc2[n] = new_mc;
    alpha2[n] = new_alpha;
    this.set(mc2, alpha2);
    return sc[n-1];
  }

  // Sets the given marginal costs and alphas and recomputes shared costs
  public void set(double mc[], double alpha[]) {
    assert mc.length == alpha.length;
    n = mc.length;
    this.mc = mc;
    this.alpha = alpha;

    // Compute subtotals (1-indexed for convinience)
    tc = new double[n+1];
    ta = new double[n+1];
    tc[0] = 0;
    ta[0] = 0;
    for(int i = 0; i < n; i++) {
      tc[i+1] = tc[i] + mc[i];
      ta[i+1] = ta[i] + alpha[i];
    }

    // Compute shared costs
    init = new double[n]; // Initial cost
    sc = new double[n]; // Final cost

    for(int k = n-1; k >= 0; k--) {
      // Compute initial shared cost for user k
      init[k] = 0;
      for(int i = 0; i <= k; i++) {
        // Coalition from i thru k
        double coal_cost = tc[k+1] - tc[i];
        double coal_alpha = ta[k+1] - ta[i];
        double ccpa = coal_cost / coal_alpha;
        init[k] = Math.max(init[k], alpha[k] * ccpa);
      }

      // Compute shared cost for user k
      sc[k] = Double.POSITIVE_INFINITY;
      for(int j = k; j < n; j++) {
        double cpa = init[j] / alpha[j];
        sc[k] = Math.min(sc[k], alpha[k] * cpa);
      }
    }

  }

  // Returns the coalition cost per alpha for coalition (k1, k2)
  private double ccpa(int k1, int k2) {
    double coal_cost = tc[k2+1] - tc[k1];
    double coal_alpha = ta[k2+1] - ta[k1];
    return coal_cost / coal_alpha;
  }

  // Computes the costs based on mc[] and alpha[] with proportional online cost sharing
  // Exactly like the pseudocode:
  public static double[] pocs(double mc[], double alpha[]) {
    assert mc.length == alpha.length;
    int t = mc.length;
    assert t > 0;

    // Compute subtotals (1-indexed for convinience)
    double tc[] = new double[t+1];
    double ta[] = new double[t+1];
    tc[0] = 0;
    ta[0] = 0;
    for(int k = 0; k < t; k++) {
      tc[k+1] = tc[k] + mc[k];
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
        double ccpa = coal_cost / coal_alpha;
        cost_k[k] = Math.max(cost_k[k], alpha[k] * ccpa);
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

  public String toString() {
    if(n == 0) {
      return "[], total = 0.0";
    } else {
      double total = 0.0;
      String str = "[" + sc[0];

      for(int i = 1; i < n; i++) {
        if(sc[i-1] == sc[i])
          str += " " + sc[i];
        else
          str += "] [" + sc[i];

        total += sc[i];
      }

      str += "], total = " + String.format("%.2f", total);
      return str;
    }
  }

}

