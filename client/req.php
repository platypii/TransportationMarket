<html>
  <head>
    <title>Transportation Market</title>
  </head>

  <body>

    <?php
       $req = "REQ"." ".$_POST["id"]." ".$_POST["start"]." ".$_POST["end"]." ".$_POST["earliest_pickup"]." ".$_POST["latest_pickup"]." ".$_POST["earliest_dropoff"]." ".$_POST["latest_dropoff"];
       echo "<tt>" . $req . "</tt>";
     ?>

    <form>
      <table>
	<tr style="background:#bbb">
	  <td />
	  <td>Shuttle</td>
	  <td>Cost</td>
	  <td>Distance</td>
	  <td>Travel Time</td>
	</tr>

	<?php
	   // TODO: Send REQ to auctionserver
	   $aserver = stream_socket_cllient("tcp://localhost:1337", $errno, $errstr, 30);
	   if(!$aserver) {
           echo "$errstr ($errno)<br />\n";
	   } else {
             fwrite($aserver, $req);
             while(!feof($aserver)) {
               $offer = fgets($aserver, 1024);
	       echo $offer
             }
             fclose($aserver);
	   }

	   // TODO: Display OFFERs

           // Parse the offer string and echo the table row
	   function parseOffer($offer) {
             sscanf($offer, "%s %s %s %s %s %s %s %s %s %s %s %s", $command, $shuttle, $consumer, $start, $end, $earliest_pickup, $latest_pickup, $earliest_dropoff, $latest_dropoff, $cost, $distance, $traveltime);
	     echo "<tr>";
             // TODO: offer + index
	     echo "<td><input type=\"radio\" name=\"offer\" value=\"offer0\" /></td>";
	     echo "<td>".$shuttle."</td>";
	     echo "<td>".$cost."</td>";
	     echo "<td>".$distance."</td>";
	     echo "<td>".$traveltime."</td>";
	     echo "</tr>";
	   }

	   function parseTime($time) {
             // TODO
	   }
         ?>
	</table>
      <input type="submit" value="Confirm Offer" />
    </form>
  </body>
</html>
