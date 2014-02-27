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

package TMarket.common;


// A class to track mean and variance
public class Stat {

  long n = 0;
  double total = 0.0;
  double mean = 0.0;
  double M2 = 0.0;

  private long exp = 2;


  public void addSample(double x) {
    // online mean and variance (thanks Knuth)
    n++;
    total += x;
    double delta = x - mean;
    mean = mean + delta / n;
    M2 = M2 + delta * (x - mean);

    // Logarithmic alerts
    if(n == exp) {
//      Debug.println("n = " + n);
      exp *= 2;
    }
  }

  public double total() {
    return total;
  }

  public double mean() {
    return mean;
  }

  public double var() {
    // Sample Variance:
    return M2 / n;

    // Population Variance:
//    return M2 / (n - 1);
  }

  public String toString() {
    return String.format("%.3f ± %.3f", mean, var());
//      return mean + " ± " + var();
  }

}