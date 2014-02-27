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



public class Time {

  public static char charArray[] = new char[5];

  // Parses a time string ("16:25") and returns minutes since midnight (985)
  public static int parseTime(String time_str) {
    String split[] = time_str.split(":");
    int days = (split.length == 3)? Integer.parseInt(split[0]) : 0;
    int hours = Integer.parseInt(split[split.length - 2]);
    int minutes = Integer.parseInt(split[split.length - 1]);
    return days * 24 * 60 + hours * 60 + minutes;
  }

  // Takes the time in minutes since midnight, and formats in 24hr time (00:00)
  public static String formatTime(int time) {
    assert 0 <= time;
    int days = time / (24 * 60);
    int hours = (time / 60) % 24;
    int minutes = time % 60;
    StringBuilder buffer = new StringBuilder();
    if(days > 0) {
      buffer.append(days);
      buffer.append(':');
    }
    if(hours < 10)
      buffer.append('0');
    buffer.append(hours);
    buffer.append(':');
    if(minutes < 10)
      buffer.append('0');
    buffer.append(minutes);
    return buffer.toString();
  }
  public static String formatTime(double time) {
    return formatTime((int)time);
  }

}

