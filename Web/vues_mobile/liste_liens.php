<?php
    $co =mysqli_connect("mysql-paris-bethanie.alwaysdata.net","299683","Gambetta","paris-bethanie_membre_test") or die("Erreur de connexion !");
    $response = array();
    if ($co){
        $sth = mysqli_query($co, "SELECT libelle FROM lienparente ORDER BY libelle");
        if (mysqli_num_rows($sth) > 0){
            $rows = array();
            $response["liens"] = array();
            while($r = mysqli_fetch_array($sth)) {
		$rows["lien"] = $r['libelle'];

                array_push($response["liens"],$rows);
            }
            $response["success"]=1;
        } else {
            $response["success"]=0;
            $response["message"]="Aucun lien de parenté actuellement !";
        }
    } else {
	$response["success"]=0;
            $response["message"]="Aucune connexion !";
    }
    echo json_encode($response);
?>