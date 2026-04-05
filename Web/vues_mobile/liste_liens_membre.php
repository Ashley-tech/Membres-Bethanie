<?php
    	$co =mysqli_connect("mysql-paris-bethanie.alwaysdata.net","299683","Gambetta","paris-bethanie_membre_test") or die("Erreur de connexion !");
    	$response = array();
	if (!empty ($_GET['membre'])) {
		$c = $_GET['membre'];
    	if ($co){
        	$sth = mysqli_query($co, "SELECT libelle FROM lienpersonne JOIN lienparente ON (lien = id_lien) WHERE personne=$c");
					$rows = array();
					$response["liens"] = array();
					if (mysqli_num_rows($sth) > 0){
						while($r = mysqli_fetch_array($sth)) {
							$rows["lien"] = $r["libelle"];
							array_push($response["liens"],$rows);
						}
					} else {
						$rows['lien'] = "Aucun";
						array_push($response["liens"],$rows);
					}
					
					$response["success"]=1;
    	} else {
			$response["success"]=0;
            $response["message"]="Aucune connexion !";
		}
	} else {
		$response["success"]=0;
        $response["message"]="L'identifiant du membre n'est pas renseigné !";
	}
    	echo json_encode($response);
?>