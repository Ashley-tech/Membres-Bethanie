<?php
    $co =mysqli_connect("mysql-paris-bethanie.alwaysdata.net","299683","Gambetta","paris-bethanie_membre_test") or die("Erreur de connexion !");
    $response = array();
    if ($co){
        $sth = mysqli_query($co, "SELECT id_personne, sexe, nom, prenom FROM personne WHERE statut_personne = 'Inscrit' ORDER BY nom, prenom");
        if (mysqli_num_rows($sth) > 0){
            $rows = array();
            $response["membres_inscrits"] = array();
            while($r = mysqli_fetch_array($sth)) {
		$rows["id"] = $r["id_personne"];
		if ($r['sexe'] == 'F') {
			$rows["sexe"] = "Mme";
		} else {
			$rows["sexe"] = "M.";
		}
                $rows["nom"] = $r["nom"];
                $rows["prenom"] = $r["prenom"];

                array_push($response["membres_inscrits"],$rows);
            }
            $response["success"]=1;
        } else {
            $response["success"]=0;
            $response["message"]="Aucun membre n'est inscrit actuellement !";
        }
    } else {
	$response["success"]=0;
            $response["message"]="Aucune connexion !";
    }
    echo json_encode($response);
?>