<?php
    	$co =mysqli_connect("mysql-paris-bethanie.alwaysdata.net","299683","Gambetta","paris-bethanie_membre_test") or die("Erreur de connexion !");
    	$response = array();
	if (!empty ($_GET['compte'])) {
		$c = $_GET['compte'];
    		if ($co){
        		$sth = mysqli_query($co, "SELECT login, mdp, profil_compte, date_creation, heure_creation FROM compte WHERE id_compte=$c");
        		if (mysqli_num_rows($sth) > 0){
            			$rows = array();
            			$response["compte"] = array();
            			while($r = mysqli_fetch_array($sth)) {
					$rows["login"] = $r["login"];
					$rows["mdp"] = $r['mdp'];
                			$rows["profil"] = $r["profil_compte"];
					$rows["date"] = $r["date_creation"];
					$rows["heure"] = $r["heure_creation"];

                			array_push($response["compte"],$rows);
            			}
            			$response["success"]=1;
        		} else {
            			$response["success"]=0;
            			$response["message"]="Aucun login actuellement !";
        		}
    		} else {
			$response["success"]=0;
            		$response["message"]="Aucune connexion !";
		}
	} else {
		$response["success"]=0;
            	$response["message"]="Aucune entrée !";
	}
    	echo json_encode($response);
?>