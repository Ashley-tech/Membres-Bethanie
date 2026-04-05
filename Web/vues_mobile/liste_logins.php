<?php
    $co =mysqli_connect("mysql-paris-bethanie.alwaysdata.net","299683","Gambetta","paris-bethanie_membre_test") or die("Erreur de connexion !");
    $response = array();
    if ($co){
        $sth = mysqli_query($co, "SELECT login FROM compte ORDER BY login");
        if (mysqli_num_rows($sth) > 0){
            $rows = array();
            $response["logins"] = array();
            while($r = mysqli_fetch_array($sth)) {
                $rows["login"] = $r["login"];

                array_push($response["logins"],$rows);
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
    echo json_encode($response);
?>