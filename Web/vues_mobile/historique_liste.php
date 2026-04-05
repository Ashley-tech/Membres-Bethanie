<?php
    $co =mysqli_connect("mysql-paris-bethanie.alwaysdata.net","299683","Gambetta","paris-bethanie_membre_test") or die("Erreur de connexion !");
    $response = array();
    if (!empty($_GET['compte'])){
        $c = $_GET['compte'];
        if ($co){
            $sth = mysqli_query($co, "SELECT id_historique, texte, date_historique, heure_historique, personne_concerne FROM historiquepersonne WHERE compte_concerne = $c AND supprime = false ORDER BY date_historique DESC, heure_historique DESC");
            if (mysqli_num_rows($sth) > 0){
                $rows = array();
                $response["historiques"] = array();
                while($r = mysqli_fetch_array($sth)) {
		    $rows['id'] = $r['id_historique'];
                    $rows["intitule"] = $r["texte"];
                    $rows["date"] = $r["date_historique"];
                    $rows["heure"] = $r["heure_historique"];
                    if (!empty($r['personne_concerne'])) {
                        $sth0 = mysqli_query($co, "SELECT sexe, nom, prenom FROM personne WHERE id_personne = ".$r['personne_concerne']);
                        while ($l = mysqli_fetch_array($sth0)){
                            $p = $l['sexe'].' '.$l['nom'].' '.$l['prenom'];
                        }
                    } else {
                        $p = 'Non';
                    }
                    $rows['personne'] = $p;
                    array_push($response["historiques"],$rows);
                }
                $response["success"]=1;
            } else {
                $response["success"]=0;
                $response["message"]="Aucune historique actuellement !";
            }
        }
    } else {
        $response["success"]=0;
        $response["message"]="Aucun entrée compte !";
    }
    echo json_encode($response);
?>